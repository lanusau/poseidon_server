package com.untd.database.poseidon.model.database.types;

import com.untd.database.poseidon.model.Severity;

public class SeverityUserType extends PersistentEnumUserType<Severity>{

	@Override
	public Class<Severity> returnedClass() {
		return Severity.class;
	}

}
