package org.papertrail.database;

public class TableNames {
	
	private TableNames() {
		throw new IllegalStateException("Utility Class");
	}
	
	public static final String AUDIT_LOG_TABLE = "audit_log_table";
	public static final String MESSAGE_LOG_REGISTRATION_TABLE = "message_log_registration_table";
	public static final String MESSAGE_LOG_CONTENT_TABLE = "message_log_content_table";
}
