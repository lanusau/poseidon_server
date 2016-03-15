package com.untd.database.poseidon.model;

import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.model.database.Target;

/**
 * 
 * Class used to store information needed to raise an alert
 * 
 */
public class AlertMessage {
	
	
	private static final String nl = "\n";
	
	private String messageSubject,messageHeader, messageFooter;
	private StringBuilder messageBody;
	private Script script;
	private String severityTxt;
	private MessageFormat messageFormat;
	
	/**
	 * Build alert message.
	 * Message is built using templates from script, which are then populated with values
	 * from target and execution result
	 * @param paramScript script which generated alert. 
	 * @param paramTarget target where alert was generated
	 * @param paramResult execution result that triggered the alert
	 */
	public AlertMessage(Script script, Target target, ExecutionResult result) {
		this.script = script;
		
		messageBody = new StringBuilder();
		
		messageSubject = script.getMessageSubject().replaceAll("%t",target.getName());
		messageSubject = messageSubject.replaceAll("%n",script.getName());
		
		messageHeader = script.getMessageHeader().replaceAll("%t",target.getName());
		messageHeader = messageHeader.replaceAll("%n",script.getName());
		messageHeader = messageHeader.replaceAll("%rc",""+result.getRows().size());
		
		messageFooter = script.getMessageFooter();
		
		messageFormat = script.getMessageFormat();
		
		severityTxt = "Low";
	}
	
	/**
	 * Add a row to a message text. 
	 * The row is constructed using template in the script and columns in
	 * provided row from execution result
	 * @param row row from execution result 
	 */
	public void addRowToMessage(ExecutionResultRow row) {
		
		String rowMessage;
		
		// Add alert from this row to the message text
		rowMessage = script.getMessageTextStr();
		
		// For each column, replace variable %x			
		for (int i=0;i<row.getColumns().size();i++) {
			rowMessage = rowMessage.replaceAll("%"+i,row.getColumns().get(i));
		}			
		
		messageBody.append(rowMessage).append(nl);
	}

	
	/**
	 * Set the severity of alert message
	 * 
	 * @param severity the severity to set
	 */
	public void setSeverity(Severity severity) {
		switch (severity) {

		case LOW:
			severityTxt = "Low";
			break;
		case MEDIUM:
			severityTxt = "Medium";
			break;
		case HIGH:
			severityTxt = "High";
			break;
		default:
			severityTxt = "";
			break;
		}
	}

	/**
	 * Return message subject
	 * 
	 * @return message subject 
	 */
	public String getMessageSubject() {
		return messageSubject.replaceAll("%s",severityTxt);
	}
	
	/**
	 * Return message text
	 * 
	 * @return message text
	 */
	public String getMessageText() {
		StringBuilder messageText;
		
		messageText = new StringBuilder(messageHeader);
		messageText.append(messageBody);
		messageText.append(messageFooter);
		
		return messageText.toString();
	}
	
	/**
	 * Return message format
	 * 
	 * @return messageFormat
	 */
	public MessageFormat getMessageFormat() {
		return messageFormat;
	}
}
