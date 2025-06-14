package utilities;

import java.util.Map;

public class PermissionResolver {
	
	private static final Map<Long, String> PERMISSION_MAP = Map.ofEntries(
			Map.entry(1L << 0, "CREATE_INSTANT_INVITE"),
	        Map.entry(1L << 1, "KICK_MEMBERS"),
	        Map.entry(1L << 2, "BAN_MEMBERS"),
	        Map.entry(1L << 3, "ADMINISTRATOR"),
	        Map.entry(1L << 4, "MANAGE_CHANNELS"),
	        Map.entry(1L << 5, "MANAGE_GUILD"),
	        Map.entry(1L << 6, "ADD_REACTIONS"),
	        Map.entry(1L << 7, "VIEW_AUDIT_LOG"),
	        Map.entry(1L << 8, "PRIORITY_SPEAKER"),
	        Map.entry(1L << 9, "STREAM"),
	        Map.entry(1L << 10, "VIEW_CHANNEL"),
	        Map.entry(1L << 11, "SEND_MESSAGES"),
	        Map.entry(1L << 12, "SEND_TTS_MESSAGES"),
	        Map.entry(1L << 13, "MANAGE_MESSAGES"),      
	        Map.entry(1L << 14, "EMBED_LINKS"),
	        Map.entry(1L << 15, "ATTACH_FILES"),
	        Map.entry(1L << 16, "READ_MESSAGE_HISTORY"),
	        Map.entry(1L << 17, "MENTION_EVERYONE"),
	        Map.entry(1L << 18, "USE_EXTERNAL_EMOJIS"),
	        Map.entry(1L << 19, "VIEW_GUILD_INSIGHTS"),
	        Map.entry(1L << 20, "CONNECT"),
	        Map.entry(1L << 21, "SPEAK"),	    
	        Map.entry(1L << 22, "MUTE_MEMBERS"),
	        Map.entry(1L << 23, "DEAFEN_MEMBERS"),
	        Map.entry(1L << 24, "MOVE_MEMBERS"),
	        Map.entry(1L << 25, "USE_VOICE_ACTIVITY_DETECTION"),
	        Map.entry(1L << 26, "CHANGE_NICKNAME"),
	        Map.entry(1L << 27, "MANAGE_NICKNAMES"),
	        Map.entry(1L << 28, "MANAGE_ROLES"),
	        Map.entry(1L << 29, "MANAGE_WEBHOOKS"),
	        Map.entry(1L << 30, "MANAGE_GUILD_EXPRESSIONS"),	    	        
	        Map.entry(1L << 31, "USE_APPLICATION_COMMANDS"), 
			Map.entry(1L << 32, "REQUEST_TO_SPEAK"),
	        Map.entry(1L << 33, "MANAGE_EVENTS"),
	        Map.entry(1L << 34, "MANAGE_THREADS"),
	        Map.entry(1L << 35, "CREATE_PUBLIC_THREADS"),
	        Map.entry(1L << 36, "CREATE_PRIVATE_THREADS"),
	        Map.entry(1L << 37, "USE_EXTERNAL_STICKERS"),
	        Map.entry(1L << 38, "SEND_MESSAGES_IN_THREADS"),
	        Map.entry(1L << 39, "USE_EMBEDDED_ACTIVITIES"),
	        Map.entry(1L << 40, "MODERATE_MEMBERS"),
	        Map.entry(1L << 41, "VIEW_CREATOR_MONETIZATION_ANALYTICS"),
	        Map.entry(1L << 42, "USE_SOUNDBOARD"),
	        Map.entry(1L << 43, "CREATE_GUILD_EXPRESSIONS"),
	        Map.entry(1L << 44, "CREATE_EVENTS"),
	        Map.entry(1L << 45, "USE_EXTERNAL_SOUNDS"),
	        Map.entry(1L << 46, "SEND_VOICE_MESSAGES"),
	        
	        Map.entry(1L << 49, "SEND_POLLS"),
	        Map.entry(1L << 50, "USE_EXTERNAL_APPS")
			);
	
	private PermissionResolver() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static String parsePermissions(long bitfield, String emoji){
		StringBuilder permissions = new StringBuilder();
		for(Map.Entry<Long, String> entry: PERMISSION_MAP.entrySet()) {
			if((bitfield & entry.getKey()) != 0) {
				permissions.append(emoji).append(entry.getValue()).append(System.lineSeparator());
			}
		}
		return permissions.toString();
	}
	
	public static String getParsedPermissions(Object permissionValues, String emoji) { // this emoji is ❌ for denied permissions and ✅ for allowed
		if(permissionValues==null) {
			return "ERROR: Value Returned Null";
		}
		
		try {
			return parsePermissions(Long.parseLong(permissionValues.toString()), emoji);
		} catch (NumberFormatException e) {
			return "No Parsable Permissions Detected";
		}
	}
}
