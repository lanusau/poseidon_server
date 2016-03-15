package com.untd.database.poseidon.model;

import com.untd.database.poseidon.model.database.types.PersistentEnum;

public enum Severity implements PersistentEnum{
	
	CALCULATE(0),HIGH(1),MEDIUM(2),LOW(3);
	
	private final int id;
	
	Severity(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}

}
