package org.papertrail.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.sql.SQLException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.papertrail.cleanup.BotKickListener;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.listeners.customlisteners.AnnouncementListener;
import org.papertrail.listeners.customlisteners.BotInfoListener;
import org.papertrail.listeners.customlisteners.ServerStatListener;
import org.papertrail.listeners.customlisteners.BotSetupListener;
import org.papertrail.listeners.guildlisteners.ServerBoostListener;
import org.papertrail.listeners.loglisteners.AuditLogListener;
import org.papertrail.listeners.loglisteners.AuditLogCommandListener;
import org.papertrail.listeners.memberlisteners.GuildMemberJoinAndLeaveListener;
import org.papertrail.listeners.messagelisteners.MessageLogCommandListener;
import org.papertrail.listeners.messagelisteners.MessageLogListener;
import org.papertrail.listeners.voicelisteners.GuildVoiceListener;
import org.tinylog.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
/*
 * The main class of the bot
 */
public class FireRun {
	
	// Register Bouncy Castle as a security provider
	// Required for the PBEWITHSHA256AND256BITAES-CBC-BC encryption algorithm
	private static void registerBouncyCastle() {	
		
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
			Logger.info("Bouncy Castle Security Provider Registered.");
		}
	}

	public static void main(String[] args) throws IOException, SQLException {
		
		registerBouncyCastle();
		
		DatabaseConnector dc = new DatabaseConnector();
		
		ConnectionInitializer ci = new ConnectionInitializer();
		ci.getManager().addEventListener(new AuditLogCommandListener(dc));
		ci.getManager().addEventListener(new AuditLogListener(dc));
		ci.getManager().addEventListener(new GuildVoiceListener(dc));
		ci.getManager().addEventListener(new GuildMemberJoinAndLeaveListener(dc));
		ci.getManager().addEventListener(new ServerBoostListener(dc));
		ci.getManager().addEventListener(new BotKickListener(dc));
		ci.getManager().addEventListener(new MessageLogCommandListener(dc));
		ci.getManager().addEventListener(new MessageLogListener(dc));
		
		ci.getManager().addEventListener(new ServerStatListener());
		ci.getManager().addEventListener(new BotInfoListener());
		ci.getManager().addEventListener(new BotSetupListener());
		ci.getManager().addEventListener(new AnnouncementListener(dc));
		
		/*
		 * This is required only to set up a cron-job to periodically ping this end-point so that
		 * hosting services that spin down after inactivity don't do that anymore
		 */
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/ping", new PingHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	 exchange.sendResponseHeaders(200, -1);
        }
    }

}
