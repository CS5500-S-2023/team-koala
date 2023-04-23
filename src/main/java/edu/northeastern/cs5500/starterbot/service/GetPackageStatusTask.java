package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

/*
 * GetPackageStatusTask is a timer task
 * which schedules various timer tasks to retrieve packages' status at different time
 */
@Slf4j
public class GetPackageStatusTask extends TimerTask {

    public static final int TASK_FREQUENCEY = 24;

    JDA jda;
    TrackPackageService trackPackageService;

    /**
     * Public constructor for usage in PackageSchedulingService
     *
     * @param jda - represents a connection to discord
     * @param trackPackageService
     */
    public GetPackageStatusTask(JDA jda, TrackPackageService trackPackageService) {
        this.jda = jda;
        this.trackPackageService = trackPackageService;
    }

    /** Run method to retrieve all packages and assign to sub-tasks */
    @Override
    public void run() {
        Collection<Package> allPackages = trackPackageService.packageRepository.getAll();
        // The collection of packages needs to be accessed by index later so it is converted to
        // array first and there will be no write operation on this array
        Package[] packageArray = allPackages.toArray(new Package[0]);

        // Calculate the number of packages each task should iterate over
        long hourIntervalInMilliseconds = TimeUnit.HOURS.toMillis(1);
        int numOfPackagesEachTask = (int) Math.ceil(allPackages.size() * 1.0 / TASK_FREQUENCEY);

        Date currTime = new Date();
        log.info("Starting daily package status retrieval task at {}", currTime);

        // Start distribute work to sub-tasks
        Timer timer = new Timer();
        for (int i = 0; i < TASK_FREQUENCEY; i++) {
            int endIdx = (i + 1) * numOfPackagesEachTask;
            if (endIdx > allPackages.size()) {
                endIdx = allPackages.size();
            }

            GetPackageStatusSubTask task =
                    new GetPackageStatusSubTask(
                            jda,
                            trackPackageService,
                            packageArray,
                            i * numOfPackagesEachTask,
                            endIdx,
                            i);

            // Fixed delay time for each task to execute at different time
            timer.schedule(task, i * hourIntervalInMilliseconds);
        }
    }
}
