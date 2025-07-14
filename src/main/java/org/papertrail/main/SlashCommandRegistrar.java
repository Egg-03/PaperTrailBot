package org.papertrail.main;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
				"Set audit log channel");
		CommandData auditLogChannelFetch = Commands.slash("auditlogchannel-view",
				"Show audit log channel");
		CommandData auditLogChannelDeletion = Commands.slash("auditlogchannel-remove",
				"Unset audit log channel");
		
		CommandData messageLogChannelRegistration = Commands.slash("messagelogchannel-set",
				"Set message log channel");
		CommandData messageLogChannelFetch = Commands.slash("messagelogchannel-view",
				"Show message log channel");
		CommandData messageLogChannelDeletion = Commands.slash("messagelogchannel-remove",
				"Unset message log channel");
		
		CommandData serverStats = Commands.slash("stats",
				"Provides Server Statistics");
		CommandData botInfo = Commands.slash("about",
				"Provides Bot Info");
		CommandData setup = Commands.slash("setup", "Provides a guide on setting up the bot");
		
		CommandData announcement = Commands.slash("announcement", "Developer Only")
				.addOption(OptionType.STRING, "type", "Announcement Type", true)
				.addOption(OptionType.STRING, "description", "Announcement Description", true)
				.addOption(OptionType.STRING, "detail", "Detailed Information", true)
				.addOption(OptionType.STRING, "extra", "Extra Notes", false);
		
		CommandData permCheck = Commands.slash("permcheck", "Checks if the bot has the necessary permissions to operate");
		
		jda.updateCommands()
				.addCommands(auditLogChannelRegistration,
						auditLogChannelFetch,
						auditLogChannelDeletion,
						messageLogChannelRegistration,
						messageLogChannelFetch,
						messageLogChannelDeletion,
						serverStats,
						botInfo,
						setup,
						announcement,
						permCheck)			
				.queue();
	}
}
