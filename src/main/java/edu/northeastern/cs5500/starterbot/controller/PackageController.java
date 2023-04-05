package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import javax.inject.Inject;

public class PackageController {

    GenericRepository<Package> packageRepository; // data access object
    TrackPackageService trackPackageService;

    @Inject
    public PackageController(
            GenericRepository<Package> packageRepository, TrackPackageService trackPackageService) {
        this.packageRepository = packageRepository;
        this.trackPackageService = trackPackageService;
    }

    /**
     * The updated status will be written in the passed-in package object If the status and
     * statusTime is null, then it means no status is available yet.
     *
     * @param package1
     */
    public void getPackageLatestStatus(Package package1) {
        trackPackageService.getPackageLatestStatus(package1);
    }
}
