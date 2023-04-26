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
- Use /add-reminder command to add a reminder.
    - Parameters:

        title (required): the title of the event you want to be reminded of.
   
        reminder-time (required): when the reminded event starts (hh:mm 24 hour clock format, 2 digit for hour and minute each; e.g. 14:00)

        reminder-offset (optional): how much earlier do you want us to remind you (in minutes, default: 0)

        time-zone (optional): time zone of the reminder, defaults to GMT-7 (PDT)

        delay (optional): how many days later do you expect the first reminder (default: 0)

        interval-unit (optional): time unit of the repeat interval (no value by default = reminder does not repeat)

        repeat-interval (optional): interval between 2 reminder messages (no value by default = reminder does not repeat)

