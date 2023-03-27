package edu.northeastern.cs5500.starterbot.model;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull String discordUserId;

    // title of reminder.
    @Nonnull String title;

    // the time the reminded event is supposed to start
    @Nonnull LocalTime reminderTime;

    // how many minutes earlier the reminder should be sent
    @Nonnull Integer reminderOffset;

    // the interval between 2 reminders if the reminder entry is repeated
    @Nullable Integer recurrenceInterval;

    // the unit of the recurrenceInterval;
    @Nullable TimeUnit recurrencTimeUnit;
}
