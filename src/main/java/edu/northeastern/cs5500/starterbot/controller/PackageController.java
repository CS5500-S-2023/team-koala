package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import javax.inject.Inject;

public class PackageController {

    GenericRepository<Package> packageRepository; // data access object
    public final String SUCCESS_STATUS = "success";

    @Inject
    public PackageController(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
    }

    // add package to database after create tracking item via third-party api
    public String createPackage(Package package1) {

        // create tracking item
        

        // write to database 
        // assumed success, otherwise will need to modify repository functions
        packageRepository.add(package1);

        return SUCCESS_STATUS;
    }
}
