# __Koala Reminder Bot__

# Functionality

## Package Status Update
We sends you daily updates of packages you ask us to track.
### Adding a Package
- Use `/add-package` command and type in a tracking number and optionally a name for your package.
    - Parameters:

        tracking_number (required): the tracking number of your package.

        name (optional): an alias for your package, strongly suggested for identifying your packages. <br>
        &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &nbsp;When our bot sends you updates about your packages, you can recognize your packages by name, otherwise by tracking number.

- You will then be prompted to select the carrier of your package (input required).
    - Supported carriers:

        UPS, DHL, FedEx, USPS, LaserShip, China-post, China Ems International, GLS, Canada Post, Purolator

        > Note:
        The above 10 carriers are what we think are popular based on personal experiences and online searches.
        For the current UI design, we can support at most 25 carriers. Feel free to suggest any carriers you want to include and we will respond to you if that carrier is supported.

If your tracking number or carrier id is valid, you will receive a confirmation message indicating success. <br>
Otherwise, you will receive a message indicating the failure of adding the package.

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

        > Note:
        interval-unit and repeat interval should both be specified to repeat your reminder, if only one of them is specified you
        will receive an error message.

    After successfully adding a reminder, we will send back the reminder info you entered as a receipte and you can expect reminder message at the rate defined by specified interval-unit and repeat-interval.

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
- In the terminal, at the root path of the cloned project in your local environment / online codespace, do export MONGODB_URI=[your MongoDB connection string].

<br>

# Documentation
- All documents related to this project can be found [here](https://drive.google.com/drive/folders/1KwwUDZ7SErRCVsoH6g6h0l3_oXPF8htD).

<br>

# Invitation Link
- Join our [server](https://discord.com/api/oauth2/authorize?client_id=1079155253699686490&permissions=0&scope=bot) and play with our bot! 🥳
