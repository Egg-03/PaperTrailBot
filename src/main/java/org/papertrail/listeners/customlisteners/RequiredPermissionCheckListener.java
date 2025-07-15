package org.papertrail.listeners.customlisteners;

import java.util.EnumMap;
import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RequiredPermissionCheckListener extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if (event.getName().equals("permcheck")) {
			
			Guild guild = event.getGuild();
					
			SelfUser botAsUser = event.getJDA().getSelfUser();
			Role botIntegrationRole = guild.getRoleByBot(botAsUser);
			Member botMember = guild.getMember(botAsUser);
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("PaperTrail Permissions Checker");
			eb.setDescription("Helps determine whether the required permissions are granted for PaperTrail to function properly");
			
			eb.addField("Bot Integration Role Specific Permission", (botIntegrationRole.hasPermission(Permission.VIEW_AUDIT_LOGS) ? "✅" : "❌")+Permission.VIEW_AUDIT_LOGS.getName(), false);
			
			// create a map of required permissions and set all their statuses to false
			Map<Permission, Boolean> requiredPermissions = new EnumMap<>(Permission.class);
			requiredPermissions.putAll(Map.of(
					Permission.MESSAGE_EMBED_LINKS, false,
					Permission.MESSAGE_HISTORY, false,
					Permission.MESSAGE_SEND, false,
					Permission.MESSAGE_SEND_IN_THREADS, false,
					Permission.VIEW_CHANNEL, false));
			
			
			GuildChannel currentChannel = guild.getGuildChannelById(event.getChannelIdLong());
			
			if(currentChannel==null || botMember == null) {
				eb.addField("Channel Specific Permissions","ERROR: Could not determine bot permissions for the given channel. Channel possibly not supported", false);
			} else {
				botMember.getPermissions(currentChannel).forEach(permission ->{
					if(requiredPermissions.containsKey(permission)) {
						requiredPermissions.replace(permission, true);
					}
				});
				
				StringBuilder finalPermissions = new StringBuilder();
				requiredPermissions.entrySet().forEach(permission -> {
					finalPermissions.append(Boolean.TRUE.equals(permission.getValue()) ? "✅" : "❌");
					finalPermissions.append(permission.getKey().getName()+System.lineSeparator());
				});
				
				
				eb.addField("Channel Specific Permissions: "+currentChannel.getAsMention(), finalPermissions.toString(), false);
			}
			
										
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).queue();
		}
	}
}
