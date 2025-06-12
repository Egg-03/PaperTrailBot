package utilities;

/*
 * Resolves Object sub-types from their given IDs
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-types"> Discord Channel Types </a>
 */
public class TypeResolver {

	private TypeResolver() {
		throw new IllegalStateException("Utility Class");
	}

	public static String channelTypeResolver(int channelTypeId) {

		return switch (channelTypeId) {
		case 0 -> "Text";
		case 1 -> "DM";
		case 2 -> "Voice";
		case 3 -> "Group DM";
		case 4 -> "Category";
		case 5 -> "Announcement";
		case 10 -> "Announcement Thread";
		case 11 -> "Public Thread";
		case 12 -> "Private Thread";
		case 13 -> "Stage Voice";
		case 14 -> "Stage Directory";
		case 15 -> "Forum";
		case 16 -> "Media";
		default -> "Undocumented Type: " + channelTypeId;
		};
	}

	public static String formatNumberOrUnlimited(int limitNumber) {
		if (limitNumber == 0) {
			return "Unlimited";
		} else {
			return String.valueOf(limitNumber);
		}
	}

	public static String videoQualityModeResolver(int qualityValue) {
		return switch (qualityValue) {
			case 1 -> "Auto";
			case 2 -> "720p/full";
			default -> String.valueOf(qualityValue);
		};
	}
	
	public static String voiceChannelBitrateResolver(int bitrate) {
        if (bitrate <= 0) {
            return "Unknown";
        }

        int kbps = bitrate / 1000;
        return kbps + " kbps";
    }
	
	public static String channelOverrideTypeResolver (int type) {
		return switch (type) {
			case 0 -> "Role";
			case 1 -> "Member/Application";
			default -> "Unknown";
		};
	}
}
