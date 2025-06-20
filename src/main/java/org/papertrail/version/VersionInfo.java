package org.papertrail.version;

public class VersionInfo {
	
	private VersionInfo() {
		throw new IllegalStateException("Utility Class");
	}
	
	public static final String APPNAME = "PaperTrail";
	public static final String VERSION = "v1.1.0";
	public static final String PROJECT_LINK = "https://github.com/Egg-03/PaperTrailBot";
	public static final String PROJECT_ISSUE_LINK="https://github.com/Egg-03/PaperTrailBot/issues";
	
	public static final String SERVER_LOCATION = "Render: Frankfurt(Germany, Europe)";
	public static final String DATABASE_LOCATION = "Aiven: England(UK, Europe)";
}
