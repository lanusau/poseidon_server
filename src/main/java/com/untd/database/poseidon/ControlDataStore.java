package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 * This class groups static method to get control information from the 
 * control database
 *
 */

public class ControlDataStore {	

	static private String jdbcURL,username,password;
	static private Connection controlConnection;
	static private int maxConnectionRetryCount;

	/**
	 * 
	 * Initialize control database data store
	 * 
	 * @param paramJdbcURL JDBC URL for control database
	 * @param paramUsername username for control database 
	 * @param paramPassword password for control database
	 * @param prop properties
	 * @throws SQLException
	 */
	public static void init(
			String paramJdbcURL, 
			String paramUsername, 
			String paramPassword) throws SQLException{
		
		jdbcURL = paramJdbcURL;
		username = paramUsername;
		password = paramPassword;
		

		maxConnectionRetryCount = PoseidonConfiguration.getConfiguration().getInt("maxConnectionRetryCount");
		
		connect();
	}
	
	/**
	 * Connect to control database
	 * @throws SQLException
	 */
	private static void connect() throws SQLException{
		int try_count = 0;
		SQLException exception_CanNotConnect;
		String sqlErrorMessage;
		
		sqlErrorMessage = ""; 
		
		controlConnection = ping(controlConnection);
		
		if (controlConnection == null) {
			while ((controlConnection == null) && (try_count++ < maxConnectionRetryCount) ) {
				try {
					controlConnection = DriverManager.getConnection(
							jdbcURL,
							username,
							password);		
					controlConnection.setAutoCommit(false);				
				} catch (SQLException e) {
					sqlErrorMessage = e.getMessage();
					// Do nothing - just sleep and keep trying to reconnect
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {						
					}
				}				
			}								
		}		
		
		// If connection is still null, raise exception
		if (controlConnection == null) {
			exception_CanNotConnect = new SQLException("Can not connect to the control database:"+sqlErrorMessage); 
			throw  exception_CanNotConnect;
		}

	}
	
	/**
	 * 
	 * Ping connection to check its health. Return original connection if success,
	 * otherwise return null
	 * @param conn JDBC connection
	 * 
	 */
	public static Connection ping(Connection conn) {
		PreparedStatement st;
		ResultSet rs;
				
		if (conn == null) return null;
		
		try {
			// pinging for MySQL
			st = conn.prepareStatement(SqlText.getString("ControlDataStore.pingSQL"));
			rs = st.executeQuery();
			
			while (rs.next());
			
			rs.close();
			st.close();
			
			return conn;
		} catch (SQLException e) {
			// Something is not right with this connection, try to close it			
			try {
				conn.close();
			} catch (SQLException e1){
				// Do nothing - we trying to close it
			}
			return null;
		}
	}
	
	/**
	 * Build new script object using provided script id
	 * @param scriptId script id
	 * @return {@link Script}
	 * @throws SQLException
	 */
	synchronized public static Script getScript(int scriptId) throws SQLException{
		connect();
		return new Script(controlConnection,scriptId);
	}
	
	/**
	 * Get a list of active scripts for this server
	 * @param serverId server id
	 * @return array list of scripts
	 * @throws SQLException
	 */
	public static ArrayList<Script> getActiveScriptList(int serverId) throws SQLException {
		ArrayList<Script> scriptList;
		PreparedStatement st;
		ResultSet rs;
		
		connect();
		
		scriptList = new ArrayList<Script>();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.activeScriptSQL")); 
		
		st.setInt(1,serverId);
		st.setInt(2,serverId);
		rs = st.executeQuery();
		while (rs.next()) {
			scriptList.add(new Script(rs));
		}
		
		rs.close();
		st.close();
		
		
		return scriptList;
	}
	
	/**
	 * Get a list of inactive scripts for this server id
	 * @param serverId server id
	 * @return array list of scripts
	 * @throws SQLException
	 */
	public static ArrayList<Script> getInactiveScriptList(int serverId) throws SQLException {
		ArrayList<Script> scriptList;
		PreparedStatement st;
		ResultSet rs;
		
		connect();
		
		scriptList = new ArrayList<Script>();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.inactiveScriptSQL"));
		
		st.setInt(1,serverId);
		st.setInt(2,serverId);
		rs = st.executeQuery();
		while (rs.next()) {
			scriptList.add(new Script(rs));
		}
		
		rs.close();
		st.close();
		
		
		return scriptList;
	}
	
	/**
	 * Get generated key value 
	 * @return generated key
	 * @throws SQLException
	 */
	private static int getKeyFromStatement(Statement st) throws SQLException{
		
		ResultSet rs;
		int keyValue;
		
		rs = st.getGeneratedKeys();
		
		if (rs.next()){
			keyValue = rs.getInt(1);
		} else {
			throw new SQLException("Can not obtain AUTOINCREMENT column value"); 
		}
		
		rs.close();
		
		return keyValue;		
		
	}
	
	/**
	 * Log start of the script
	 * @param script script object
	 * @return id of master log entry that can be used later to log subsequent operations
	 * @throws SQLException
	 */
	synchronized public static int logScriptStart(Script script) throws SQLException {
		int scriptLogId;
		PreparedStatement st;
		
		
		connect();
	
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.logScriptStartSQL"), Statement.RETURN_GENERATED_KEYS);		 
		st.setInt(1,script.getScript_id());
		st.setShort(2,ExecutionResult.RESULT_NOT_FINISHED);
		st.execute();
		
		// Get AUTOINCREMENT column value 
		scriptLogId = getKeyFromStatement(st);
		
		controlConnection.commit();
		
		st.close();
		
		return scriptLogId;
	}
	
