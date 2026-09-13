package io.github.eggy03.papertrail.bot.service.handlers.guild.auditlog;

import io.github.eggy03.papertrail.bot.listeners.guild.auditlog.GuildAuditLogEntryEventListener;
import lombok.NonNull;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

/**
 * <p>
 * Abstract base class for routing {@link GuildAuditLogEntryCreateEvent} {@link ActionType}
 * to their appropriate handler methods.
 * Routing is performed by
 * {@link #handleActionType(GuildAuditLogEntryCreateEvent)}.
 * </p>
 *
 * <p>
 * {@link GuildAuditLogEntryCreateEvent} is emitted for all actions that
 * appear in a Discord guild's audit log. The specific audit log action
 * (e.g. BAN, KICK, UNBAN) can be determined from {@link ActionType}.
 * This class exposes overridable methods for each supported
 * {@link ActionType}, allowing subclasses to implement only the handlers
 * they are interested in.
 * </p>
 *
 * <p>
 * Subclasses may override only the action type handlers they require. The
 * {@code handleActionType(...)} method is responsible for determining which
 * handler method should be invoked for a given action type.
 * </p>
 *
 * <p>
 * If multiple subclasses are registered as CDI beans and are iterated
 * through {@code Instance<AbstractGuildAuditLogEntryCreateEventActionTypeHandler>}, each handler
 * instance will independently receive and process the event.
 * </p>
 *
 * <p>
 * For example, if two subclasses override {@code onBan()} for processing BAN action types, both
 * implementations will be executed when a BAN audit log event is
 * processed, assuming both handler instances are iterated and invoked.
 * </p>
 *
 * <p>
 * The design philosophy is inspired by JDA's
 * {@link net.dv8tion.jda.api.hooks.ListenerAdapter}, providing a simple
 * inheritance-based mechanism for handling only the events that are
 * relevant to a particular implementation.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * @ApplicationScoped
 * public class MyHandler extends AbstractGuildAuditLogEntryCreateEventActionTypeHandler {
 *
 *     @Override
 *     public void onBan(@NonNull GuildAuditLogEntryCreateEvent event) {
 *         // your logic here
 *     }
 * }
 * }</pre>
 *
 * <p>
 * Calling {@code handleActionType(...)} on {@code MyHandler} will
 * automatically route BAN audit log events to the overridden
 * {@code onBan()} method.
 * </p>
 *
 * <p>
 * Multiple handler beans may also extend this class simultaneously.
 * These handlers can be dynamically resolved and iterated using:
 * </p>
 *
 * <pre>{@code
 * @Inject
 * Instance<AbstractGuildAuditLogEntryCreateEventActionTypeHandler> eventHandlerInstance;
 *
 * eventHandlerInstance.forEach(handler ->
 *     handler.handleActionType(event)
 * );
 * }</pre>
 *
 * <p>
 * A concrete example of this pattern can be found in
 * {@link GuildAuditLogEntryEventListener}.
 * </p>
 */
public abstract class AbstractGuildAuditLogEntryCreateEventActionTypeHandler {

    // handler methods for ActionType

