package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Abstract class inherited by classes that execute scripts in the databases
 *
 */
public abstract class Executor {

	/**
	 * Abstract method to execute script using provided database connection
	 * @param conn established JDBC connection
	 * @param script instance of the script
	 * @param executionResult execution result
	 * @throws SQLException
	 */
	public abstract void execute(Connection conn,Script script, ExecutionResult executionResult) throws SQLException;

}
