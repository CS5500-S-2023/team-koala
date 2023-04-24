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

/**
 * The model that contains the info about a reminder:
 *
 * <p>discordUserId - user who created this reminder entry. <br>
 * title - title of reminder. <br>
 * reminderTime - the time the reminded event is supposed to start. <br>
 * nextReminderTime - the time when the next message for this reminder will be sent. <br>
 * reminderOffset - how much earlier (in minutes) the reminder should be sent. <br>
 * repeatInterval - the interval between 2 reminders if the reminder entry is repeated. <br>
 * repeatTimeUnit - time unit of the repeat interval. <br>
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReminderEntry implements Model {
    ObjectId id;
    @Nonnull String discordUserId;
    @Nonnull String title;
    @Nonnull LocalTime reminderTime;
    @Nonnull Integer reminderOffset;
    @Nonnull LocalDateTime nextReminderTime;
    @Nonnull String timeZone;
    @Nullable Integer repeatInterval;
    @Nullable TimeUnit repeatTimeUnit;
}
