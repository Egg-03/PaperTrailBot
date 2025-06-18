package org.papertrail.listeners.customlisteners;

import java.awt.Color;
import java.time.Instant;

import org.papertrail.version.VersionInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SetupListener extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

		if (event.getName().equals("setup")) {

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("🛠️ Setup Guide for " + VersionInfo.APPNAME);
			eb.setDescription("Welcome to **" + VersionInfo.APPNAME + "**!\nHere's how to get started using the bot in your server.");
			eb.setColor(Color.decode("#38e8bc"));

			eb.addField("1️⃣ Register Audit Log Channel",
					"- Use `/ralc` to **register the current channel** for receiving audit log updates.\n╰┈➤ User must have `Manage Server` permission.",
					false);

			eb.addField("2️⃣ View Registered Channel",
					"- Use `/galc` to **check which channel** is currently registered for audit logs.\nThis is helpful if you're unsure where logs are going.\n╰┈➤ User must have `Manage Server` permission.",
					false);

			eb.addField("3️⃣ Unregister Audit Log Channel",
					"- Use `/ualc` to **unset the audit log channel** if you wish to stop logging or switch to another one.\n╰┈➤ User must have `Manage Server` permission.",
					false);

			eb.addField("4️⃣ View Server Stats",
					"- Use `/stats` to **get useful server information** like member count, channel count, and more.",
					false);

			eb.addField("5️⃣ Bot Information",
					"- Use `/about` to **view bot details**, including author info and version.",
					false);
			
			String tips = """
					- Make sure the bot has proper permissions for it to work correctly.
					- Commands only work in servers (guilds), not in DMs.
					- Re-register the audit log channel after deletion or permission changes.""";
			
			eb.addField("💡 Tips",tips,false);
			eb.addField("📬 Need help?", "Create an issue on [GitHub](" + VersionInfo.PROJECT_ISSUE_LINK+")", false);
			eb.setFooter(VersionInfo.APPNAME+" "+VersionInfo.VERSION);
			eb.setTimestamp(Instant.now());

			MessageEmbed mb = eb.build();
			event.replyEmbeds(mb).setEphemeral(false).queue();
		}
	}
}

