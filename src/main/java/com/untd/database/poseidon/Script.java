package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Object with information about script
 *
 */
public class Script {
	
	/**
	 * Low severity
	 */
	public static final short SEVERITY_LOW = 3;
	/**
	 * Medium severity
	 */
	public static final short SEVERITY_MED = 2;
	/**
	 * High severity
	 */
	public static final short SEVERITY_HIGH = 1;
	/**
	 * Calculated severity
	 */
	public static final short SEVERITY_CALCULATE = 0;
	
	/**
	 * This script runs SQL queries
	 */
	public static int QUERY_TYPE_SQL = 1;
	/**
	 * This script runs PLSQL queries
	 */
	public static int QUERY_TYPE_PLSQL = 2;
	
	/**
	 * Value for Text messages
	 */
	public static final int MESSAGE_FORMAT_TEXT = 0;
	/**
	 * Value for HTML messages
	 */
	public static final int MESSAGE_FORMAT_HTML = 1;
	
		
	private int script_id;
	private String name;
	private String description;
	private String schedule_min;
	private String schedule_hour;
	private String schedule_day;
	private String schedule_month;
	private String schedule_week;
	private int query_type;
	private String query_text;
	private int timeout_sec;
	private short fixed_severity;
	private int severity_column_position;
	private int value_med_severity;
	private int value_high_severity;
	private int message_format;
	private String expression_text;
	private String message_subject;
	private String message_header;
	private String message_text_str;
	private String message_footer;
	private String status_code;
	private Timestamp create_sysdate;
	private Timestamp update_sysdate;
	  
	private ArrayList<Target> targets;
	private ArrayList<Notification> notificationList;
	
	private boolean errorOccured;
	private boolean triggered;	
	private short executionSeverity;
	
