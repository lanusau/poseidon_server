package com.untd.database.poseidon.model;

import com.untd.database.poseidon.model.database.types.PersistentEnum;

public enum MessageFormat implements PersistentEnum{
	
	TEXT(0),HTML(1);
	
	private final int id;
	
	MessageFormat(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
}
