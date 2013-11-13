package com.untd.database.poseidon;


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
	private Target target;
	private ExecutionResult result;
	private String severityTxt;
	private int messageFormat;
	
	/**
	 * Build alert message.
	 * Message is built using templates from script, which are then populated with values
	 * from target and execution result
	 * @param paramScript script which generated alert. 
	 * @param paramTarget target where alert was generated
	 * @param paramResult execution result that triggered the alert
	 */
	public AlertMessage(Script paramScript, Target paramTarget, ExecutionResult paramResult) {
		script = paramScript;
		target = paramTarget;
		result = paramResult;
		
		messageBody = new StringBuilder();
		
		messageSubject = script.getMessage_subject().replaceAll("%t",target.getName());
		messageSubject = messageSubject.replaceAll("%n",script.getName());
		
		messageHeader = script.getMessage_header().replaceAll("%t",target.getName());
		messageHeader = messageHeader.replaceAll("%n",script.getName());
		messageHeader = messageHeader.replaceAll("%rc",""+result.getRows().size());
		
		messageFooter = script.getMessage_footer();
		
		messageFormat = script.getMessage_format();
		
		severityTxt = "Low";
	}
	
	/**
	 * Add a row to a message text
	 * The row is constructed using template in the script and columns in
	 * provided row from execution result
	 * @param row row from execution result 
	 */
	public void addRowToMessage(ExecutionResultRow row) {
		
		String rowMessage;
		
		// Add alert from this row to the message text
		rowMessage = script.getMessage_text();
		
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
	public void setSeverity(short severity) {
		switch (severity) {
		
			case Script.SEVERITY_LOW: 
				severityTxt = "Low";
				break;
			case Script.SEVERITY_MED: 
				severityTxt = "Medium";
				break;	
			case Script.SEVERITY_HIGH: 
				severityTxt = "High";
				break;		
				
		}
	}

	/**
	 * @return Return message subject 
	 */
	public String getMessageSubject() {
		return messageSubject.replaceAll("%s",severityTxt);
	}
	
	/**
	 * @return Return message text
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
	 * @return messageFormat
	 */
	public int getMessageFormat() {
		return messageFormat;
	}
}
