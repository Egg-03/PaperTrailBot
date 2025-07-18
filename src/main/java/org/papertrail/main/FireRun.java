package org.papertrail.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.sql.SQLException;

import net.dv8tion.jda.api.sharding.ShardManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.papertrail.cleanup.BotKickListener;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.listeners.commandlisteners.AnnouncementCommandListener;
import org.papertrail.listeners.commandlisteners.BotInfoCommandListener;
import org.papertrail.listeners.commandlisteners.ServerStatCommandListener;
import org.papertrail.listeners.commandlisteners.BotSetupCommandListener;
import org.papertrail.listeners.commandlisteners.RequiredPermissionCheckCommandListener;
import org.papertrail.listeners.guildlisteners.ServerBoostListener;
import org.papertrail.listeners.loglisteners.AuditLogListener;
import org.papertrail.listeners.commandlisteners.AuditLogSetupCommandListener;
import org.papertrail.listeners.memberlisteners.GuildMemberJoinAndLeaveListener;
import org.papertrail.listeners.commandlisteners.MessageLogSetupCommandListener;
import org.papertrail.listeners.loglisteners.MessageLogListener;
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
		ShardManager manager = ci.getManager();

		manager.addEventListener(new AuditLogSetupCommandListener(dc));
		manager.addEventListener(new AuditLogListener(dc));

		manager.addEventListener(new MessageLogSetupCommandListener(dc));
		manager.addEventListener(new MessageLogListener(dc));

		manager.addEventListener(new GuildVoiceListener(dc));
		manager.addEventListener(new GuildMemberJoinAndLeaveListener(dc));
		manager.addEventListener(new ServerBoostListener(dc));
		manager.addEventListener(new BotKickListener(dc));

		manager.addEventListener(new ServerStatCommandListener());
		manager.addEventListener(new BotInfoCommandListener());
		manager.addEventListener(new BotSetupCommandListener());
		manager.addEventListener(new AnnouncementCommandListener(dc));
		manager.addEventListener(new RequiredPermissionCheckCommandListener());
		
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