	/**
	 * Construct new Script object by querying data in the control database.
	 * 
	 * @param conn established JDBC connection
	 * @param scriptId script id
	 * @throws SQLException 
	 */
	public Script(Connection conn, int scriptId) throws SQLException {
		PreparedStatement st;
		ResultSet rs;
		SQLException exception_scriptNotFound;
		Target target;
		
		
		st = conn.prepareStatement(SqlText.getString("Script.getScriptSQL"));
		
		// Query control database for this script
		st.setInt(1,scriptId);
		rs = st.executeQuery();
		if (! rs.next()) {			
			exception_scriptNotFound = new SQLException("Can not find script id:"+scriptId);
			throw exception_scriptNotFound;
		}
		
		// Set various variables based on column values
		script_id = rs.getInt("script_id");
		name  = rs.getString("name");
		description = rs.getString("description");
		schedule_min = rs.getString("schedule_min");
		schedule_hour = rs.getString("schedule_hour");
		schedule_day = rs.getString("schedule_day");
		schedule_month = rs.getString("schedule_month");
		schedule_week = rs.getString("schedule_week");
		query_type = rs.getInt("query_type");
		query_text = rs.getString("query_text");
		timeout_sec = rs.getInt("timeout_sec");
		fixed_severity = rs.getShort("fixed_severity");
		severity_column_position = rs.getInt("severity_column_position");
		if (rs.wasNull()) {
			severity_column_position = -1;
		}
		value_med_severity = rs.getInt("value_med_severity");
		if (rs.wasNull()) {
			value_med_severity = -1;
		}
		value_high_severity = rs.getInt("value_high_severity");
		if (rs.wasNull()) {
			value_med_severity = -1;
		}
		expression_text = rs.getString("expression_text");
		message_format = rs.getInt("message_format");
		message_subject = rs.getString("message_subject");
		if (rs.wasNull()) {
			message_subject = "No subject";
		}
		message_header = rs.getString("message_header");
		if (rs.wasNull()) {
			message_header = "No message specified in the script";
		}
		message_text_str = rs.getString("message_text_str");
		if (rs.wasNull()) {
			message_text_str = "";
		}
		message_footer = rs.getString("message_footer");
		if (rs.wasNull()) {
			message_footer = "";
		}
		status_code = rs.getString("status_code");
		create_sysdate = rs.getTimestamp("create_sysdate");
		update_sysdate = rs.getTimestamp("update_sysdate");
		
		rs.close();
		st.close();
		
		// Construct array of targets
		targets = new ArrayList<Target>();
		
		// Get all active targets directly assigned to the script		
		st = conn.prepareStatement(SqlText.getString("Script.getTargetsSQL"));
		st.setInt(1,scriptId);
		rs = st.executeQuery();
		while (rs.next()) {			
			if (!containsTarget(targets,rs.getInt("target_id"))) {
				target = new Target(conn,rs.getInt("target_id"));
				targets.add(target);
			}
		}
		
		rs.close();
		st.close();
		
		// Get all the targets assigned through groups
		st = conn.prepareStatement(SqlText.getString("Script.getTargetsThroughGroupsSQL"));
		st.setInt(1,scriptId);
		rs = st.executeQuery();
		while (rs.next()) {
			if (!containsTarget(targets,rs.getInt("target_id"))) {
				target = new Target(conn,rs.getInt("target_id"));
				targets.add(target);
			}
		}
		
		rs.close();
		st.close();
		
		// Get a list of notification emails
		
		notificationList = new ArrayList<Notification>();
		
		st = conn.prepareStatement(SqlText.getString("Script.getNotificationsSQL"));
		
		st.setInt(1,scriptId);
		rs = st.executeQuery();
		while (rs.next()){
			notificationList.add(new Notification(rs.getShort("severity"),rs.getString("email_address")));
		}
		
		rs.close();
		st.close();
		
		
		// Get a list of personal notifications
		st = conn.prepareStatement(SqlText.getString("Script.getPersonNotificationsSQL"));
		
		st.setInt(1,scriptId);
		rs = st.executeQuery();
		while (rs.next()){
			notificationList.add(new Notification(Script.SEVERITY_LOW,rs.getString("email_address")));
			notificationList.add(new Notification(Script.SEVERITY_MED,rs.getString("email_address")));
			notificationList.add(new Notification(Script.SEVERITY_HIGH,rs.getString("email_address")));
		}
		
		rs.close();
		st.close();
	}
	/**
	 * Simplified version of constructor, which uses already fetched row. 
	 * This improves performance when building a list of scripts
	 * @param rs JDBC resultset
	 * @throws SQLException
	 */
	public Script(ResultSet rs) throws SQLException {			
				
		script_id = rs.getInt("script_id");
		name  = rs.getString("name");
		description = rs.getString("description");
		schedule_min = rs.getString("schedule_min");
		schedule_hour = rs.getString("schedule_hour");
		schedule_day = rs.getString("schedule_day");
		schedule_month = rs.getString("schedule_month");
		schedule_week = rs.getString("schedule_week");
		query_type = rs.getInt("query_type");
		query_text = rs.getString("query_text");
		fixed_severity = rs.getShort("fixed_severity");
		severity_column_position = rs.getInt("severity_column_position");
		if (rs.wasNull()) {
			severity_column_position = -1;
		}
		value_med_severity = rs.getInt("value_med_severity");
		if (rs.wasNull()) {
			value_med_severity = -1;
		}
		value_high_severity = rs.getInt("value_high_severity");
		if (rs.wasNull()) {
			value_med_severity = -1;
		}
		expression_text = rs.getString("expression_text");
		message_subject = rs.getString("message_subject");
		if (rs.wasNull()) {
			message_subject = "No subject";
		}
		message_header = rs.getString("message_header");
		if (rs.wasNull()) {
			message_header = "No message specified in the script";
		}
		message_text_str = rs.getString("message_text_str");
		status_code = rs.getString("status_code");
		create_sysdate = rs.getTimestamp("create_sysdate");
		update_sysdate = rs.getTimestamp("update_sysdate");	
		
	}
	
