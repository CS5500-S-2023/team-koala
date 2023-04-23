package edu.northeastern.cs5500.starterbot.service;

import java.util.Timer;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

@Slf4j
/** PackageSchedulingService is in charge of schedule tasks to send package updates to users */
public class PackageSchedulingService implements Service {

    // Delay in milliseconds that allows the task to be initialized after the bot is fully started
    public static final long START_DELAY = TimeUnit.MINUTES.toMillis(1);

    @Inject JDA jda;
    @Inject TrackPackageService trackPackageService;

    /** Public constructor for injection */
    @Inject
    public PackageSchedulingService() {}

    /**
     * Schedule daily tasks to retrieve packages' status and notify users only when there are
     * updates for their packages
     */
    public void scheduleTask() {
        Timer timer = new Timer();

        long dayIntervalInMilliseconds = TimeUnit.DAYS.toMillis(1);
        timer.schedule(
                new GetPackageStatusTask(jda, trackPackageService),
                START_DELAY,
                dayIntervalInMilliseconds);
    }

    @Override
    public void register() {
        log.info("Registering PackageSchedulingService");
    }
}
