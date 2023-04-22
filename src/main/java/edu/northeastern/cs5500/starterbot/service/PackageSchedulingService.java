package edu.northeastern.cs5500.starterbot.service;

import java.util.Timer;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;

/** PackageSchedulingService is in charge of schedule tasks to send package updates to users */
public class PackageSchedulingService {

    JDA jda;
    TrackPackageService trackPackageService;

    /**
     * Public constructor
     *
     * @param jda - represents a connection to discord
     * @param trackPackageService
     */
    @Inject
    public PackageSchedulingService(JDA jda, TrackPackageService trackPackageService) {
        this.jda = jda;
        this.trackPackageService = trackPackageService;
    }

    /**
     * Schedule daily tasks to retrieve packages' status and notify users only when there are
     * updates for their packages
     */
    public void scheduleTask() {
        Timer timer = new Timer();

        long dayIntervalInMilliseconds = TimeUnit.DAYS.toMillis(1);
        timer.schedule(
                new GetPackageStatusTask(jda, trackPackageService),
                2000,
                dayIntervalInMilliseconds);
    }
}
