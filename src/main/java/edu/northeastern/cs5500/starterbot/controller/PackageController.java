package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;

import javax.inject.Inject;

public class PackageController {

    GenericRepository<Package> packageRepository; // data access object
    TrackPackageService trackPackageService;

    @Inject
    public PackageController(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
        this.trackPackageService = TrackPackageService.getTrackPackageService();
    }

    public String createPackage() {
        return trackPackageService.createPackageTracking(null);
    }
}