	/**
	 * Check whether list has particular target id
	 * @param list list of targets
	 * @param targetId target id
	 * @return true or false
	 */
	public boolean containsTarget(ArrayList<Target> list, int targetId) {
		
		for (int i=0; i<list.size();i++){
			if (((Target)list.get(i)).getTarget_id() == targetId){
				return true;
			}			
		}
		
		return false;
	}

	/**
	 * Get create_sysdate field
	 * 
	 * @return create_sysdate.
	 */
	public Timestamp getCreate_sysdate() {
		return create_sysdate;
	}

	/**
	 * Get description field
	 * 
	 * @return description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get expression text field
	 * 
	 * @return expression_text.
	 */
	public String getExpression_text() {
		return expression_text;
	}

	/**
	 * Get fixed_severity flag
	 * 
	 * @return fixed_severity flag
	 */
	public short getFixed_severity() {
		return fixed_severity;
	}

	/**
	 * Get message_header field
	 * 
	 * @return message_header.
	 */
	public String getMessage_header() {
		return message_header;
	}

	/**
	 * Get message_subject field
	 * 
	 * @return message_subject.
	 */
	public String getMessage_subject() {
		return message_subject;
	}

	/**
	 * Get message_text field
	 * 
	 * @return message_text.
	 */
	public String getMessage_text() {
		return message_text_str;
	}

	/**
	 * Get script name
	 * 
	 * @return script name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get query_text field
	 * 
	 * @return query_text.
	 */
	public String getQuery_text() {
		return query_text;
	}

	/**
	 * Get query_type field
	 * 
	 * @return query_type.
	 */
	public int getQuery_type() {
		return query_type;
	}

	/**
	 * Get schedule_day field
	 * 
	 * @return schedule_day.
	 */
	public String getSchedule_day() {
		return schedule_day;
	}

	/**
	 * Get schedule_hour field
	 * 
	 * @return schedule_hour.
	 */
	public String getSchedule_hour() {
		return schedule_hour;
	}

	/**
	 * Get schedule_min field
	 * 
	 * @return schedule_min.
	 */
	public String getSchedule_min() {
		return schedule_min;
	}

	/**
	 * Get schedule_month field
	 * 
	 * @return schedule_month.
	 */
	public String getSchedule_month() {
		return schedule_month;
	}

	/**
	 * Get schedule_week field
	 * 
	 * @return schedule_week.
	 */
	public String getSchedule_week() {
		return schedule_week;
	}
	
	/**
	 * Get full schedule string
	 * 
	 * @return full schedule script
	 */
	public String getFullSchedule() {
		return 
			"0 "+
			schedule_min+" "+
			schedule_hour+" "+
			schedule_day+" "+
			schedule_month+" "+
			schedule_week;
	}

	/**
	 * Get script_id field
	 * 
	 * @return script_id.
	 */
	public int getScript_id() {
		return script_id;
	}
	
	/**
	 * @return string value of script ID
	 */
	public String getScriptIdStr() {
		return String.valueOf(script_id);
	}

	/**
	 * Get severity_column_position field
	 * 
	 * @return severity_column_position.
	 */
	public int getSeverity_column_position() {
		if (severity_column_position != -1) {
			return severity_column_position;
		} else {
			return 0;
		}				
	}

	/**
	 * Get status_code field
	 * 
	 * @return status_code.
	 */
	public String getStatus_code() {
		return status_code;
	}

	/**
	 * Get update_sysdate field
	 * 
	 * @return update_sysdate.
	 */
	public Timestamp getUpdate_sysdate() {
		return update_sysdate;
	}

	/**
	 * Get value_high_severity field
	 * 
	 * @return value_high_severity.
	 */
	public int getValue_high_severity() {
		return value_high_severity;
	}

