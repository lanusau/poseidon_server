package com.untd.database.poseidon.util;

import java.security.DigestException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.untd.database.poseidon.model.Settings;
import com.untd.database.poseidon.model.database.Target;

public class PasswordDecryptor {
	
	@Autowired
	private Settings settings;
	
	/**
     * Get decrypted password
     * @return decrypted password
     * @throws DigestException
     */
    public String getDecryptedPassword(Target target) throws DigestException {
		String decryptedPassword = "";
		
		// Try to decrypt the password
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] key = md.digest(settings.getDecryptionSecret().getBytes("UTF-8"));
			final SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			final byte[] iv = md.digest(target.getSalt().getBytes("UTF-8"));

			final byte[] decodedBytes = Base64.decodeBase64(target.getMonitorPassword());

			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
			final byte[] decryptedBytes = cipher.doFinal(decodedBytes);
			decryptedPassword = new String(decryptedBytes);
		} catch (Exception e) {
			throw new DigestException("Can not decrypt password for target id:"
					+ target.getTargetId());
		}
		return decryptedPassword;
    }

}
