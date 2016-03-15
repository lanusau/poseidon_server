package com.untd.database.poseidon;

import com.untd.database.poseidon.util.CnameChecker;

/**
 * Fake CNAME checker used in testing
 *
 */
public class FakeCnameChecker extends CnameChecker {
	
	private boolean match = true; // Whether simulate CNAME matching our hostname
	
	public boolean check() {
		return match;
	}

	public void setMatch(boolean match) {
		this.match = match;
	}

}
