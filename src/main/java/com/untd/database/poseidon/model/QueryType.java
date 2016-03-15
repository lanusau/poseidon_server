package com.untd.database.poseidon.model;

import com.untd.database.poseidon.model.database.types.PersistentEnum;

public enum QueryType  implements PersistentEnum{
	
	SQL(1), PLSQL(2);

	private final int id;
	
	QueryType(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
}
