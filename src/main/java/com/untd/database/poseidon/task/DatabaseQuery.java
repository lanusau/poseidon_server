package com.untd.database.poseidon.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.untd.database.poseidon.model.ExecutionResultRow;
import com.untd.database.poseidon.model.database.Script;

/**
 * Interface implemented by classes that execute scripts in the databases
 *
 */
public interface DatabaseQuery {
	
	/**
	 * Execute particular script using database connection provided
	 * @param conn - {@link Connection} to the database
	 * @param script - {@link Script} to execute
	 * @return {@link ExecutioResult}
	 */
	List<ExecutionResultRow> execute(Connection conn,Script script) throws SQLException ;
}
