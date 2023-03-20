package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import javax.inject.Inject;

public class PackageController {

    GenericRepository<Package> packageRepository; // data access object

    @Inject
    public PackageController(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
    }
}
