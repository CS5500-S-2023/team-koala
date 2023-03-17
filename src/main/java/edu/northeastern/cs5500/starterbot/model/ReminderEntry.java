package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@Data
@Builder
@EqualsAndHashCode
public class ReminderEntry implements Model {
    ObjectId id;

    // user who created this reminder entry
    @NonNull String discordUserId;

    // title of reminder.
    @NonNull String title;

    // the time the reminded event is supposed to start
    @NonNull LocalTime reminderTime;

    // how many minutes earlier the reminder should be sent
    @NonNull Integer reminderOffset;

    // the interval between 2 reminders if the reminder entry is repeated
    @Nullable Integer recurrenceInterval;

    // the unit of the recurrenceInterval;
    @Nullable TimeUnit recurrencTimeUnit;
}
