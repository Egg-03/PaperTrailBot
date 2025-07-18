package org.papertrail.listeners.guildlisteners;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;
import org.papertrail.database.DatabaseConnector;
import org.papertrail.database.TableNames;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerBoostListener extends ListenerAdapter {

	private final Executor vThreadPool;
	private final DatabaseConnector dc;

	public ServerBoostListener(DatabaseConnector dc, Executor vThreadPool) {
		this.dc=dc;
		this.vThreadPool = vThreadPool;
	}
	
	@Override
	public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {

		vThreadPool.execute(()->{
			// this will return a non-null text id if a channel was previously registered in the database
			// server boost logs are mapped to audit log table
			String registeredChannelId=dc.getGuildDataAccess().retrieveRegisteredChannel(event.getGuild().getId(), TableNames.AUDIT_LOG_TABLE);

			if(registeredChannelId==null ||registeredChannelId.isBlank()) {
				return;
			}

			Member member = event.getMember();
			Guild guild = event.getGuild();

			String mentionableMember = member.getAsMention();

			OffsetDateTime newBoostTime = event.getNewTimeBoosted(); // Will be null if the member stopped boosting

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🚀 Server Boost Event ");
			eb.setThumbnail(guild.getIconUrl());

			if (newBoostTime != null) {
				eb.setDescription("🎉 **" + guild.getName() + "** has been boosted!");
				eb.setColor(Color.PINK);
				eb.addField("🔋 Booster Gained", "╰┈➤"+mentionableMember+" has started boosting your server", false);
				eb.addField("📈 Total Boosts In The Server", "╰┈➤"+guild.getBoostCount(), false);
				eb.addField("🎖️ Current Boost Tier", "╰┈➤"+ guild.getBoostTier(), false);
			} else {
				eb.setDescription("⚠️ **" + guild.getName() + "** has lost a boost.");
				eb.setColor(Color.GRAY);
				eb.addField("🪫 Booster Lost", "╰┈➤"+mentionableMember+" has removed their boost from your server", false);
				eb.addField("📉 Remaining Boosts In The Server", "╰┈➤"+guild.getBoostCount(), false);
				eb.addField("🎖️ Current Boost Tier", "╰┈➤"+ guild.getBoostTier(), false);
				eb.addField("Notice", "Boosts remain active for a period even after a member stops boosting, so the server's boost count doesn't update immediately.", false);
			}

			eb.setFooter("Server Boost Detection");
			eb.setTimestamp(Instant.now());

			MessageEmbed mb = eb.build();
			Objects.requireNonNull(event.getGuild().getTextChannelById(registeredChannelId)).sendMessageEmbeds(mb).queue();
		});
	}
}
