package org.papertrail.utilities;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

	private static final Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
	
	private EnvConfig() {
		throw new IllegalStateException("Utility Class");
	}

	public static String get(String key) {
		
		String value = dotenv.get(key);
		if (value != null) {
			return value;
		}
		
		return System.getenv(key);
	}
}