    public void onAutoModerationFlagToChannel(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onAutoModerationMemberTimeout(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onAutoModerationQuarantineUser(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onAutoModerationRuleBlockMessage(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onAutoModerationRuleCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onAutoModerationRuleUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onAutoModerationRuleDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onKick(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onPrune(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onBan(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onUnban(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onBotAdd(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onChannelCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onChannelUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onChannelDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onChannelOverrideCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onChannelOverrideUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onChannelOverrideDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onRoleCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onRoleUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onRoleDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onEmojiCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onEmojiUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onEmojiDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onStickerCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onStickerUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onStickerDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onGuildUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onGuildProfileUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onIntegrationCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onIntegrationUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onIntegrationDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onApplicationCommandPrivilegesUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onInviteCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onInviteUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onInviteDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMemberRoleUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMemberUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMemberVoiceKick(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMemberVoiceMove(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMessagePin(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMessageUnpin(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onScheduledEventCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onScheduledEventUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onScheduledEventDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onStageInstanceCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onStageInstanceUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onStageInstanceDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onThreadCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onThreadUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onThreadDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onVoiceChannelStatusUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onVoiceChannelStatusDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onWebhookCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onWebhookUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onWebhookDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onSoundboardSoundCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onSoundboardSoundUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onSoundboardSoundDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onOnboardingCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onOnboardingUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onOnboardingPromptCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onOnboardingPromptUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onOnboardingPromptDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onHomeSettingsCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onHomeSettingsUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMessageBulkDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMessageCreate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMessageUpdate(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    public void onMessageDelete(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    /**
     * For {@link ActionType}s not defined in JDA yet
     */
    public void onUnknownActionType(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    /**
     * For {@link ActionType}s which are defined in JDA but not implemented by PaperTrail
     */
    public void onUnimplementedActionType(@NonNull GuildAuditLogEntryCreateEvent event) {
    }

    /**
     * <p>
     * Routes a {@link GuildAuditLogEntryCreateEvent} to its corresponding
     * handler method based on the {@link ActionType} of the audit log entry.
     * </p>
     *
     * <p>
     * This method is marked as {@code final} and cannot be overridden
     * </p>
     *
     * @param event the audit log event received from JDA
     */
    public final void handleActionType(@NonNull GuildAuditLogEntryCreateEvent event) {
        ActionType action = event.getEntry().getType();

        switch (action) {
            case AUTO_MODERATION_FLAG_TO_CHANNEL -> onAutoModerationFlagToChannel(event);
            case AUTO_MODERATION_MEMBER_TIMEOUT -> onAutoModerationMemberTimeout(event);
            case AUTO_MODERATION_QUARANTINE_USER -> onAutoModerationQuarantineUser(event);
            case AUTO_MODERATION_RULE_BLOCK_MESSAGE -> onAutoModerationRuleBlockMessage(event);
            case AUTO_MODERATION_RULE_CREATE -> onAutoModerationRuleCreate(event);
            case AUTO_MODERATION_RULE_UPDATE -> onAutoModerationRuleUpdate(event);
            case AUTO_MODERATION_RULE_DELETE -> onAutoModerationRuleDelete(event);

            case KICK -> onKick(event);
            case PRUNE -> onPrune(event); // never happened to trigger
            case BAN -> onBan(event);
            case UNBAN -> onUnban(event);
            case BOT_ADD -> onBotAdd(event);

            case CHANNEL_CREATE -> onChannelCreate(event);
            case CHANNEL_UPDATE -> onChannelUpdate(event);
            case CHANNEL_DELETE -> onChannelDelete(event);
            case CHANNEL_OVERRIDE_CREATE -> onChannelOverrideCreate(event);
            case CHANNEL_OVERRIDE_UPDATE -> onChannelOverrideUpdate(event);
            case CHANNEL_OVERRIDE_DELETE -> onChannelOverrideDelete(event);

            case ROLE_CREATE -> onRoleCreate(event);
            case ROLE_UPDATE -> onRoleUpdate(event);
            case ROLE_DELETE -> onRoleDelete(event);

            case EMOJI_CREATE -> onEmojiCreate(event);
            case EMOJI_UPDATE -> onEmojiUpdate(event);
            case EMOJI_DELETE -> onEmojiDelete(event);

            case STICKER_CREATE -> onStickerCreate(event);
            case STICKER_UPDATE -> onStickerUpdate(event);
            case STICKER_DELETE -> onStickerDelete(event);

            case GUILD_UPDATE -> onGuildUpdate(event);
            case GUILD_PROFILE_UPDATE -> onGuildProfileUpdate(event);

            case INTEGRATION_CREATE -> onIntegrationCreate(event);
            case INTEGRATION_UPDATE -> onIntegrationUpdate(event); // never triggers
            case INTEGRATION_DELETE -> onIntegrationDelete(event);
            case APPLICATION_COMMAND_PRIVILEGES_UPDATE -> onApplicationCommandPrivilegesUpdate(event);

            case INVITE_CREATE -> onInviteCreate(event);
            case INVITE_UPDATE -> onInviteUpdate(event); // never triggers
            case INVITE_DELETE -> onInviteDelete(event);

            case MEMBER_ROLE_UPDATE -> onMemberRoleUpdate(event);
            case MEMBER_UPDATE -> onMemberUpdate(event);

            case MEMBER_VOICE_KICK -> onMemberVoiceKick(event);
            case MEMBER_VOICE_MOVE -> onMemberVoiceMove(event);

            case MESSAGE_PIN -> onMessagePin(event);
            case MESSAGE_UNPIN -> onMessageUnpin(event);

            // these seemingly don't trigger properly, or are unreliable
            case MESSAGE_BULK_DELETE -> onMessageBulkDelete(event);
            case MESSAGE_CREATE -> onMessageCreate(event);
            case MESSAGE_DELETE -> onMessageDelete(event);
            case MESSAGE_UPDATE -> onMessageUpdate(event);

            case SCHEDULED_EVENT_CREATE -> onScheduledEventCreate(event);
            case SCHEDULED_EVENT_UPDATE -> onScheduledEventUpdate(event);
            case SCHEDULED_EVENT_DELETE -> onScheduledEventDelete(event);

            case STAGE_INSTANCE_CREATE -> onStageInstanceCreate(event);
            case STAGE_INSTANCE_UPDATE -> onStageInstanceUpdate(event);
            case STAGE_INSTANCE_DELETE -> onStageInstanceDelete(event);

            case THREAD_CREATE -> onThreadCreate(event);
            case THREAD_UPDATE -> onThreadUpdate(event);
            case THREAD_DELETE -> onThreadDelete(event);

            case VOICE_CHANNEL_STATUS_UPDATE -> onVoiceChannelStatusUpdate(event);
            case VOICE_CHANNEL_STATUS_DELETE -> onVoiceChannelStatusDelete(event);

            case WEBHOOK_CREATE -> onWebhookCreate(event);
            case WEBHOOK_UPDATE -> onWebhookUpdate(event);
            case WEBHOOK_REMOVE -> onWebhookDelete(event);

            case SOUNDBOARD_SOUND_CREATE -> onSoundboardSoundCreate(event);
            case SOUNDBOARD_SOUND_UPDATE -> onSoundboardSoundUpdate(event);
            case SOUNDBOARD_SOUND_DELETE -> onSoundboardSoundDelete(event);

            case ONBOARDING_CREATE -> onOnboardingCreate(event); // unreliable
            case ONBOARDING_UPDATE -> onOnboardingUpdate(event);

            case ONBOARDING_PROMPT_CREATE -> onOnboardingPromptCreate(event);
            case ONBOARDING_PROMPT_UPDATE -> onOnboardingPromptUpdate(event);
            case ONBOARDING_PROMPT_DELETE -> onOnboardingPromptDelete(event);

            case HOME_SETTINGS_CREATE -> onHomeSettingsCreate(event);
            case HOME_SETTINGS_UPDATE -> onHomeSettingsUpdate(event);

            case UNKNOWN -> onUnknownActionType(event);

            default -> onUnimplementedActionType(event);
        }
    }
}
