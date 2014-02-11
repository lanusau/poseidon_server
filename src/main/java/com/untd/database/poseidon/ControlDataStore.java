package com.untd.database.poseidon;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.untd.database.poseidon.data.Script;
import com.untd.database.poseidon.data.ScriptLog;
import com.untd.database.poseidon.data.ScriptLogMapper;
import com.untd.database.poseidon.data.ScriptMapper;
import com.untd.database.poseidon.data.ScriptTargetColLog;
import com.untd.database.poseidon.data.ScriptTargetLog;
import com.untd.database.poseidon.data.ScriptTargetLogMapper;
import com.untd.database.poseidon.data.ScriptTargetRowLog;
import com.untd.database.poseidon.data.ServerMapper;
import com.untd.database.poseidon.data.Target;
import com.untd.database.poseidon.data.TargetMapper;


/**
 * This class groups static method to query/update control information from the 
 * control database
 *
 */

public class ControlDataStore {	
	
	/**
	 * MyBatis Session Factory
	 */
	private static SqlSessionFactory sqlSessionFactory;


	/**
	 * 
	 * Initialize control database data store
	 * 
	 * @throws IOException 
	 */
	public static void init(Properties prop) throws IOException{
		
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,prop);
	}
		
	
	/**
	 * Return script using provided script id
	 * @param scriptId script id
	 * @return {@link Script}
	 * @throws SQLException
	 */
	synchronized public static Script getScript(int scriptId) {		
		Script script;
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptMapper mapper = session.getMapper(ScriptMapper.class);
			script = mapper.select(scriptId);
		} finally {
		    session.close();
		}
		
		return script;
	}
	
	/**
	 * Get a list of active scripts for this server
	 * @param serverId server id
	 * @return array list of scripts
	 */
	public static List<Script> getActiveScriptList(int serverId) {
		List<Script> scriptList;
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptMapper mapper = session.getMapper(ScriptMapper.class);
			scriptList =  mapper.selectActive(serverId);
		} finally {
		    session.close();
		}
		
		return scriptList;
	}
	
	/**
	 * Get a list of inactive scripts for this server
	 * @param serverId server id
	 * @return array list of scripts
	 */
	public static List<Script> getInactiveScriptList(int serverId) {
		List<Script> scriptList;
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptMapper mapper = session.getMapper(ScriptMapper.class);
			scriptList = mapper.selectInactive(serverId);
		} finally {
		    session.close();
		}
		
		return scriptList;
	}	
	
	/**
	 * Return notifications assigned for the script for particular severity
	 * @param script - Script
	 * @param severity - severity
	 * @return list of notifications
	 */
	synchronized public static List<Notification> getScriptNotifications(Script script, short severity) {
        ArrayList<Notification> notificationList = new ArrayList<Notification>();
        
        SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptMapper mapper = session.getMapper(ScriptMapper.class);
			// Get notifications assigned through groups
			for (final Notification n : mapper.selectGroupNotifications(script.getScriptId())) {
				if (n.getSeverity() == severity) {
					notificationList.add(n);
				}
			}
			// Get directly assigned notifications
			for (final Notification n : mapper.selectPersonNotifications(script.getScriptId())) { 
				n.setSeverity(severity);
				notificationList.add(n);
			}
		} finally {
		    session.close();
		}
		
		return notificationList;
	}
	
	
	/**
	 * Log start of the script
	 * @param script script object
	 * @return ScriptLog record
	 */
	synchronized public static ScriptLog logScriptStart(Script script){
		
		ScriptLog scriptLog = new ScriptLog();
		scriptLog.setScriptId(script.getScriptId());
		scriptLog.setServerId(PoseidonServer.serverId);
		scriptLog.setStatusNumber(new Integer(ExecutionResult.RESULT_NOT_FINISHED));
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptLogMapper mapper = session.getMapper(ScriptLogMapper.class);
			mapper.insert(scriptLog);
			session.commit();
		} finally {
		    session.close();
		}
		
		return scriptLog;

	}
	
	/**
	 * Log start of the script on particular target
	 * @param scriptLog  Script Log record that was received from {@link #logScriptStart(Script)}
	 * @param target
	 * @return ScriptTargetLog record
	 */
	synchronized public static ScriptTargetLog logScriptTargetStart(ScriptLog scriptLog,Target target) {
		ScriptTargetLog scriptTargetLog = new ScriptTargetLog();
		scriptTargetLog.setScriptLogId(scriptLog.getScriptLogId());
		scriptTargetLog.setTargetId(target.getTargetId());
		scriptTargetLog.setStatusNumber(new Integer(ExecutionResult.RESULT_NOT_FINISHED));
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptTargetLogMapper mapper = session.getMapper(ScriptTargetLogMapper.class);
			mapper.insert(scriptTargetLog);
			session.commit();
		} finally {
		    session.close();
		}
		
		return scriptTargetLog;
	}
	

	/**
	 * Log end of a script execution
	 * @param script script
	 * @param scriptLog ScriptLog that was received from {@link #logScriptStart(Script)}
	 */
	synchronized public static void logScriptEnd(Script script,ScriptLog scriptLog) {
		

		scriptLog.setStatusNumber(new Integer(ExecutionResult.RESULT_FINISHED));
		
		if (script.hasErrorOccured()) {
			scriptLog.setErrorStatusCode(1);			
		} else {
			scriptLog.setErrorStatusCode(0);
		}
		if (script.isTriggered()) {
			scriptLog.setTriggerStatusCode(1);			
		} else {
			scriptLog.setTriggerStatusCode(0);
		}
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptLogMapper mapper = session.getMapper(ScriptLogMapper.class);
			mapper.update(scriptLog);
			session.commit();
		} finally {
		    session.close();
		}
		
	}
	
	/**
	 * Log script miss-fire.
	 * Miss-fire happens when script was supposed to run but didn't, most likely due to 
	 * lack of available Quartz threads
	 * @param script script
	 */
	synchronized public static void logScriptMissfire(Script script)  {	
		ScriptLog scriptLog = new ScriptLog();
		scriptLog.setScriptId(script.getScriptId());
		scriptLog.setServerId(PoseidonServer.serverId);
		scriptLog.setStatusNumber(new Integer(ExecutionResult.RESULT_MISSFIRED));
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptLogMapper mapper = session.getMapper(ScriptLogMapper.class);
			mapper.insert(scriptLog);
			session.commit();
		} finally {
		    session.close();
		}		
	}
	
	/**
	 * Log script timeout.
	 * Timeout happens when script takes too long to execute.
	 * @param script script
	 * @param scriptLogId ScriptLog that was received from {@link #logScriptStart(Script)}
	 */
	synchronized public static void logScriptTimeout(Script script,ScriptLog scriptLog) {
		scriptLog.setStatusNumber(new Integer(ExecutionResult.RESULT_TIMEDOUT));
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptLogMapper mapper = session.getMapper(ScriptLogMapper.class);
			mapper.update(scriptLog);
			session.commit();
		} finally {
		    session.close();
		}		
	}
	

	/**
	 * @param scriptTargetLog - ScriptTargetLog that was received from {@link #logScriptTargetStart(ScriptLog ,Target)}
	 * @param result - Execution result
	 */
	synchronized public static void logScriptTargetEnd(ScriptTargetLog scriptTargetLog,ExecutionResult result)  {		
		
		int rowNum,colNum;
		
		scriptTargetLog.setStatusNumber(new Integer(result.getResultCode()));
		scriptTargetLog.setErrorMessage(result.getResultErrorMsg());
		scriptTargetLog.setSeverity(new Integer(result.getSeverity()));

		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ScriptTargetLogMapper mapper = session.getMapper(ScriptTargetLogMapper.class);
			mapper.update(scriptTargetLog);
			
			// Dump rows
			rowNum = 0;
			for (final ExecutionResultRow row : result.getRows()) {
				
				ScriptTargetRowLog rowLog = new ScriptTargetRowLog();				
				rowLog.setScriptTargetLogId(scriptTargetLog.getScriptTargetLogId());
				rowLog.setRowNumber(rowNum);
				rowLog.setExpressionResult(row.getExpressionResult());
				rowLog.setExpressionErrorMessage(row.getExpressionErrorMsg());
				rowLog.setSeverity(row.getExpressionSeverity());
				mapper.insertRow(rowLog);
				
				// Dump columns			
				colNum = 0;
				for (String columnValue : row.getColumns()) {	
					ScriptTargetColLog colLog = new ScriptTargetColLog();
					
					colLog.setScriptTargetRowLogId(rowLog.getScriptTargetRowLogId());
					colLog.setColumnNumber(colNum);
					colLog.setColumnValue(columnValue);
					
					mapper.insertColumn(colLog);
					
					colNum++;
				}
				
				rowNum++;
			}
			session.commit();
		} finally {
		    session.close();
		}
		
	}
	
	/**
	 * Get a list of targets that particular script should run on
	 * @param script - Script
	 * @return list of targets
	 */
	synchronized public static List<Target> getScriptTargets(Script script, int serverId) {
		ArrayList<Target> targetList = new ArrayList<Target>();
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			TargetMapper mapper = session.getMapper(TargetMapper.class);
			
			// Only include targets that are assigned to this server
			for (final Target t : mapper.selectScriptTargets(script.getScriptId())) {
				if (t.getServerId() == serverId) {
					targetList.add(t);
				}
			}			
		} finally {
		    session.close();
		}
		
		return targetList;
	}
	
	/**
	 * Log server heart beat.
	 * Every so often, Poseidon server logs heart beat to the control database
	 * by updating heartbeat_sysdate column in psd_server table. This allows
	 * to have external monitor (monitor the monitoring :-) 
	 * @param serverId our server id
	 */
	public static void logHeartbeat(int serverId) {
		
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ServerMapper mapper = session.getMapper(ServerMapper.class);
			mapper.heartbeat(serverId);
			session.commit();
		} finally {
		    session.close();
		}	
	}
}
