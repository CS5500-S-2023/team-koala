package edu.northeastern.cs5500.starterbot.service.packages;

import static com.google.common.truth.Truth.assertThat;

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class PackageSchedulingServiceTest {
    PackageSchedulingService packageSchedulingService;

    public PackageSchedulingServiceTest() {
        this.packageSchedulingService = new PackageSchedulingService();
    }

    @Test
    public void testGetFirstStartTimeSameDay() {
        Calendar currTime = Calendar.getInstance(PackageSchedulingService.time_zone);
        currTime.set(Calendar.HOUR_OF_DAY, 7);
        currTime.set(Calendar.MINUTE, 0);
        currTime.set(Calendar.SECOND, 0);
        int currDay = currTime.get(Calendar.DAY_OF_YEAR);

        // check the expected day and hour
        Date returnedDate = this.packageSchedulingService.getFirstStartTime(currTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(returnedDate);
        assertThat(calendar.get(Calendar.DAY_OF_YEAR)).isEqualTo(currDay);
    }

    @Test
    public void testGetFirstStartTimeSecondDay() {
        Calendar currTime = Calendar.getInstance(PackageSchedulingService.time_zone);
        currTime.set(Calendar.HOUR_OF_DAY, 11);
        currTime.set(Calendar.MINUTE, 0);
        currTime.set(Calendar.SECOND, 0);
        int currDay = currTime.get(Calendar.DAY_OF_YEAR);

        // check the expected day and hour
        Date returnedDate = this.packageSchedulingService.getFirstStartTime(currTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(returnedDate);
        assertThat(calendar.get(Calendar.DAY_OF_YEAR)).isGreaterThan(currDay);
    }
}
