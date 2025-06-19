package org.papertrail.main;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/*
 * Registers all the slash commands places throughout the code
 */
public class SlashCommandRegistrar extends ListenerAdapter {

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		setAuditLogCommands(event.getJDA());
	}

	private void setAuditLogCommands(JDA jda) {

		CommandData auditLogChannelRegistration = Commands.slash("auditlogchannel-set",
				"Registers the channel where audit log updates will be posted (Requires Manage Server Permission)");
		CommandData auditLogChannelFetch = Commands.slash("auditlogchannel-view",
				"Shows the channel where audit log updates gets posted (Requires Manage Server Permission)");
		CommandData auditLogChannelDeletion = Commands.slash("auditlogchannel-remove",
				"Unsets the channel where audit log updates used to be posted (Requires Manage Server Permission)");
		
		CommandData messageLogChannelRegistration = Commands.slash("messagelogchannel-set",
				"Sets the channel where messages are logged (Requires Manage Server Permission)");
		CommandData messageLogChannelFetch = Commands.slash("messagelogchannel-view",
				"Shows the channel messages are logged (Requires Manage Server Permission)");
		CommandData messageLogChannelDeletion = Commands.slash("messagelogchannel-remove",
				"Unsets the channel messages are logged (Requires Manage Server Permission)");
		
		CommandData serverStats = Commands.slash("stats",
				"Provides Server Statistics");
		CommandData botInfo = Commands.slash("about",
				"Provides Bot Info");
		CommandData setup = Commands.slash("setup", "Provides a guide on setting up the bot");
		jda.updateCommands()
				.addCommands(auditLogChannelRegistration,
						auditLogChannelFetch,
						auditLogChannelDeletion,
						messageLogChannelRegistration,
						messageLogChannelFetch,
						messageLogChannelDeletion,
						serverStats,
						botInfo,
						setup)			
				.queue();
	}
}
