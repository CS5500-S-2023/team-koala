package edu.northeastern.cs5500.starterbot.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

/** The model that contains the info about a reminder. */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReminderEntry implements Model {
    /** Id of the reminder, generated. */
    ObjectId id;
    /** User who created this reminder entry. */
    @Nonnull String discordUserId;
    /** Title of reminder. */
    @Nonnull String title;
    /** The time the reminded event is supposed to start. */
    @Nonnull LocalTime reminderTime;
    /** How much earlier (in minutes) the reminder message should be sent. */
    @Nonnull Integer reminderOffset;
    /** The time when the next message for this reminder will be sent. */
    @Nonnull LocalDateTime nextReminderTime;
    /** Time zone of the reminder. */
    @Nonnull String timeZone;
    /** The interval between 2 reminders if the reminder is repeated. */
    @Nullable Integer repeatInterval;
    /** Time unit of the repeat interval. */
    @Nullable TimeUnit repeatTimeUnit;
}
