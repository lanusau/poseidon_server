package com.untd.database.poseidon.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.untd.database.poseidon.model.ExecutionResultRow;
import com.untd.database.poseidon.model.database.Script;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 * PL/SQL query (with REF CURSOR output parameter)
 *
 */
public class PlsqlQuery implements DatabaseQuery {

	@Override
	public List<ExecutionResultRow> execute(Connection conn, Script script) throws SQLException {
		
		// Works only for Oracle databases
		if (!conn.getMetaData().getDriverName().equals("Oracle JDBC Driver ")) {
			throw new UnsupportedOperationException("PLSQL script only works on Oracle database");
		}
		final List<ExecutionResultRow> executionResultRowList = new ArrayList<ExecutionResultRow>();
		
		final OracleCallableStatement st = (OracleCallableStatement)conn.prepareCall(script.getQueryText());	
		st.setQueryTimeout(script.getTimeoutSec());
		st.registerOutParameter (1, OracleTypes.CURSOR);
		st.execute();
		
		final ResultSet rs = st.getCursor(1);
		while (rs.next()) {
			executionResultRowList.add(new ExecutionResultRow(rs));
		}		
		return executionResultRowList;
	}

}
