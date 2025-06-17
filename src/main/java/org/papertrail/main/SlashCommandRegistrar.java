package org.papertrail.main;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class SlashCommandRegistrar extends ListenerAdapter {

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		setAuditLogCommands(event.getJDA());
	}

	private void setAuditLogCommands(JDA jda) {

		CommandData auditLogChannelRegistration = Commands.slash("ralc",
				"Registers the channel where audit log updates will be posted (Requires Manage Server Permission)");
		CommandData auditLogChannelFetch = Commands.slash("galc",
				"Shows the channel where audit log updates gets posted (Requires Manage Server Permission)");
		CommandData auditLogChannelDeletion = Commands.slash("ualc",
				"Unsets the channel where audit log updates used to be posted (Requires Manage Server Permission)");
		CommandData serverStats = Commands.slash("stats",
				"Provides Server Statistics");
		jda.updateCommands()
				.addCommands(auditLogChannelRegistration, auditLogChannelFetch, auditLogChannelDeletion, serverStats)			
				.queue();
	}
}
