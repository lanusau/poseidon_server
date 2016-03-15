package com.untd.database.poseidon.model.database.types;

import com.untd.database.poseidon.model.MessageFormat;

public class MessageFormatUserType extends PersistentEnumUserType<MessageFormat>{

	@Override
	public Class<MessageFormat> returnedClass() {
		return MessageFormat.class;
	}

}
