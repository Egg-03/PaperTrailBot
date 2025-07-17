package org.papertrail.database;

import org.jooq.DSLContext;
import org.papertrail.utilities.MessageEncryption;

import java.util.Map;
import java.util.Objects;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.table;

public class MessageDataAccess {

    private final DSLContext dsl;

    private static final String MESSAGE_ID_COLUMN = "message_id";
    private static final String MESSAGE_CONTENT_COLUMN = "message_content";
    private static final String AUTHOR_ID_COLUMN = "author_id";

    public MessageDataAccess (DSLContext dsl) {
        this.dsl = dsl;
    }

    public void logMessage(String messageId, String messageContent, String authorId) {

        dsl.insertInto(table(TableNames.MESSAGE_LOG_CONTENT_TABLE))
                .columns(field(MESSAGE_ID_COLUMN), field(MESSAGE_CONTENT_COLUMN), field(AUTHOR_ID_COLUMN))
                .values(Long.parseLong(messageId), MessageEncryption.encrypt(messageContent), Long.parseLong(authorId))
                .onConflictDoNothing()
                .execute();
    }

    public Map<String, String> retrieveAuthorAndMessage (String messageId) {

        return dsl.select(field(AUTHOR_ID_COLUMN), field(MESSAGE_CONTENT_COLUMN))
                .from(table(TableNames.MESSAGE_LOG_CONTENT_TABLE))
                .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
                .fetchOne(r ->
                        Map.of(String.valueOf(r.get(field(AUTHOR_ID_COLUMN))), Objects.requireNonNull(MessageEncryption.decrypt(String.valueOf(r.get(field(MESSAGE_CONTENT_COLUMN)))))));
    }

    public boolean messageExists (String messageId) {

        return dsl.fetchExists(
                selectOne().from(table(TableNames.MESSAGE_LOG_CONTENT_TABLE))
                        .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
        );
    }

    public void updateMessage (String messageId, String messageContent) {

        dsl.update(table(TableNames.MESSAGE_LOG_CONTENT_TABLE))
                .set(field(MESSAGE_CONTENT_COLUMN), MessageEncryption.encrypt(messageContent))
                .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
                .execute();
    }

    public void deleteMessage (String messageId) {

        dsl.deleteFrom(table(TableNames.MESSAGE_LOG_CONTENT_TABLE))
                .where(field(MESSAGE_ID_COLUMN).eq(messageId))
                .execute();
    }
}
