# Self-Hosting Guide

## 1) Setting up the bot
### Step 1: Get Required Secrets

You will need two environment variables to run the bot:

- `TOKEN` – Your Discord bot token from the [Discord Developer Portal](https://discord.com/developers/applications)
- `DATABASEURL` – A PostgreSQL connection URL (format: `jdbc:postgresql://host:port/dbname`)
> 💡 You will receive the DATABASEURL from your database hosting provider once you've set up your database. This value is essential and should be kept secure.

Create a `.env` file with the following:

```env
# .env file
TOKEN=your-discord-application-token
DATABASEURL=jdbc:postgresql://your-database-url
```

> ⚠️ Never commit your `.env` file to version control. Add it to `.gitignore`:

```gitignore
.env
```


### Step 2: Deployment Options
> Fork this repository to your GitHub account, connect it to your preferred cloud platform, and configure your environment variables either through the platform's dashboard or secrets manager. Some services may also support adding secrets directly from your `.env` file.
#### A. Cloud Platforms with GitHub + Docker Support

- These can auto-deploy using the included `Dockerfile`

#### B. Platforms with GitHub + Java Support (No Docker)

- These can build the project using the `pom.xml` if JDK 21+ is available

#### Build and Run (for local/manual deployment)

If deploying manually or running locally:

**Build the JAR:**

```sh
./mvnw clean package   # If using Maven Wrapper
```
 OR
```sh
mvn clean package       # If Maven is installed globally
```
This creates a runnable JAR file in the `target/` folder, named `paper-trail-bot.jar`.

**Run the JAR:**

```sh
java -jar target/paper-trail-bot.jar
```

> Ensure you have JDK 21 or later installed.

> For local deployments, make sure your `.env` file containing the secrets is placed in the project's base directory


## 2) Setting up the database

You’ll need a PostgreSQL database with the following tables:
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
### Optional: Automatic Message Cleanup with `pg_cron`

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

> If `pg_cron` is not supported, consider using a scheduled task in your app or CI/CD platform to run cleanup logic.

---

Feel free to contribute to this guide or raise issues on GitHub if you get stuck!

