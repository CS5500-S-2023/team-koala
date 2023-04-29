package edu.northeastern.cs5500.starterbot.service.packages;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.service.Service;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

@Slf4j
/** This service is in charge of schedule tasks to send package updates to users */
public class PackageSchedulingService implements Service {

    public static final int DEFAULT_STARTING_HOUR = 8; // 24-hour clock
    public static final int DEFAULT_STARTING_MINUTE = 0; // range 0-59
    public static final TimeZone time_zone = TimeZone.getTimeZone("PST");
    public static final long DAY_INTERVAL_MILLISECONDS = TimeUnit.DAYS.toMillis(1);

    @Inject JDA jda;
    @Inject TrackPackageService trackPackageService;

    /** Public constructor for injection */
    @Inject
    public PackageSchedulingService() {}

    /**
     * Read starting hour from environment variables to make it flexible for developers
     *
     * <p>If environmental variable is not a valid integer, the default hour will be used.
     *
     * @return the starting hour for daily task
     */
    static int getStartingHour() {
        String env =
                new ProcessBuilder()
                        .environment()
                        .get("PACKAGE_SCHEDULING_DAILY_TASK_STARTING_HOUR");
        int startingHour = DEFAULT_STARTING_HOUR;
        try {
            startingHour = Integer.parseInt(env);
        } catch (Exception e) {
            log.error(
                    "The PACKAGE_SCHEDULING_DAILY_TASK_STARTING_HOUR token {} is not a valid integer - throwing {}",
                    env,
                    e.getMessage());
        }
        return startingHour;
    }

    /**
     * Read starting minute from environment variables to make it flexible for developers
     *
     * <p>If environmental variable is not a valid integer, the default minute will be used.
     *
     * @return the starting minute for daily task
     */
    static int getStartingMinute() {
        String env =
                new ProcessBuilder()
                        .environment()
                        .get("PACKAGE_SCHEDULING_DAILY_TASK_STARTING_MINUTE");
        int startingMinute = DEFAULT_STARTING_MINUTE;
        try {
            startingMinute = Integer.parseInt(env);
        } catch (Exception e) {
            log.error(
                    "The PACKAGE_SCHEDULING_DAILY_TASK_STARTING_MINUTE token {} is not a valid integer - throwing {}",
                    env,
                    e.getMessage());
        }
        return startingMinute;
    }

    /**
     * Schedule daily tasks to retrieve packages' status and notify users only when there are
     * updates for their packages
     */
    public void scheduleTask() {
        Timer timer = new Timer();

        Calendar currTime = Calendar.getInstance(time_zone);
        Date startTime = getFirstStartTime(currTime);
        timer.schedule(
                new GetPackageStatusTask(jda, trackPackageService),
                startTime,
                DAY_INTERVAL_MILLISECONDS);
    }

    /**
     * Set the task start time at {@value #DEFAULT_STARTING_HOUR} in 24-hour clock;
     *
     * <p>If current time(hh:mm:ss) has past {@value #DEFAULT_STARTING_HOUR}, the start time is set
     * to next day
     *
     * @return Date - first start time for the daily task
     */
    @VisibleForTesting
    Date getFirstStartTime(Calendar currTime) {

        Calendar targetTime = Calendar.getInstance(time_zone);

        targetTime.set(Calendar.HOUR_OF_DAY, getStartingHour());
        targetTime.set(Calendar.MINUTE, getStartingMinute());

        // if current time has past the set start time, execute next day
        if (currTime.after(targetTime)) {
            targetTime.set(Calendar.DAY_OF_YEAR, currTime.get(Calendar.DAY_OF_YEAR) + 1);
        }

        log.info("Daily task startst at: {}", targetTime.getTime());
        return targetTime.getTime();
    }

    @Override
    public void register() {
        log.info("Registering PackageSchedulingService");
    }
}
