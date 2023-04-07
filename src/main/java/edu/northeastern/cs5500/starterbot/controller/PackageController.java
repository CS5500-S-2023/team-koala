package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import javax.inject.Inject;
import org.bson.types.ObjectId;

public class PackageController {

    GenericRepository<Package> packageRepository; // data access object
    public static final String SUCCESS = "success";
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

    // add package to database after create tracking item via third-party api
    public String createPackage(Package package1) {

        // create tracking item

        // write to database
        // assumed insertion success, otherwise will need to modify repository functions
        packageRepository.add(package1);

        return SUCCESS;
    }

    public Package getPackage(ObjectId id) {
        return this.packageRepository.get(id);
    }
}
