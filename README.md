# Self-Hosting Guide

## 1) Setting up the bot
The first thing you will need is your discord application token from the [Discord Developer Portal](https://discord.com/developers/applications).

Then you will need a database URL for the database you set up. It needs to be a `PostgreSQL` database.
The token and the url together will be your environment variables which will be required during setting up the bot.

```env
TOKEN=your-discord-application-token
DATABASEURL=jdbc:postgresql://your-database-url
```

### Using a cloud platform
If the cloud platform you're using supports deployment from GitHub, you can fork a copy of this repository and use it to deploy the bot. A dockerfile is included for platforms supporting Docker. Otherwise, if the cloud service supports `JDK21` and above, it may build the application from the provided `pom.xml` file.

If the cloud platform you are using requires manual configuration, here are the commands for building and running the bot

For Building
```
./mvnw clean package
```
OR, if you have Maven installed
```
mvn clean package
```
This creates the application jar, which gets stored in the project's `target` folder.

You can then configure it to run like this
```
java -jar paper-trail-bot.jar
```
The above also works for local setups. In case of local setups, you will need to store the secrets as environment variables in a .env file in your project's directory.
Make sure the secrets don't get leaked and never commit them to your git branch.

## 2) Setting up the database
Database Tables you will need

![image](https://github.com/user-attachments/assets/5e56e80c-70e0-4bde-8bcf-0b48933a72af)

Assuming you have a default public schema, use the following SQL Queries to create the required tables:

```SQL
CREATE TABLE public.audit_log_table (
	guild_id int8 NOT NULL,
	channel_id int8 NOT NULL,
	CONSTRAINT audit_log_table_pk PRIMARY KEY (guild_id),
	CONSTRAINT audit_log_table_unique UNIQUE (channel_id)
);

CREATE TABLE public.message_log_content_table (
	message_id int8 NOT NULL,
	message_content text NULL,
	author_id int8 NOT NULL,
	created_at timestamp DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'::text) NOT NULL,
	CONSTRAINT message_log_content_table_pk PRIMARY KEY (message_id)
);
CREATE INDEX message_log_content_table_created_at_idx ON public.message_log_content_table USING btree (created_at);

CREATE TABLE public.message_log_registration_table (
	guild_id int8 NOT NULL,
	channel_id int8 NOT NULL,
	CONSTRAINT message_log_registration_table_pk PRIMARY KEY (guild_id),
	CONSTRAINT message_log_registration_table_unique UNIQUE (channel_id)
);
```
NOTE: The following requires the `pg_cron` extension to be available.

Check with your database provider to see if it is supported

You can also set up a cron-job via the `pg_cron` extension to auto-delete messages older than 30 days or your preferred time interval
```SQL
CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
  'daily_log_cleanup',
  '0 2 * * *',  -- 2:00 AM UTC daily
  $$DELETE FROM message_log_content_table WHERE created_at < CURRENT_TIMESTAMP AT TIME ZONE 'UTC' - INTERVAL '30 days';$$
);
```

To check your cron-job runs
```SQL
SELECT * FROM cron.job_run_details
```
