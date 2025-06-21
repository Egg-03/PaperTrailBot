package org.papertrail.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.papertrail.utilities.EnvConfig;
import org.papertrail.utilities.MessageEncryption;
import org.tinylog.Logger;

public class DatabaseConnector {

	private static final String DB_URL = EnvConfig.get("DATABASEURL");

	private Connection connect;

	public DatabaseConnector() throws SQLException {
		connect = DriverManager.getConnection(DB_URL);
	}

	public void registerGuildAndChannel(String guildId, String channelId, String tableName) throws SQLException  {

		String sqlStatement = "INSERT INTO "+tableName+" (guild_id, channel_id) VALUES (?, ?)";

		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setLong(1, Long.parseLong(guildId));
			psmt.setLong(2, Long.parseLong(channelId));
			psmt.executeUpdate();
		} 

	}

	public String retrieveRegisteredChannelId(String guildId, String tableName) {

		String sqlStatement = "SELECT channel_id FROM " + tableName + " WHERE guild_id = ?";
		String channelId = "";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setLong(1, Long.parseLong(guildId));

			try (ResultSet rs = psmt.executeQuery()) {

				while (rs.next()) { // by configuration, there will always be only one channel id row at a time
					// because registering multiple channels is not allowed
					channelId = String.valueOf(rs.getLong("channel_id"));
				}
			}
			return channelId;
		} catch (SQLException e) {
			Logger.error("Could not retrieve registered guild channel id", e);
			return null;
		}
	}
	/*
	 * If a guild of the  given id is found, will return that id, otherwise null
	 */
	public String checkGuildRegistration(String guildId, String tableName) {

		String sqlStatement = "SELECT guild_id FROM " + tableName + " WHERE guild_id = ?";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setLong(1, Long.parseLong(guildId));

			try (ResultSet rs = psmt.executeQuery()) {

				while (rs.next()) { 					
					return String.valueOf(rs.getLong("guild_id"));

				}
			}
			return null;
		} catch (SQLException e) {
			Logger.error("Could not retrieve registered guild channel id", e);
			return null;
		}
	}

	/*
	 * If a channel of the  given id is found, will return that id, otherwise null
	 */
	public String checkChannelRegistration(String channelId, String tableName) {

		String sqlStatement = "SELECT channel_id FROM " + tableName + " WHERE channel_id = ?";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setLong(1, Long.parseLong(channelId));

			try (ResultSet rs = psmt.executeQuery()) {
				while (rs.next()) { 					
					return String.valueOf(rs.getLong("guild_id"));
				}
			}
			return null;
		} catch (SQLException e) {
			Logger.error("Could not retrieve registered guild channel id", e);
			return null;
		}
	}

	public void unregisterGuildAndChannel(String guildId, String tableName) throws SQLException {

		String sqlStatement = "DELETE FROM "+tableName+" WHERE guild_id = ?";

		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setLong(1, Long.parseLong(guildId));
			psmt.executeUpdate();
		} 
	}

	
	// MESSAGE FUNCTIONS
	public void logMessage(String messageId, String messageContent, String authorId, String tableName) throws SQLException {

		String sqlStatement = "INSERT INTO "+tableName+" (message_id, message_content, author_id) VALUES (?, ?, ?)";

		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setLong(1, Long.parseLong(messageId));
			psmt.setString(2, MessageEncryption.encrypt(messageContent));
			psmt.setLong(3, Long.parseLong(authorId));
			psmt.executeUpdate();
		} 
	}

	public List<String> retrieveAuthorAndMessage(String messageId, String tableName) {

		String sqlStatement = "SELECT author_id, message_content FROM " + tableName + " WHERE message_id = ?";

		String messageContent = "";
		String authorId = "";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setLong(1, Long.parseLong(messageId));

			try (ResultSet rs = psmt.executeQuery()) {	
				if (rs.next()) { // only one message is logged per row per message id
					messageContent = MessageEncryption.decrypt(rs.getString("message_content"));
					authorId = String.valueOf(rs.getLong("author_id"));
					return List.of(authorId, messageContent);
				}
			}
			return Collections.emptyList();
		} catch (SQLException e) {
			Logger.error("Could not retrieve message", e);
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	/*
	 * Checks if the message id exists in the database and returns the same if found, else null
	 */
	public String checkMessageId(String messageId, String tableName) {

		String sqlStatement = "SELECT message_id FROM " + tableName + " WHERE message_id = ?";

		try (PreparedStatement psmt = connect.prepareStatement(sqlStatement)) {
			psmt.setLong(1, Long.parseLong(messageId));

			try (ResultSet rs = psmt.executeQuery()) {	
				if (rs.next()) { // only one message is logged per row per message id
					return String.valueOf(rs.getLong("message_id"));
				}
			}
			return null;
		} catch (SQLException e) {
			Logger.error("Could not retrieve message id", e);
			e.printStackTrace();
			return null;
		}
	}

	public void updateMessage(String messageId, String messageContent, String tableName) throws SQLException {

		String sqlStatement = "UPDATE " + tableName + " SET message_content = ? WHERE message_id = ?";

		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setString(1, MessageEncryption.encrypt(messageContent));
			psmt.setLong(2, Long.parseLong(messageId));
			psmt.executeUpdate();
		} 
	}

	public void deleteMessage(String messageId, String tableName) throws SQLException {

		String sqlStatement = "DELETE FROM "+tableName+" WHERE message_id = ?";

		try(PreparedStatement psmt = connect.prepareStatement(sqlStatement)){
			psmt.setLong(1, Long.parseLong(messageId));
			psmt.executeUpdate();
		} 
	}

}
