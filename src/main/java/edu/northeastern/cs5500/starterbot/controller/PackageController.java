package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.exception.NotYourPackageException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.TrackPackageService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
     * The updated status will be written in the passed-in package object.
     *
     * <p>Expected: If the status and statusTime is null, then it means no status is available yet.
     *
     * @param package1
     */
    public boolean getPackageLatestStatus(Package package1) {
        return trackPackageService.getPackageLatestStatus(package1);
    }

    // add package to database after create tracking item via third-party api
    public String createPackage(Package package1) {

        // create tracking item

        // write to database
        // assumed insertion success, otherwise will need to modify repository functions
        packageRepository.add(package1);

        return SUCCESS;
    }

    /**
     * This method gets the package with a certain id
     *
     * @param id
     * @return Package the package with the associate id
     * @throws IllegalArgumentException if the id is invalid or does not exist
     */
    public Package getPackage(String id) throws IllegalArgumentException {
        ObjectId objectId = null;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return this.packageRepository.get(objectId);
    }

    /**
     * This method deletes the package with the associated id only if it belongs to the user
     *
     * @param id String that represents the package id
     * @param userId String that represents the user's discord id
     * @throws IllegalArgumentException if the package id is invalid
     * @throws NotYourPackageException if the user's id does not match the user id associated to the
     *     package
     */
    public void deletePackage(String id, String userId)
            throws IllegalArgumentException, NotYourPackageException {
        ObjectId objectId = null;
        Package p = null;
        try {
            objectId = new ObjectId(id);
            p = packageRepository.get(objectId);
        } catch (IllegalArgumentException e) {
            throw e;
        }

        if (!p.getUserId().equals(userId)) {
            throw new NotYourPackageException("This is not your package!");
        }
        packageRepository.delete(objectId);
    }

    /**
     * This method gets all the user's packages
     *
     * @param userId String that represents the user's discord id
     * @return List<Package> the packages that the user owns
     */
    public List<Package> getUsersPackages(String userId) {
        Collection<Package> allPackages = packageRepository.getAll();

        List<Package> usersPackages = new ArrayList<>();
        for (Package p : allPackages) {
            if (p.getUserId().equals(userId)) {
                if (!getPackageLatestStatus(p)) {
                    p.setStatus(null);
                    p.setStatusTime(null);
                }
                usersPackages.add(p);
            }
        }

        return usersPackages;
    }

    /**
     * This method updates the name, tracking number, and carrier id of the the package with the
     * associated package id if the user owns the package
     *
     * @param id String representing the package id
     * @param userId String representing the user's discord id
     * @param name String representing the new package name
     * @param trackingNumber String representing the new tracking number
     * @param carrierId String representing the new carrier id
     * @return Package the package that got updated
     * @throws IllegalArgumentException if the package id is invalid
     * @throws NotYourPackageException if the package does not belong to the user
     */
    public Package updatePackage(
            String id, String userId, String name, String trackingNumber, String carrierId)
            throws IllegalArgumentException, NotYourPackageException {

        ObjectId objectId = null;
        Package p = null;
        try {
            objectId = new ObjectId(id);
            p = packageRepository.get(objectId);
        } catch (IllegalArgumentException e) {
            throw e;
        }

        if (!p.getUserId().equals(userId)) {
            throw new NotYourPackageException("This is not your package");
        }
        p.setName(name);
        p.setTrackingNumber(trackingNumber);
        p.setCarrierId(carrierId);
        packageRepository.update(p);
        return p;
    }
}
