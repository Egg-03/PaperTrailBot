package org.papertrail.utilities;

/*
 * Resolves Object sub-types from their given IDs
 * @see <a href="https://discord.com/developers/docs/resources/channel#channel-object-channel-types"> Discord Channel Types </a>
 */
public class TypeResolver {

	private TypeResolver() {
		throw new IllegalStateException("Utility Class");
	}

	public static String channelTypeResolver(Object channelTypeId) {
		if (channelTypeId == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int channelType = Integer.parseInt(channelTypeId.toString());
			return switch (channelType) {
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
		} catch (NumberFormatException e) {
			return "Channel Type Is Not Parsable";
		}

	}

	public static String formatNumberOrUnlimited(Object limitNumber) {
		if (limitNumber == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int limitNumberInt = Integer.parseInt(limitNumber.toString());
			if (limitNumberInt == 0) {
				return "Unlimited";
			} else {
				return String.valueOf(limitNumberInt);
			}
		} catch (NumberFormatException e) {
			return "Type Cannot Be Parsed";
		}

	}

	public static String videoQualityModeResolver(Object qualityValue) {
		if (qualityValue == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int qualityValueInt = Integer.parseInt(qualityValue.toString());
			return switch (qualityValueInt) {
			case 1 -> "Auto";
			case 2 -> "720p/full";
			default -> String.valueOf(qualityValueInt);
			};
		} catch (NumberFormatException e) {
			return "Type Cannot Be Parsed";
		}

	}

	public static String voiceChannelBitrateResolver(Object bitrate) {
		if (bitrate == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int bitrateInt = Integer.parseInt(bitrate.toString());
			if (bitrateInt <= 0) {
				return "Unknown";
			}

			int kbps = bitrateInt / 1000;
			return kbps + " kbps";
		} catch (NumberFormatException e) {
			return "Type Cannot Be Parsed";
		}

	}

	public static String channelOverrideTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int typeInt = Integer.parseInt(type.toString());
			return switch (typeInt) {
			case 0 -> "Role";
			case 1 -> "Member/Application";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "Type Cannot Be Parsed";
		}

	}

	public static String automodEventTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int typeInt = Integer.parseInt(type.toString());
			return switch (typeInt) {
			case 1 -> "Message Send";
			case 2 -> "Message Update";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "Type Cannot Be Parsed";
		}

	}

	public static String automodTriggerTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int typeInt = Integer.parseInt(type.toString());
			return switch (typeInt) {
			case 1 -> "Keyword";
			case 3 -> "Spam";
			case 4 -> "Keyword Preset";
			case 5 -> "Mention Spam";
			case 6 -> "Member Profile";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "Type Cannot Be Parsed";
		}

	}

	public static String guildVerificationLevelResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int t = Integer.parseInt(type.toString());
			return switch (t) {
			case 0 -> "NONE";
			case 1 -> "LOW (Verified Email)";
			case 2 -> "MEDIUM (Registered on Discord for more than 5 minutes";
			case 3 -> "HIGH (Must be a member of the server for longer than 10 minutes)";
			case 4 -> "VERY_HIGH (Must have a verified phone number)";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "No Parsable Type Values Detected";
		}

	}
	
	public static String explicitFilterTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int t = Integer.parseInt(type.toString());
			return switch (t) {
			case 0 -> "No Filter";
			case 1 -> "Filter Messages From Server Members Without Roles";
			case 2 -> "Filter Messages From All Members";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "No Parsable Type Values Detected";
		}

	}
	
	public static String scheduleEventTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int t = Integer.parseInt(type.toString());
			return switch (t) {
			case 1 -> "Stage Event";
			case 2 -> "Voice Event";
			case 3 -> "Text Event";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "No Parsable Type Values Detected";
		}

	}
	
	public static String scheduleEventStatusTypeResolver(Object type) {
		if (type == null) {
			return "ERROR: Value Returned Null";
		}

		try {
			int t = Integer.parseInt(type.toString());
			return switch (t) {
			case 1 -> "Scheduled";
			case 2 -> "Active";
			case 3 -> "Completed";
			case 4 -> "Cancelled";
			default -> "Unknown";
			};
		} catch (NumberFormatException e) {
			return "No Parsable Type Values Detected";
		}

	}
}
