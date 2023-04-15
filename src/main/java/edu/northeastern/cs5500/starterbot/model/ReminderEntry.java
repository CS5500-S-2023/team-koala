package edu.northeastern.cs5500.starterbot.model;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/**
 * The model that contains the info about a reminder: discordUserId - user who created this reminder
 * entry; title - title of reminder. reminderTime - the time the reminded event is supposed to
 * start; reminderOffset - how much earlier (in minutes) the reminder should be sent; repeatInterval
 * - the interval between 2 reminders if the reminder entry is repeated; repeatTimeUnit - time unit
 * of the repeat interval.
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ReminderEntry implements Model {
    ObjectId id;
    @Nonnull String discordUserId;
    @Nonnull String title;
    @Nonnull LocalTime reminderTime;
    @Nonnull Integer reminderOffset;
    @Nullable Integer repeatInterval;
    @Nullable TimeUnit repeatTimeUnit;
}
