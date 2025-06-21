package org.papertrail.utilities;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.tinylog.Logger;

public class MessageEncryption {
	
	private static final String SECRET = EnvConfig.get("MESSAGE_SECRET");
    private static final String ALGORITHM = "PBEWITHSHA256AND256BITAES-CBC-BC";
	
	private MessageEncryption() {
		throw new IllegalStateException("UtilityClass");
	}
	
	private static StandardPBEStringEncryptor getEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECRET); 
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setKeyObtentionIterations(100_000);
        return encryptor;
    }
	
	private static boolean isSecretEmpty() {
		return SECRET == null || SECRET.isBlank();
	}
	
	public static String encrypt(String plainText) {
		if(isSecretEmpty()) {
			Logger.error("MESSAGE_SECRET is empty! No message will be encrypted and a null will be returned instead");
			return null;
		}
		StandardPBEStringEncryptor enc = getEncryptor();
		return enc.encrypt(plainText);	
	}
	
	public static String decrypt(String encryptedText) {
		if(isSecretEmpty()) {
			Logger.error("MESSAGE_SECRET is empty! No message will be decrypted and a null will be returned instead");
			return null;
		}
		StandardPBEStringEncryptor enc = getEncryptor();
		return enc.decrypt(encryptedText);
	}
	
}
