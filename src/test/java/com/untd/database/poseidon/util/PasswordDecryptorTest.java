package com.untd.database.poseidon.util;

import static org.junit.Assert.*;

import java.security.DigestException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.untd.database.poseidon.config.TestConfig;
import com.untd.database.poseidon.model.database.Target;

@RunWith(SpringJUnit4ClassRunner.class)
public class PasswordDecryptorTest extends TestConfig{

	@Autowired
	private PasswordDecryptor passwordDecryptor;
	
	@Test
	public void testGetDecryptedPassword() {
		Target target = new Target();
		target.setSalt("3802852479467620");
		target.setMonitorPassword("wFgBkMJ5nkgSJOtVJo1r7g==");
		try {
			assertEquals("dev2",passwordDecryptor.getDecryptedPassword(target));
		} catch (DigestException e) {
			fail();
		}
	}
}
