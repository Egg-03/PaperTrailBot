package org.papertrail.main;

import org.papertrail.utilities.EnvConfig;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
/*
 * This class initializes the connection to discord and configures all the necessary intents
 */
public class ConnectionInitializer {
	
	// Manages multiple shards (instances) of the bot
	private final ShardManager manager;
		
	public ConnectionInitializer() {
		
		String token = EnvConfig.get("TOKEN");
		
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
		builder.enableIntents(GatewayIntent.SCHEDULED_EVENTS, 
				GatewayIntent.AUTO_MODERATION_EXECUTION,
				GatewayIntent.AUTO_MODERATION_CONFIGURATION,
				GatewayIntent.MESSAGE_CONTENT,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_EXPRESSIONS,
				GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_MODERATION);
		
		builder.setStatus(OnlineStatus.ONLINE);
		
		//cache all users of the bot
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.setChunkingFilter(ChunkingFilter.ALL);
		//you will still need builder.enableCache() to cache user activities and status
		builder.enableCache(CacheFlag.ACTIVITY,
				CacheFlag.CLIENT_STATUS,
				CacheFlag.EMOJI,
				CacheFlag.STICKER,
				CacheFlag.ONLINE_STATUS,
				CacheFlag.MEMBER_OVERRIDES,
				CacheFlag.ROLE_TAGS,
				CacheFlag.SCHEDULED_EVENTS);
		
		manager = builder.build();
		manager.addEventListener(new ActivityUpdateListener(manager));
		 manager.addEventListener(new SlashCommandRegistrar()); 
		// re-enable it only when adding/updating/deleting commands
	}
	
	// returns the manager that manages multiple shards (instances) of the bot
	public ShardManager getManager() {
		return manager;
	}
	
}
