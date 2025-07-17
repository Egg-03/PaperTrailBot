package org.papertrail.database;

public class AuthorAndMessageEntity {
	
	private final String authorId;
	private final String messageContent;
		
	public AuthorAndMessageEntity(String authorId, String messageContent) {
		super();
		this.authorId = authorId;
		this.messageContent = messageContent;
	}
	
	public String getAuthorId() {
		return authorId;
	}

	public String getMessageContent() {
		return messageContent;
	}
	
}
