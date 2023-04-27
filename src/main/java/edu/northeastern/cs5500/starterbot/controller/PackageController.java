package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.MongoException;
import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.NotYourPackageException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.packages.TrackPackageService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

/** PackageController is a class for operations on the package model */
@Slf4j
public class PackageController {

    GenericRepository<Package> packageRepository;
    TrackPackageService trackPackageService;

    public static final String SUCCESS = "success";
    public static final String PACKAGE_NOT_FOUND_MESSAGE =
            "there is no such package with the provided carrier id and tracking number.";
    public static final String THIRD_PARTY_API_FAILED_MESSAGE =
            "there is something wrong with connecting to the third-party api";
    public static final String UNKNOWN_ERROR = "there is something wrong.";
    public static final String TRY_AGAIN_MESSAGE = "Please try again later.";
    public static final String PACKAGE_ALREADY_EXISTS_MESSAGE =
            "you have created this package in our database.";
    public static final String MONGODB_ADD_PACKAGE_FAILED_MESSAGE =
            "it is unable to add package to MongoDB database";

    /**
     * Public Constructor for injection
     *
     * @param packageRepository - actually provided with MongoDBRepository
     */
    @Inject
    public PackageController(
            GenericRepository<Package> packageRepository, TrackPackageService trackPackageService) {
        this.packageRepository = packageRepository;
        this.trackPackageService = trackPackageService;
    }

    /**
     * Add package to database after validating the existence of the package via third-party api
     *
     * @param package1
     * @return string - SUCESS or Any other error message
     */
    public String createPackage(@Nonnull Package package1) {

        // Check if this package already created by this user
        Package packageExists = getPackageByUserCarrirerAndTrackingNumber(package1);
        if (!Objects.equals(null, packageExists)) {
            return PACKAGE_ALREADY_EXISTS_MESSAGE;
        }

        // Verify if a package exists
        String validationResult = validatePackage(package1);
        if (!validationResult.equals(SUCCESS)) {
            return validationResult;
        }

        // write to database
        try {
            packageRepository.add(package1);
        } catch (MongoException e) {
            log.error("{} because {}", MONGODB_ADD_PACKAGE_FAILED_MESSAGE, e.getMessage());
            throw e;
        }

        return SUCCESS;
    }

    /**
     * Find a package based on userId, carrierId, and trackingNumber
     *
     * @param package1
     * @return null if this package is not created yet in our system; a Package object if this
     *     package has been created.
     */
    private Package getPackageByUserCarrirerAndTrackingNumber(Package package1) {
        log.info("getPackageByUserCarrirerAndTrackingNumber of package - {}", package1.getId());
        String userId = package1.getUserId();
        String carrierId = package1.getCarrierId();
        String trackingNumber = package1.getTrackingNumber();
        Collection<Package> allPackages = packageRepository.getAll();

        for (Package p : allPackages) {
            if (p.getUserId().equals(userId)
                    && p.getCarrierId().equals(carrierId)
                    && p.getTrackingNumber().equals(trackingNumber)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Verify if a package is existent via calling third-party(KeyDelivery) API
     *
     * @param package1
     * @return string - SUCESS or Any other error message
     */
    private String validatePackage(@Nonnull Package package1) {
        try {
            getPackageLatestStatus(package1);
        } catch (PackageNotExistException e) {
            return String.format("%s %s", PACKAGE_NOT_FOUND_MESSAGE, TRY_AGAIN_MESSAGE);
        } catch (KeyDeliveryCallException e) {
            return String.format("%s %s", THIRD_PARTY_API_FAILED_MESSAGE, TRY_AGAIN_MESSAGE);
        } catch (Exception e) {
            return String.format("%s %s", UNKNOWN_ERROR, TRY_AGAIN_MESSAGE);
        }

        return SUCCESS;
    }

    /**
     * The updated status will be written in the passed-in package object.
     *
     * <p>Expected: If the status and statusTime is null, then it means no status is available yet.
     *
     * @param package1
     * @throws PackageNotExistException
     * @throws KeyDeliveryCallException
     */
    @SneakyThrows
    public void getPackageLatestStatus(Package package1)
            throws KeyDeliveryCallException, PackageNotExistException {
        trackPackageService.getPackageLatestStatus(package1);
    }

    /**
     * This method gets the package with a certain id
     *
     * @param id
     * @return Package the package with the associate id
     * @throws IllegalArgumentException if the package id is invalid
     */
    @SneakyThrows
    public Package getPackage(String id) {
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
    @SneakyThrows
    public void deletePackage(String id, String userId)
            throws IllegalArgumentException, NotYourPackageException {
        ObjectId objectId = new ObjectId(id);
        Package p = packageRepository.get(objectId);

        if (p == null) {
            throw new IllegalArgumentException("The packageId is not valid!");
        } else if (!p.getUserId().equals(userId)) {
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
    @SneakyThrows
    public List<Package> getUsersPackages(String userId) {
        Collection<Package> allPackages = packageRepository.getAll();

        List<Package> usersPackages = new ArrayList<>();
        for (Package p : allPackages) {
            if (p.getUserId().equals(userId)) {
                getPackageLatestStatus(p);

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
     * @throws PackageNotExistException if the package is not valid (bad carrier and tracking number
     *     combination)
     */
    @SneakyThrows
    public Package updatePackage(
            String id, String userId, String name, String trackingNumber, String carrierId)
            throws IllegalArgumentException, NotYourPackageException, PackageNotExistException {

        ObjectId objectId = null;
        Package p = null;
        objectId = new ObjectId(id);
        p = packageRepository.get(objectId);

        if (!p.getUserId().equals(userId)) {
            throw new NotYourPackageException("This is not your package");
        }
        p.setName(name);
        p.setTrackingNumber(trackingNumber);
        p.setCarrierId(carrierId);

        getPackageLatestStatus(p);

        packageRepository.update(p);
        return p;
    }
}
