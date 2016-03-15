package com.untd.database.poseidon.model.database.types;

import com.untd.database.poseidon.model.QueryType;

public class QueryTypeUserType extends PersistentEnumUserType<QueryType>{

	@Override
	public Class<QueryType> returnedClass() {
		return QueryType.class;
	}

}
