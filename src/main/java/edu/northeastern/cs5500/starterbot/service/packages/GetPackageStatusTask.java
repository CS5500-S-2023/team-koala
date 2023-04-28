package edu.northeastern.cs5500.starterbot.service.packages;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.model.Package;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

/**
 * This class is a timer task which schedules various timer tasks to retrieve packages' status at
 * different time
 */
@Slf4j
public class GetPackageStatusTask extends TimerTask {

    public static final int TASK_FREQUENCEY = 12;
    public static final long HOUR_INTERVAL_MILLISECONDS = TimeUnit.HOURS.toMillis(1);

    JDA jda;
    TrackPackageService trackPackageService;

    /**
     * Public constructor for usage in PackageSchedulingService
     *
     * @param jda - represents a connection to discord
     * @param trackPackageService - TrackPackageService
     */
    public GetPackageStatusTask(JDA jda, TrackPackageService trackPackageService) {
        this.jda = jda;
        this.trackPackageService = trackPackageService;
    }

    /** Run method to retrieve all packages and assign to sub-tasks */
    @Override
    public void run() {
        Collection<Package> allPackages = trackPackageService.packageRepository.getAll();
        Package[] packageArray = allPackages.toArray(new Package[0]);

        int numOfPackagesEachTask = calculateNumEachTask(allPackages.size());

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
            timer.schedule(task, i * HOUR_INTERVAL_MILLISECONDS);
        }
    }

    @VisibleForTesting
    int calculateNumEachTask(int size) {
        // Calculate the number of packages each task should iterate over
        return (int) Math.ceil(size * 1.0 / TASK_FREQUENCEY);
    }
}
