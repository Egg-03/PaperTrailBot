# Privacy Policy

This Discord bot was built as an open source project with a privacy-first mindset. The Service is provided at no cost and is intended for use as-is.

This page is used to inform users regarding our policies with the collection, use, and disclosure of information for anyone choosing to use the bot.

If you choose to use this bot in your Discord server, you agree to the collection and use of information in accordance with this policy. The data collected is strictly limited to what is necessary for the bot's core functionality and moderation features. We do not use or share your data with anyone except as described here.

---

## Information Collection and Use

The bot only stores data when specific features are explicitly enabled by server administrators:

### If **Message Logging** is enabled:
- Encrypted message content (not readable by us)
- Message ID
- Author ID (Discord user ID)
- Channel ID
- Guild ID
- Timestamp (created_at)

Message content is encrypted before storage and cannot be decrypted by the bot operator.

### If **only Audit Logging** is enabled:
- Guild ID
- Channel ID where audit logs should be sent (one per guild)

No message content or user data is stored in this case.

The stored data is used solely for server moderation purposes.

---

## Data Retention

- All stored message data is automatically deleted after **30 days**.
- No data is permanently retained or used for analytics.
- Configuration data (e.g., log channel IDs) is kept until the server administrator removes it or disables the feature.

---

## Log Data

In case of runtime errors, the bot may log basic diagnostic information such as error messages, timestamps, and internal event states to assist with debugging. These logs do not contain any personal user data and are not persisted long-term.

---

## Security

We take data protection seriously:
- All sensitive data (e.g., message content) is encrypted before being saved.
- No decryption keys are stored on our servers.
- The database is securely hosted using Aiven PostgreSQL wiht UpCloud as it's provider.

However, no method of transmission over the internet or method of electronic storage is 100% secure, and we cannot guarantee absolute security.

---

## Children’s Privacy

This bot is not intended for users under the age of 13. It does not knowingly collect any personal information from children. If it is discovered that such data has been inadvertently stored, it will be deleted immediately upon request.

---

## User Rights (GDPR Compliance)

If you are located in the EU/EEA, you have the following rights under the General Data Protection Regulation (GDPR):

- **Right of Access**: You may request what data (if any) is associated with your Discord user ID.
- **Right to Erasure**: You may request deletion of your data from the database.

Due to the encryption of message content, we are unable to provide decrypted content under any circumstances.

To request access or deletion, please contact us using the method below.

---

## Contact

For privacy-related questions or GDPR requests:
- GitHub: [Submit an issue or discussion](https://github.com/Egg-03/PaperTrailBot/issues)
- Email: `eggzerothree@proton.me`

---

## Changes to This Privacy Policy

This policy may be updated from time to time. Changes will be posted here and are effective immediately once published.

---

