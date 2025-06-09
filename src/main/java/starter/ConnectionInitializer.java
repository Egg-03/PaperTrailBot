package starter;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class ConnectionInitializer {
	
	private final ShardManager manager;
	
	
	public ConnectionInitializer() {
		
		String token = EnvConfig.get("TOKEN");
		
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EXPRESSIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MODERATION);
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.watching("Logs"));
		
		//cache all users of the bot
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.setChunkingFilter(ChunkingFilter.ALL);
		//you will still need builder.enableCache() to cache user activities and status
		builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.ONLINE_STATUS);
		
		manager = builder.build();
	}

	public ShardManager getManager() {
		return manager;
	}
}
