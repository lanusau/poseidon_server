package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * Executor to execute PLSQL statements
 *
 */
public class PLSQLExecutor extends Executor {

	OracleCallableStatement  st;
	
	/**
	 * @see untd.database.poseidon.Executor#execute(java.sql.Connection, untd.database.poseidon.Script, untd.database.poseidon.ExecutionResult)
	 */
	public void execute(Connection conn, Script script,
			ExecutionResult executionResult) throws SQLException {	
				
		ResultSet rs;	
		
		st = (OracleCallableStatement)conn.prepareCall(script.getQuery_text());	
		st.setQueryTimeout(script.getTimeout_sec());


		st.registerOutParameter (1, OracleTypes.CURSOR);
		
		st.execute();
		
		rs = st.getCursor(1);
		while (rs.next()) {
			executionResult.add(new ExecutionResultRow(rs));
		}		
		
	}
	

}