	/**
	 * Get value_med_severity field
	 * 
	 * @return value_med_severity.
	 */
	public int getValue_med_severity() {
		return value_med_severity;
	}

	/**
	 * Get a list of targets assigned to this script
	 * 
	 * @return List of {@link Target} objects
	 */
	public ArrayList<Target> getTargets() {
		return targets;
	}
	
	/**
	 * Get message format
	 * 
	 * @return message_format
	 */
	public int getMessage_format() {
		return message_format;
	}

	/**
	 * Get message footer
	 * 
	 * @return message_footer
	 */
	public String getMessage_footer() {
		return message_footer;
	}

	
	/**
	 * 
	 * Return a list of targets that script runs on, 
	 * filtered by our server id
	 * 
	 * @param serverId our server id
	 * @return list of {@link Target} objects
	 */
	public ArrayList<Target> getTargets(int serverId) {	
		
		ArrayList<Target> matchingTargets = new ArrayList<Target>();		
		
		for (final Target target : targets) {			
			if (target.getServer_id() == serverId) {
				matchingTargets.add(target);
			}
		}
		return matchingTargets;
	}

	/**
	 * Return notification list for particular severity
	 * @return list of {@link Notification} objects
	 */
	public ArrayList<Notification> getNotificationList(short severity) {
		
		ArrayList<Notification> notificationListforSeverity = 
				new ArrayList<Notification>();
		
		for (final Notification notification : notificationList) {
			if (notification.getSeverity() == severity) {
				notificationListforSeverity.add(notification);
			}
		}
		return notificationListforSeverity;
	}
	
	/**
	 * 
	 * Returns whether script is of fixed severity
	 * 
	 * @return true or false
	 */
	public boolean isFixedSeverity() {
		if (fixed_severity != SEVERITY_CALCULATE) {
			return true;
		} else {
			return false;
		}		
	}
	
	/**
	 * Calculate severity based on column value and thresholds in the script
	 * @param columnValue
	 * @return severity
	 */
	public short calculateSeverity(String columnValue) {
		double columnValueNumber;
		short severity;
		// If data is missing for some of the columns
		// return SEVERITY_HIGH
		if (
				(severity_column_position == -1) ||
				(value_med_severity == -1 )||
				(value_high_severity == -1)
		    ) {	return SEVERITY_HIGH; }
		
		severity = SEVERITY_HIGH;
		
		// Try to convert column value to double
		try {
			columnValueNumber = Double.parseDouble(columnValue);
			if (columnValueNumber < value_med_severity) {
				severity = SEVERITY_LOW;
			} else if (columnValueNumber > value_high_severity) {
				severity = SEVERITY_HIGH;
			} else {
				severity = SEVERITY_MED;
			}
		} catch (Exception e) {
			severity = SEVERITY_HIGH;
		}
		
		
		return severity;
	}
	/**
	 * Returns whether error has occurred
	 * 
	 * @return true or false 
	 */
	public boolean hasErrorOccured() {
		return errorOccured;
	}
	
	/**
	 * Set error flag
	 * @param errorOccured error flag
	 */
	public void setErrorOccured(boolean errorOccured) {
		this.errorOccured = errorOccured;
	}
	
	/**
	 * Return triggered flag
	 * @return true of false
	 */
	public boolean isTriggered() {
		return triggered;
	}
	
	/**
	 * Set triggered flag
	 * @param triggered triggered flag
	 */
	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}
	
	/**
	 * Return execution severity
	 * @return execution severity
	 */
	public short getExecutionSeverity() {
		return executionSeverity;
	}
	
	/**
	 * Set execution severity
	 * @param executionSeverity execution severity
	 */	
	public void setExecutionSeverity(short executionSeverity) {
		this.executionSeverity = executionSeverity;
	}
	
	/**
	 * @return the timeout_sec
	 */
	public int getTimeout_sec() {
		return timeout_sec;
	}
	
	


}
