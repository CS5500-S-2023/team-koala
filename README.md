# __Koala Reminder Bot__
Koala Reminder is a discord chatbot that reminds discord users of their packages or events. Discord users need to interact with the bot via commands.

# Functionalities
Reminds users of status updates of their packages via discord private messages.
- Currently, this bot supports creating, updating, updating and deleting packages.
- Once users have created packages with the bot, they will be notified daily (from 8:00 a.m. to 8:00 p.m. PST) if there are any updates.
    - Adaptability to users in time zones that is not PST is in the plan.



Reminds users of events via discord private messages
- Currently, this bot only supports creating reminders/events that repeats or don't repeat. User facing delete functionality is not yet implemented. Regarding deleting / stopping the reminders please refer to details in [Reminder Commands](#reminder-commands) section below.
- This bot supports reminders in all 24 time zones with the default time zone being GMT-7 (PDT).
> Note: This documentation uses the word **reminder** interchagebaly with the word **event**.


# Pakcage Commands
### `/add-package`
- Options:

    - tracking_number (required): the tracking number of a package/shipment.

    - name (optional): a name for the package, strongly suggested for identifying packages.
      - When Koala Reminder bot sends users updates of their packages, they can easily recognize their packages by name, otherwise by tracking number.

- Users will then be prompted to select the carrier of the package (input required).
    - Supported carriers:

        UPS, DHL, FedEx, USPS, LaserShip, China-post, China Ems International, GLS, Canada Post, Purolator

        > Note:<br>
        The above 10 carriers are what we think are popular based on personal experiences and online searches.<br>
        For the current UI design, we can support at most 25 carriers. <br>
        Feel free to suggest any carriers you want to include and we will respond to you if that carrier is supported.

- Confirmation messages
    - If the combination of the tracking number and carrier id is valid, users will receive a confirmation message indicating success.
    - Otherwise, users will receive a message indicating the failure of adding the package.

### `/display-packages`
- To see all packages with latest status that current users created

### `/delete-package`
- Options:
    - package ID (required) to delete a specific package that current users have created
- Confirmation messages:
    - If


# Reminder Commands
### `/add-reminder`
- Options:

    - Required:
        - title: the title of the event you want to be reminded of.

        - reminder-time: when the reminded event starts (hh:mm 24 hour clock format, 2 digit for hour and minute each; e.g. 14:00).

    - Optional:
        - reminder-offset: how much earlier do you want us to remind you (in minutes, default: 0).

        - time-zone: time zone of the reminder, defaults to GMT-7 (PDT).

        - delay: how many days later do you expect the first reminder (default: 0).

        - interval-unit: time unit of the repeat - interval (no value by default = reminder does not repeat).

        - repeat-interval: interval between 2 reminder messages (no value by default = reminder does not repeat).
        <br>
        > Note:
        interval-unit and repeat interval should both be specified to repeat your reminder, if only one of them is specified you
        will receive an error message.

- Confirmation Messages
    - After successfully adding a reminder, the bot will send back the reminder info users entered as a receipt.

### `/display_reminder`
- Coming soon... Developers please go to MongoDB to see the reminders.

### `/delete_reminder`
- Coming soon... Developers please go to MongoDB to delete reminders.
> Note: reminders with repeat intervals will repeat until they are deleted.<br>If you don't want it anymore, delete it from the database, or if using our deployed version, ping the developers to delete it for you or you will be blessed with endless love from our bot 😘

Find out more about the design decisions related to this feature [here](https://docs.google.com/document/d/1Chwb-RiiAHWGnp7b_wHYfwkcA4QZn1ouDYVdsjwNhBg/edit?usp=sharing)

<br>


# Development
This is a Gradle project written in Java 17. The project utilizes Dagger framework for dependency injection, Discord JDA for Discord interaction and MongoDB for data storage, and a third-party service KeyDelivery for package tracking, running on fly.io. <br><br>
The skeleton of this project is set up by @abl at this [repo](https://github.com/abl/bot) 👏
## Deploying this Project to a Bot Application on Discord
- Please refer to this [link](https://www.xda-developers.com/how-to-create-discord-bot/) to create a bot application and generate bot tokens.
- In the terminal, at the root path of the cloned project in your local environment / online codespace, do export BOT_TOKEN=[your generated bot token].
- In the terminal, type in ./gradlew run, and the code will be running on your bot application.

## Connecting to MongoDB
- Please refer to this [document](https://docs.google.com/document/d/1VnlAC4TKOfoEuJhqoeGt6jn3dgVf3ulPvvVPQkEQnFE/) to set up a connection string in MongoDB Atlas.
- In the terminal, at the root path of the cloned project in your local environment / online codespace, do export MONGODB_URI=[your MongoDB connection string].

## Connecting to the third-party service - KeyDelivery
- Please register an account on [KeyDelivery](https://www.kd100.com/docs/keydelivery-api)
- After logging in, retrieve the unique SECRET and API_KEY. Then do export KEY_DELIVERY_API_SECRET=<your_secret> <br>
export KEY_DELIVERY_API_KEY=<your_api_key>

 ## Deploying on fly.io
 - Please register an account on [fly.io](https://fly.io/), install [flyctl](https://fly.io/docs/hands-on/install-flyctl/), and refer to [this document](https://fly.io/docs/flyctl/apps/) to create an app
 - Then refer to [API_TOKENS](https://fly.io/docs/app-guides/continuous-deployment-with-github-actions/#api-tokens-and-deployment) to create your deploy token to authorize your applications and do export FLY_API_TOKEN=<your_API_TOKEN>

# Documentation
- All documents related to this project can be found [here](https://drive.google.com/drive/folders/1KwwUDZ7SErRCVsoH6g6h0l3_oXPF8htD).
- [Initial Design](https://docs.google.com/document/d/1ZgdpBscUf6FoKca9pQKqD_rYdXiLibfzId_-MMShRhw/edit?usp=sharing) and [decisions](https://drive.google.com/drive/folders/1rSHJKEkgYdzEcGHBGkIuOK1fCH0dQ5Cl?usp=share_link)
- [Test coverage](https://docs.google.com/document/d/1SSd0cFggNsWBtrIKm-X0d1yxvv9Z705I0Zmhrh6CJxI/edit?usp=sharing)
- [Future work & Improvement](https://docs.google.com/document/d/1gbyAg_fo3wLi9vyA04fN0BnJyelq-1aVXwh5wa0CTIY/edit?usp=sharing)
- Javadoc generation task has been created in build.gradle. Please do ./gradlew javadoc to see the Javadoc

<br>

# Invitation and Youtube Link
- Join our [server](https://discord.com/api/oauth2/authorize?client_id=1079155253699686490&permissions=0&scope=bot) and play with our bot! 🥳
- Check out the [demo](https://www.youtube.com/watch?v=0iAOVqx_JKg) about our bot!
