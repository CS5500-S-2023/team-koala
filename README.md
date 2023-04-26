# Functionality

## Package Status Update
### Adding a Package
- Use `/add-package` command and type in a tracking number and optionally a name for your package.
    - Parameters:

        tracking_number (required): the tracking number of your package.

        name (optional): an alias for your package.
- You will then be prompted to select the carrier of your package.
    - Supported carriers:

        UPS, DHL, FedEx, USPS, LaserShip, China-post, China Ems International, GLS, Canada Post, Purolator

And then we will starting tracking it for you and send daily update messages.

### Displaying your Packages
- Use `/display-packages` command to see all your packages.

### Deleting a Package
- Use `/delete-package` command and type in your package ID to delete a specific package of yours.

---
## Getting Reminder Messages
### Adding a reminder
- Use `/add-reminder` command to add a reminder.
    - Parameters:

        title (required): the title of the event you want to be reminded of.

        reminder-time (required): when the reminded event starts (hh:mm 24 hour clock format, 2 digit for hour and minute each; e.g. 14:00).

        reminder-offset (optional): how much earlier do you want us to remind you (in minutes, default: 0).

        time-zone (optional): time zone of the reminder, defaults to GMT-7 (PDT).

        delay (optional): how many days later do you expect the first reminder (default: 0).

        interval-unit (optional): time unit of the repeat interval (no value by default = reminder does not repeat).

        repeat-interval (optional): interval between 2 reminder messages (no value by default = reminder does not repeat).

### Displaying your Reminders
- Coming soon... Developers please go to MongoDB to see the reminders.

### Deleting a Reminder
- Coming soon... Developers please go to MongoDB to delete reminders.


<br>


# Development
## Deploying this Project to a Bot Application on Discord
- Please refer to this [link](https://www.xda-developers.com/how-to-create-discord-bot/) to create a bot application and generate bot tokens.
- In the terminal, at the root path of the cloned project in your local environment / online codespace, do export BOT_TOKEN=[your generated bot token].
- In the terminal, type in ./gradlew run, and the code will be running on your bot application.

## Connecting to MongoDB
- Please refer to this [document](https://docs.google.com/document/d/1VnlAC4TKOfoEuJhqoeGt6jn3dgVf3ulPvvVPQkEQnFE/) to set up a connection string in MongoDB Atlas.
- In the terminal, at the root path of the cloned project in your local environment / online codespace, do export BOT_TOKEN=[your MongoDB connection string].

<br>

# Documentation
- All documents related to this project can be found [here](https://drive.google.com/drive/folders/1KwwUDZ7SErRCVsoH6g6h0l3_oXPF8htD).

<br>

# Invitation Link
