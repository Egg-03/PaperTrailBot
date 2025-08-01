package org.papertrail.database;

import org.jooq.DSLContext;
import org.papertrail.utilities.MessageEncryption;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.selectOne;
import static org.jooq.impl.DSL.table;
import static org.papertrail.database.Schema.AUTHOR_ID_COLUMN;
import static org.papertrail.database.Schema.MESSAGE_CONTENT_COLUMN;
import static org.papertrail.database.Schema.MESSAGE_ID_COLUMN;

public class MessageDataAccess {

    private final DSLContext dsl;

    public MessageDataAccess (DSLContext dsl) {
        this.dsl = dsl;
    }

    public void logMessage(String messageId, String messageContent, String authorId) {

        dsl.insertInto(table(Schema.MESSAGE_LOG_CONTENT_TABLE))
                .columns(field(MESSAGE_ID_COLUMN), field(MESSAGE_CONTENT_COLUMN), field(AUTHOR_ID_COLUMN))
                .values(Long.parseLong(messageId), MessageEncryption.encrypt(messageContent), Long.parseLong(authorId))
                .onConflictDoNothing()
                .execute();
    }

    public AuthorAndMessageEntity retrieveAuthorAndMessage (String messageId) {

        return dsl.select(field(AUTHOR_ID_COLUMN), field(MESSAGE_CONTENT_COLUMN))
                .from(table(Schema.MESSAGE_LOG_CONTENT_TABLE))
                .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
                .fetchOne(r -> new AuthorAndMessageEntity(String.valueOf(r.get(field(AUTHOR_ID_COLUMN))), MessageEncryption.decrypt(String.valueOf(r.get(field(MESSAGE_CONTENT_COLUMN))))));
    }

    public boolean messageExists (String messageId) {

        return dsl.fetchExists(
                selectOne().from(table(Schema.MESSAGE_LOG_CONTENT_TABLE))
                        .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
        );
    }

    public void updateMessage (String messageId, String messageContent) {

        dsl.update(table(Schema.MESSAGE_LOG_CONTENT_TABLE))
                .set(field(MESSAGE_CONTENT_COLUMN), MessageEncryption.encrypt(messageContent))
                .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
                .execute();
    }

    public void deleteMessage (String messageId) {

        dsl.deleteFrom(table(Schema.MESSAGE_LOG_CONTENT_TABLE))
                .where(field(MESSAGE_ID_COLUMN).eq(Long.parseLong(messageId)))
                .execute();
    }
}
