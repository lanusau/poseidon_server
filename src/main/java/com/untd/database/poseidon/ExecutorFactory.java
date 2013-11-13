package com.untd.database.poseidon;

/**
 * Class to return correct Executor for Script. Current, 2 types of executors are provided 
 * SQL and PL/SQL
 *
 */
public class ExecutorFactory {

	
	/**
	 * Return appropriate execution based on the value of script query type
	 * field
	 * @param script instance of the script
	 * @return {@link SQLExecutor} or {@link PLSQLExecutor}
	 */
	public static Executor getExecutor(Script script) {
		
		if (script.getQuery_type() == Script.QUERY_TYPE_SQL) {
			return new SQLExecutor();
		} else if (script.getQuery_type() == Script.QUERY_TYPE_PLSQL){
			return new PLSQLExecutor();
		} else {
			return new SQLExecutor();
		}
		
	}
}
