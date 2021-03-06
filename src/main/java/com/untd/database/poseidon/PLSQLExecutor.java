package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.untd.database.poseidon.data.Script;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * Executor to execute PLSQL statements
 *
 */
public class PLSQLExecutor extends Executor {

	OracleCallableStatement  st;
	
	/**
	 * Execute PL/SQL call
	 */
	public void execute(Connection conn, Script script,
			ExecutionResult executionResult) throws SQLException {	
				
		ResultSet rs;	
		
		st = (OracleCallableStatement)conn.prepareCall(script.getQueryText());	
		st.setQueryTimeout(script.getTimeoutSec());


		st.registerOutParameter (1, OracleTypes.CURSOR);
		
		st.execute();
		
		rs = st.getCursor(1);
		while (rs.next()) {
			executionResult.add(new ExecutionResultRow(rs));
		}		
		
	}
	

}
