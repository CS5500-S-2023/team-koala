package edu.northeastern.cs5500.starterbot.service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.SneakyThrows;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PackageSchedulingService {
    GetPackageStatusTask getPackageStatusTask;

    @Inject
    public PackageSchedulingService(GetPackageStatusTask getPackageStatusTask) {
        this.getPackageStatusTask = getPackageStatusTask;
    }

    /**
     * Schedule tasks to notify users when there are updates for the package
     *
     * @param - jda is needed to send messages to discord user
     */
    @SneakyThrows 
    public void scheduleTask() {
        Timer timer = new Timer();

        String startTime = "2023-04-17 12:00"; // By default, it is 24-hour clock
        String pattern = "yyyy-MM-dd hh:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        // The ParseException(handled by annotation) should never be thrown as the input is valid and constant
        Date startDate = formatter.parse(startTime);
        
        //long intervalInMilliseconds = TimeUnit.DAYS.toMillis(1);

        //timer.schedule(task, startDate, intervalInMilliseconds);
        timer.schedule(getPackageStatusTask,2000, 6000L);
    }
}
