package com.untd.database.poseidon.task;

import com.untd.database.poseidon.model.database.Script;

/**
 * Return suitable {@link DatabaseQuery} to execute specified {@link Script}
 *
 */
public class DatabaseQueryFactory {
	
	public static DatabaseQuery getDatabaseTask(Script script) {
		switch (script.getQueryType()) {
		case PLSQL:
			return new PlsqlQuery();
		case SQL:
			return new SqlQuery();
		default:
			throw new UnsupportedOperationException("Unknown query type");
		}
		
	}
}
