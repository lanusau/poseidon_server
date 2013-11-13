package com.untd.database.poseidon;

/**
 * Exception to specify that expression evaluation failed
 *
 */
public class ExpressionException extends Exception {

	private static final long serialVersionUID = 2857573964720400480L;

	/**
	 * @param arg0
	 */
	public ExpressionException(String arg0) {
		super(arg0);
	}

}
