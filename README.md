<div align="center">
  <img
    src="https://github.com/user-attachments/assets/c465fe26-5c31-4e75-b8c5-d73b6311979b"
    alt="logo_diamond"
    width="186"
    height="186"
  />

<h2>PaperTrailBot</h2>
<h4>This logo is drawn using MS Paint because I have never tried using a logo maker before</h4>
</div>

# Table of Contents

* [Overview](#overview)
* [Repositories](#repositories)
* [Using The Bot](#using-the-bot)
* [License](#license)
* [Help](#help)

# Overview

A free and open-source Discord bot that logs the changes made to a server and it's members and logs them to a dedicated
channel.

The following is a non-exhaustive list of events that the bot can log:

AutoMod Events, Onboarding Events,
Invites, Members, Roles, Channels, Threads, Stages, Events, Polls, Messages, Boosts, Emojis, Stickers, Soundboard,
Integrations, Webhooks, Moderation Action, Unusual DMs, Raids and Unknown events.

# Repositories

| Repository                                                         | Description                                                 |
|--------------------------------------------------------------------|-------------------------------------------------------------|
| [PaperTrailBot](https://github.com/eggy03/PaperTrailBot)           | Core bot application                                        |
| [PaperTrailBot Lite](https://github.com/eggy03/PaperTrailBot-Lite) | Same bot application but designed for self-hosting          |
| [PaperTrail SDK](https://github.com/eggy03/papertrail-sdk)         | Java client library for interacting with the API            |
| [PaperTrail API](https://github.com/eggy03/PaperTrail-API-Quarkus) | API used by the core bot application to store server config |

> [!IMPORTANT]
> I've considered putting the project in maintenance mode since I believe it's mostly feature complete.
> I don't plan on introducing or changing anything major.
> However, I will be regularly updating it to fix reported/detected issues, updating dependencies and log new events or
event keys if found, for as long as I can.

# Using The Bot

### By Inviting It

A deployed instance is the easiest way to use this bot. Just invite it to your server and that's all.

Get it from here: https://discord.com/discovery/applications/1381658412550590475

Run the `/setup` slash command to see instructions on how to configure the bot for your server.

### By Self Hosting It

For self-hosting, I'd recommend the [LITE](https://github.com/eggy03/PaperTrailBot-Lite) version of the bot. The `LITE`
version requires lesser services to set up and offers more granular customization. However, you can only use it for a
single server.

If you do not wish to use the `LITE` version, you can self-host the `ORIGINAL` version by reading the
guide [here](/DEPLOYMENT.md).
The guide also mentions the difference between the `LITE` and the `ORIGINAL` versions.

# License

This project is licensed under the [AGPLv3](/LICENSE) license.

# AI Usage Policy

While my code quality is questionable at best and qualifies as slop, no AI/LLM agents have been used to generate any
part of the code. This is my first bot and I wanted to write every part of the code by myself. This will also apply to
future updates as well.

That being said, I did use AI to proofread the Privacy Policy, Security and Terms documents and help maintain a formal
tone because turns out, I really do a bad job at writing in formal tone. I also used it for generating commit messages
sometimes, especially during early stages of development and when I did not know how to write helpful commit messages (I
think I still don't know).

# Help

If you face any problems during self-hosting or have a question that needs to be answered, please feel free to
open an issue in the Issues tab. I will try my best to respond as soon as I can.
