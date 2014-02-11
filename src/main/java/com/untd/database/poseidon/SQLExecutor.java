package com.untd.database.poseidon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.untd.database.poseidon.data.Script;


/**
 * Executor to execute SQL statements
 *
 */
public class SQLExecutor extends Executor {
	
	PreparedStatement st;
	
	/* (non-Javadoc)
	 * @see untd.database.poseidon.Executor#execute(java.sql.Connection, untd.database.poseidon.Script, untd.database.poseidon.ExecutionResult)
	 */
	public void execute(Connection conn, Script script,
			ExecutionResult executionResult) throws SQLException {
		
		
		ResultSet rs;	
		
		st = conn.prepareStatement(script.getQueryText());
		
		// As of 04/10/2008 setQueryTimeout was not implemented in Postgres driver
		if (conn.getMetaData().getDriverName() != "PostgreSQL Native Driver") {
			st.setQueryTimeout(script.getTimeoutSec());
		}	
		rs = st.executeQuery();
		while (rs.next()) {
			executionResult.add(new ExecutionResultRow(rs));
		}
		
		rs.close();
		st.close();

	}
	

}
