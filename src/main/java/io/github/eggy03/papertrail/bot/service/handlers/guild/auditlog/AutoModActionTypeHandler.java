package io.github.eggy03.papertrail.bot.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.bot.service.EmbedCheckingService;
import io.github.eggy03.papertrail.bot.utils.BooleanUtils;
import io.github.eggy03.papertrail.bot.utils.auditlog.AutoModUtils;
import io.github.eggy03.papertrail.sdk.client.AuditLogRegistrationClient;
import io.github.eggy03.papertrail.sdk.entity.AuditLogRegistrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

@ApplicationScoped
@Slf4j
@SuppressWarnings("java:S1192")
public final class AutoModActionTypeHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    private final @NonNull AuditLogRegistrationClient client;
    private final @NonNull EmbedCheckingService embedCheckingService;

    @Inject
    public AutoModActionTypeHandler(@NonNull AuditLogRegistrationClient client, @NonNull EmbedCheckingService embedCheckingService) {
        this.client = client;
        this.embedCheckingService = embedCheckingService;
    }

    @NonNull
    private String getRegisteredChannelId(@NonNull String guildId) {
        return client.getRegisteredGuild(guildId)
                .map(AuditLogRegistrationEntity::getChannelId).orElse(StringUtils.EMPTY);

    }

    @Override
    public void onAutoModerationFlagToChannel(@NonNull GuildAuditLogEntryCreateEvent event) {
        
        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-Mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetMention = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Event: AutoMod Message Flag\nTarget Member: " + targetMention));
        eb.setColor(Color.YELLOW);

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Flagged message will be available in the channel set to receive AutoMod events."),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);

    }

    @Override
    public void onAutoModerationMemberTimeout(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetMention = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Event: AutoMod Member Timeout\nTarget Member: " + targetMention));
        eb.setColor(Color.MAGENTA);

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Timeout Rule and Reason will be available in the channel set to receive AutoMod events."),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onAutoModerationRuleBlockMessage(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | Auto-mod Event");

        User targetUser = ale.getJDA().getUserById(ale.getTargetId());
        String targetMention = (targetUser != null ? targetUser.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Event: AutoMod Message Block\nTarget Member: " + targetMention));
        eb.setColor(Color.ORANGE);

        eb.addField(
                MarkdownUtil.underline("Additional Info"),
                MarkdownUtil.codeblock("Blocked message will be available in the channel set to receive AutoMod events."),
                false
        );

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);

    }

    @Override
    public void onAutoModerationRuleCreate(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Create");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Rule Created By: " + mentionableExecutor + "\nRule Created For: AutoMod"));
        eb.setColor(Color.GREEN);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object newValue = changeValue.getNewValue();

            switch (changeKey) {

                case "enabled" ->
                        eb.addField(MarkdownUtil.underline("Enabled"), "╰┈➤" + BooleanUtils.formatToYesOrNo(newValue), false);

                case "trigger_type" ->
                        eb.addField(MarkdownUtil.underline("Trigger Type"), "╰┈➤" + AutoModUtils.autoModTriggerTypeResolver(newValue), false);

                case "event_type" ->
                        eb.addField(MarkdownUtil.underline("Event Type"), "╰┈➤" + AutoModUtils.autoModEventTypeResolver(newValue), false);

                case "name" -> eb.addField(MarkdownUtil.underline("AutoMod Rule Name "), "╰┈➤" + newValue, false);

                default -> {
                    // ignore everything else
                }
            }
        });

        eb.addField("Additional Info", MarkdownUtil.codeblock("For more info on trigger metadata, actions, exempt roles and channels, visit Safety Setup in your server"), false);
        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onAutoModerationRuleUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Update");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Rule Updated By: " + mentionableExecutor + "\nRule Updated For: AutoMod"));
        eb.setColor(Color.YELLOW);

        // add name of the rule which got updated
        AutoModRule rule = ale.getGuild().retrieveAutoModRuleById(ale.getTargetId()).complete();
        eb.addField(MarkdownUtil.underline("AutoMod Rule Name"), "╰┈➤" + rule.getName(), false);

        eb.addField(MarkdownUtil.underline("Additional Info"), MarkdownUtil.codeblock("For more info on trigger metadata, actions, exempt roles and channel changes, visit Safety Setup in your server"), false);

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onAutoModerationRuleDelete(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Rule Delete");

        User executor = ale.getJDA().getUserById(ale.getUserIdLong());
        String mentionableExecutor = (executor != null ? executor.getAsMention() : ale.getUserId());

        eb.setDescription(MarkdownUtil.quoteBlock("Rule Deleted By: " + mentionableExecutor + "\nRule Deleted For: AutoMod"));
        eb.setColor(Color.RED);

        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();

            if (changeKey.equals("name")) {
                eb.addField(MarkdownUtil.underline("AutoMod Rule Name"), "╰┈➤" + oldValue, false);
            }
        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }

    @Override
    public void onAutoModerationQuarantineUser(@NonNull GuildAuditLogEntryCreateEvent event) {

        String channelIdToSendTo = getRegisteredChannelId(event.getGuild().getId());
        if (channelIdToSendTo.isBlank()) return;

        AuditLogEntry ale = event.getEntry();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Audit Log Entry | AutoMod Quarantine User");

        User targetMember = ale.getJDA().getUserById(ale.getTargetIdLong());
        String mentionableTarget = (targetMember != null ? targetMember.getAsMention() : ale.getTargetId());

        eb.setDescription(MarkdownUtil.quoteBlock("Quarantined User: " + mentionableTarget));
        eb.setColor(Color.ORANGE);

        eb.addField("INFO", "PaperTrail is still learning about this event", false);

        // todo show changes in embed after enough data has been gathered
        ale.getChanges().forEach((changeKey, changeValue) -> {

            Object oldValue = changeValue.getOldValue();
            Object newValue = changeValue.getNewValue();

            log.info("Automod Quarantine User | Change Key : {} | Old Change Value : {} | New Change Value : {}", changeKey, oldValue, newValue);

        });

        eb.setFooter("Audit Log Entry ID: " + ale.getId());
        eb.setTimestamp(ale.getTimeCreated());

        embedCheckingService.checkAndSend(event, eb, channelIdToSendTo);
    }
}
