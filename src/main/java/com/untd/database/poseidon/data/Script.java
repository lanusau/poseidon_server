package com.untd.database.poseidon.data;

import java.util.Date;

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
	
	private boolean errorOccured;
	private boolean triggered;	
	private short executionSeverity;
	
	/**
	 * Get full schedule string
	 * 
	 * @return full schedule
	 */
	public String getFullSchedule() {
		// Randomly pick a second to run this script, to reduce flood of executions
		// at the minute boundary
		long scheduleSec = Math.round(Math.random()*59);
		return 
			scheduleSec + " "+
			scheduleMin+" "+
			scheduleHour+" "+
			scheduleDay+" "+
			scheduleMonth+" "+
			scheduleWeek;
	}
	
	/**
	 * @return string value of script ID
	 */
	public String getScriptIdStr() {
		return String.valueOf(scriptId);
	}	
	
	/**
	 * Get severity_column_position field
	 * 
	 * @return severity_column_position.
	 */
	public int getSeverity_column_position() {
		if (severityColumnPosition != null) {
			return severityColumnPosition.intValue();
		} else {
			return 0;
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
				(severityColumnPosition == null) ||
				(valueMedSeverity == null )||
				(valueHighSeverity == null)
		    ) {	return SEVERITY_HIGH; }
		
		severity = SEVERITY_HIGH;
		
		// Try to convert column value to double
		try {
			columnValueNumber = Double.parseDouble(columnValue);
			if (columnValueNumber < valueMedSeverity) {
				severity = SEVERITY_LOW;
			} else if (columnValueNumber > valueHighSeverity) {
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
	 * 
	 * Code below is generated by MyBatis generator
	 * 
	 */
	
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.script_id
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer scriptId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.name
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.schedule_min
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String scheduleMin;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.schedule_hour
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String scheduleHour;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.schedule_day
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String scheduleDay;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.schedule_month
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String scheduleMonth;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.schedule_week
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String scheduleWeek;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.query_type
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer queryType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.timeout_sec
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer timeoutSec;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.fixed_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer fixedSeverity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.severity_column_position
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer severityColumnPosition;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.value_med_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer valueMedSeverity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.value_high_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer valueHighSeverity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.message_format
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Integer messageFormat;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.message_subject
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String messageSubject;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.status_code
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String statusCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.create_sysdate
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Date createSysdate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.update_sysdate
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private Date updateSysdate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.description
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String description;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.query_text
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String queryText;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.expression_text
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String expressionText;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.message_header
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String messageHeader;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.message_text_str
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String messageTextStr;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column psd_script.message_footer
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    private String messageFooter;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.script_id
     *
     * @return the value of psd_script.script_id
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getScriptId() {
        return scriptId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.script_id
     *
     * @param scriptId the value for psd_script.script_id
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setScriptId(Integer scriptId) {
        this.scriptId = scriptId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.name
     *
     * @return the value of psd_script.name
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.name
     *
     * @param name the value for psd_script.name
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.schedule_min
     *
     * @return the value of psd_script.schedule_min
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getScheduleMin() {
        return scheduleMin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.schedule_min
     *
     * @param scheduleMin the value for psd_script.schedule_min
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setScheduleMin(String scheduleMin) {
        this.scheduleMin = scheduleMin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.schedule_hour
     *
     * @return the value of psd_script.schedule_hour
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getScheduleHour() {
        return scheduleHour;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.schedule_hour
     *
     * @param scheduleHour the value for psd_script.schedule_hour
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setScheduleHour(String scheduleHour) {
        this.scheduleHour = scheduleHour;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.schedule_day
     *
     * @return the value of psd_script.schedule_day
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getScheduleDay() {
        return scheduleDay;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.schedule_day
     *
     * @param scheduleDay the value for psd_script.schedule_day
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setScheduleDay(String scheduleDay) {
        this.scheduleDay = scheduleDay;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.schedule_month
     *
     * @return the value of psd_script.schedule_month
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getScheduleMonth() {
        return scheduleMonth;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.schedule_month
     *
     * @param scheduleMonth the value for psd_script.schedule_month
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setScheduleMonth(String scheduleMonth) {
        this.scheduleMonth = scheduleMonth;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.schedule_week
     *
     * @return the value of psd_script.schedule_week
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getScheduleWeek() {
        return scheduleWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.schedule_week
     *
     * @param scheduleWeek the value for psd_script.schedule_week
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setScheduleWeek(String scheduleWeek) {
        this.scheduleWeek = scheduleWeek;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.query_type
     *
     * @return the value of psd_script.query_type
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getQueryType() {
        return queryType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.query_type
     *
     * @param queryType the value for psd_script.query_type
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.timeout_sec
     *
     * @return the value of psd_script.timeout_sec
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.timeout_sec
     *
     * @param timeoutSec the value for psd_script.timeout_sec
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.fixed_severity
     *
     * @return the value of psd_script.fixed_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getFixedSeverity() {
        return fixedSeverity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.fixed_severity
     *
     * @param fixedSeverity the value for psd_script.fixed_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setFixedSeverity(Integer fixedSeverity) {
        this.fixedSeverity = fixedSeverity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.severity_column_position
     *
     * @return the value of psd_script.severity_column_position
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getSeverityColumnPosition() {
        return severityColumnPosition;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.severity_column_position
     *
     * @param severityColumnPosition the value for psd_script.severity_column_position
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setSeverityColumnPosition(Integer severityColumnPosition) {
        this.severityColumnPosition = severityColumnPosition;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.value_med_severity
     *
     * @return the value of psd_script.value_med_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getValueMedSeverity() {
        return valueMedSeverity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.value_med_severity
     *
     * @param valueMedSeverity the value for psd_script.value_med_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setValueMedSeverity(Integer valueMedSeverity) {
        this.valueMedSeverity = valueMedSeverity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.value_high_severity
     *
     * @return the value of psd_script.value_high_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getValueHighSeverity() {
        return valueHighSeverity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.value_high_severity
     *
     * @param valueHighSeverity the value for psd_script.value_high_severity
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setValueHighSeverity(Integer valueHighSeverity) {
        this.valueHighSeverity = valueHighSeverity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.message_format
     *
     * @return the value of psd_script.message_format
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Integer getMessageFormat() {
        return messageFormat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.message_format
     *
     * @param messageFormat the value for psd_script.message_format
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setMessageFormat(Integer messageFormat) {
        this.messageFormat = messageFormat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.message_subject
     *
     * @return the value of psd_script.message_subject
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getMessageSubject() {
        return emptyStringIfNull(messageSubject);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.message_subject
     *
     * @param messageSubject the value for psd_script.message_subject
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.status_code
     *
     * @return the value of psd_script.status_code
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.status_code
     *
     * @param statusCode the value for psd_script.status_code
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.create_sysdate
     *
     * @return the value of psd_script.create_sysdate
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Date getCreateSysdate() {
        return createSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.create_sysdate
     *
     * @param createSysdate the value for psd_script.create_sysdate
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setCreateSysdate(Date createSysdate) {
        this.createSysdate = createSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.update_sysdate
     *
     * @return the value of psd_script.update_sysdate
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public Date getUpdateSysdate() {
        return updateSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.update_sysdate
     *
     * @param updateSysdate the value for psd_script.update_sysdate
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setUpdateSysdate(Date updateSysdate) {
        this.updateSysdate = updateSysdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.description
     *
     * @return the value of psd_script.description
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.description
     *
     * @param description the value for psd_script.description
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.query_text
     *
     * @return the value of psd_script.query_text
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getQueryText() {
    	// Need to replace all CR characters
    	if (queryText != null) {
    		queryText = queryText.replace((char)13, (char)10);    		
    	} else {
    		queryText = "";
    	}    		
        return queryText;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.query_text
     *
     * @param queryText the value for psd_script.query_text
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.expression_text
     *
     * @return the value of psd_script.expression_text
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getExpressionText() {
        return emptyStringIfNull(expressionText);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.expression_text
     *
     * @param expressionText the value for psd_script.expression_text
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setExpressionText(String expressionText) {
        this.expressionText = expressionText;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.message_header
     *
     * @return the value of psd_script.message_header
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getMessageHeader() {
        return emptyStringIfNull(messageHeader);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.message_header
     *
     * @param messageHeader the value for psd_script.message_header
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.message_text_str
     *
     * @return the value of psd_script.message_text_str
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getMessageTextStr() {
        return emptyStringIfNull(messageTextStr);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.message_text_str
     *
     * @param messageTextStr the value for psd_script.message_text_str
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setMessageTextStr(String messageTextStr) {
        this.messageTextStr = messageTextStr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column psd_script.message_footer
     *
     * @return the value of psd_script.message_footer
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public String getMessageFooter() {
        return emptyStringIfNull(messageFooter);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column psd_script.message_footer
     *
     * @param messageFooter the value for psd_script.message_footer
     *
     * @mbggenerated Fri Jan 31 09:44:39 GMT-08:00 2014
     */
    public void setMessageFooter(String messageFooter) {
        this.messageFooter = messageFooter;
    }
    
    /**
     * Return empty string if passed parameter is NULL. 
     * @param s
     * @return
     */
    public String emptyStringIfNull(String s) {
    	if (s != null ) {
    		return s;
    	} else {
    		return "";
    	}
    }
}