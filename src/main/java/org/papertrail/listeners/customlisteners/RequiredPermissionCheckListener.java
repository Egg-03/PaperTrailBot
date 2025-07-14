package org.papertrail.listeners.customlisteners;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PermissionOverride;
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
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("PaperTrail Permissions Checker");
			eb.setDescription("Helps determine whether the required permissions are granted for PaperTrail to function properly");
			
			// create a map of required permissions and set all their statuses to false
			Map<Permission, Boolean> requiredPermissions = new EnumMap<>(Permission.class);
			requiredPermissions.putAll(Map.of(
					Permission.MESSAGE_EMBED_LINKS, false,
					Permission.MESSAGE_HISTORY, false,
					Permission.MESSAGE_SEND, false,
					Permission.MESSAGE_SEND_IN_THREADS, false,
					Permission.VIEW_AUDIT_LOGS, false,
					Permission.VIEW_CHANNEL, false));
			
			
			// iterate over the permissions granted to the bot's role and see if it intersects with the required permissions
			// if intersection exists for a particular permission, change it's status to true
			botIntegrationRole.getPermissions().forEach(permission -> {
				if(requiredPermissions.get(permission)!=null) {
					requiredPermissions.replace(permission, true);
				}
			});
			
			// iterate over the required permissions map and see what required permissions have been granted for the bot's integration role
			StringBuilder botRolePermissions = new StringBuilder();
			requiredPermissions.entrySet().forEach(permission -> {
				botRolePermissions.append(Boolean.TRUE.equals(permission.getValue()) ? "✅" : "❌");
				botRolePermissions.append(permission.getKey().getName()+System.lineSeparator());
			});
			
			eb.addField("The following set of required permissions are granted to the bot's role: `"+botIntegrationRole.getName()+"`", botRolePermissions.toString(), false);
			
			GuildChannel currentChannel = guild.getGuildChannelById(event.getChannelIdLong());
			
			if(currentChannel!=null) {	
				boolean botIntegrationRoleDetected = false;
				
				List<PermissionOverride> permissionOverrides = currentChannel.getPermissionContainer().getRolePermissionOverrides();
				for (PermissionOverride override: permissionOverrides) {
					if(override.getRole().equals(botIntegrationRole)) {
						botIntegrationRoleDetected = true;
						break;
					}
						
				}
				
				if(botIntegrationRoleDetected)
					eb.addField("Channel "+currentChannel.getAsMention()+" has a custom override for the bot role: `"+botIntegrationRole.getName()+"`", "Check the role override to make sure the required permissions are granted", false);
				else
					eb.addField("⚠️ WARNING: Channel "+currentChannel.getAsMention()+" does not have any overrides for the bot role: `"+botIntegrationRole.getName()+"`", "This shouldn't be a problem if the channel is not a private channel. If it is a private channel, PaperTrail may not have access to this channel", false);
			}
			
			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).queue();
		}
	}
}
