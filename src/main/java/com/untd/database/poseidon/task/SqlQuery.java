package com.untd.database.poseidon.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.untd.database.poseidon.model.ExecutionResultRow;
import com.untd.database.poseidon.model.database.Script;
import com.untd.database.poseidon.util.JdbcUtil;

/**
 * SQL query
 *
 */
public class SqlQuery implements DatabaseQuery {

	@Override
	public List<ExecutionResultRow> execute(Connection conn, Script script) throws SQLException {
		
		final List<ExecutionResultRow> executionResultRowList = new ArrayList<ExecutionResultRow>();
		
		final PreparedStatement st = conn.prepareStatement(script.getQueryText());
		st.setQueryTimeout(script.getTimeoutSec());
		final ResultSet rs = st.executeQuery();
		while (rs.next()) {
			executionResultRowList.add(new ExecutionResultRow(rs));
		}
		JdbcUtil.closeResultSetHandle(rs);
		JdbcUtil.closeStatementHandle(st);
		
		return executionResultRowList;
	}

}
