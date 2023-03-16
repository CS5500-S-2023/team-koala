package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PackageController {

    GenericRepository<Package> packageRepository; // data access object

    @Inject
    public PackageController(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
    }

    
}