	/**
	 * Log start of the script on particular target
	 * @param scriptLogId master log id that was received from {@link #logScriptStart(Script)}
	 * @param target
	 * @return if of the log entry in the script target log table
	 * @throws SQLException
	 */
	synchronized public static int logScriptTargetStart(int scriptLogId,Target target) throws SQLException {
		int scriptTargetLogId;
		PreparedStatement st;
		
		connect();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.logScriptTargetStartSQL"), Statement.RETURN_GENERATED_KEYS);		 
		st.setInt(1,scriptLogId);
		st.setInt(2,target.getTarget_id());
		st.setShort(3,ExecutionResult.RESULT_NOT_FINISHED);
		st.execute();
		
		// Get AUTOINCREMENT column value 
		scriptTargetLogId = getKeyFromStatement(st);
		controlConnection.commit();
		
		st.close();
		
		return scriptTargetLogId;
	}
	

	/**
	 * Log end of a script execution
	 * @param script script
	 * @param scriptLogId master log id that was received from {@link #logScriptStart(Script)}
	 * @throws SQLException
	 */
	synchronized public static void logScriptEnd(Script script,int scriptLogId) throws SQLException {
		
		PreparedStatement st;		
		
		connect();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.updateScriptLogSQL")); 
		st.setShort(1,ExecutionResult.RESULT_FINISHED);
		if (script.hasErrorOccured()) {
			st.setInt(2,1);
		} else {
			st.setInt(2,0);
		}
		if (script.isTriggered()) {
			st.setInt(3,1);
		} else {
			st.setInt(3,0);
		}
		
		st.setInt(4,scriptLogId);
		
		st.execute();
		controlConnection.commit();
		
		st.close();
	}
	
	/**
	 * Log script miss-fire.
	 * Miss-fire happens when script was supposed to run but didn't, most likely due to 
	 * lack of available Quartz threads
	 * @param script script
	 * @throws SQLException
	 */
	synchronized public static void logScriptMissfire(Script script) throws SQLException {		
		PreparedStatement st;
		
		connect();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.logScriptMissfireSQL"));		
		st.setInt(1,script.getScript_id());
		st.setShort(2,ExecutionResult.RESULT_MISSFIRED);
		st.execute();
		
		controlConnection.commit();
		
		st.close();		
	}
	
	/**
	 * Log script timeout.
	 * Timeout happens when script takes too long to execute.
	 * @param script script
	 * @param scriptLogId master log id that was received from {@link #logScriptStart(Script)}
	 * @throws SQLException
	 */
	synchronized public static void logScriptTimeout(Script script,int scriptLogId) throws SQLException {
		
		PreparedStatement st;		
		
		connect();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.updateScriptLogSQL")); 
		st.setShort(1,ExecutionResult.RESULT_TIMEDOUT);
		st.setInt(2,1);
		st.setInt(3,0);		
		st.setInt(4,scriptLogId);
		
		st.execute();
		controlConnection.commit();
		
		st.close();
	}
	
	/**
	 * Log end of execution of script on particular target
	 * @param thread execution thread
	 * @throws SQLException
	 */
	synchronized public static void logScriptTargetEnd(ExecutionThread thread) throws SQLException {
		
		int scriptTargetLogId;
		ExecutionResult result;
		PreparedStatement st, stRow,stCol;
		
		int scriptTargetRowLogId;
		int rowNum,colNum;
		
		scriptTargetLogId = thread.getScriptTargetLogId();
		result = thread.getExecutionResult();
		
		connect();
		
		// Update log record for this target
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.updateScriptTargetLogSQL")); 
		st.setString(1,result.getResultErrorMsg());
		st.setShort(2,result.getResultCode());
		st.setShort(3,result.getSeverity());
		st.setInt(4,scriptTargetLogId);
		st.execute();
		st.close();
		
		// Dump result set
		// Prepare statements for inserting data
		stRow = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.logScriptRowSQL"), Statement.RETURN_GENERATED_KEYS); 
		stCol = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.logScriptColSQL")); 

		rowNum = 0;
		for (final ExecutionResultRow row : result.getRows()) {	
			
			stRow.setInt(1,scriptTargetLogId);
			stRow.setInt(2,rowNum);
			stRow.setShort(3,row.getExpressionResult());
			stRow.setString(4,row.getExpressionErrorMsg());
			stRow.setShort(5,row.getExpressionSeverity());
			stRow.execute();
			
			// Get AUTOINCREMENT column value 
			scriptTargetRowLogId = getKeyFromStatement(stRow);
			
			rowNum++;
			
			// Dump columns			
			colNum = 0;
			for (String columnValue : row.getColumns()) {			
				
				stCol.setInt(1,scriptTargetRowLogId);
				stCol.setInt(2,colNum);
				stCol.setString(3,columnValue);
				stCol.execute();
				colNum++;
			}
		}
		
		stRow.close();
		stCol.close();
		controlConnection.commit();
	}
	
	/**
	 * Log server heart beat.
	 * Every so often, Poseidon server logs heart beat to the control database
	 * by updating heartbeat_sysdate column in psd_server table. This allows
	 * to have external monitor (monitor the monitoring :-) 
	 * @param serverId our server id
	 * @throws SQLException
	 */
	public static void logHeartbeat(int serverId) throws SQLException {
		
		PreparedStatement st;		
		
		connect();
		
		st = controlConnection.prepareStatement(SqlText.getString("ControlDataStore.logHeartbeatSQL")); 		
		st.setInt(1,serverId);
		
		st.execute();
		controlConnection.commit();
		
		st.close();
	}
}
