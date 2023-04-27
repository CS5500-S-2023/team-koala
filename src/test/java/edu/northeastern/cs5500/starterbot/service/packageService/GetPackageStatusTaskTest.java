package edu.northeastern.cs5500.starterbot.service.packageService;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class GetPackageStatusTaskTest {
    GetPackageStatusTask getPackageStatusTask;

    public GetPackageStatusTaskTest() {
        this.getPackageStatusTask = new GetPackageStatusTask(null, null);
    }

    @Test
    public void testCalculateNumEachTaskSmallerPackages() {
        int numPackages = GetPackageStatusTask.TASK_FREQUENCEY - 2;
        assertThat(this.getPackageStatusTask.calculateNumEachTask(numPackages)).isEqualTo(1);
        ;
    }

    @Test
    public void testCalculateNumEachTaskGreaterPackages() {
        int numPackages = GetPackageStatusTask.TASK_FREQUENCEY + 10;
        assertThat(this.getPackageStatusTask.calculateNumEachTask(numPackages)).isEqualTo(2);
        ;
    }
}
