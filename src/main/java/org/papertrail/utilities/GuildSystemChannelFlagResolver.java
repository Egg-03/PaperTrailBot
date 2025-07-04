package org.papertrail.utilities;

import java.util.Map;

public class GuildSystemChannelFlagResolver {
	
	private static final Map<Long, String> SYSTEM_CHANNEL_FLAG_MAP = Map.ofEntries(
			Map.entry(1L << 0, "Suppress member join notifications"),
	        Map.entry(1L << 1, "Suppress server boost notifications"),
	        Map.entry(1L << 2, "Suppress server setup tips"),
	        Map.entry(1L << 3, "Hide member join sticker reply buttons"),
	        Map.entry(1L << 4, "Suppress role subscription purchase and renewal notifications"),
	        Map.entry(1L << 5, "Hide role subscription sticker reply buttons")
			);
	
	private GuildSystemChannelFlagResolver() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static String parseFlags(long bitfield){
		StringBuilder flags = new StringBuilder();
		for(Map.Entry<Long, String> entry: SYSTEM_CHANNEL_FLAG_MAP.entrySet()) {
			if((bitfield & entry.getKey()) != 0) {
				flags.append("✅").append(entry.getValue()).append(System.lineSeparator());
			}
		}
		return flags.toString();
	}
	
	public static String getParsedFlags(Object flagValue) {
		if(flagValue==null) {
			return "ERROR: Value Returned Null";
		}
		
		try {
			return parseFlags(Long.parseLong(flagValue.toString()));
		} catch (NumberFormatException  e) {
			return "No Parsable Flag Values Detected";
		}
		
	}
}

