package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.exception.NotFoundException;
import edu.northeastern.cs5500.starterbot.model.TrackingInformation;
import java.io.IOException;
import javax.annotation.Nonnull;

public interface ShipmentTrackingService extends Service {
    /**
     * Returns true if this service might be able to track this tracking number, otherwise false.
     *
     * @param trackingNumber a tracking number of unknown type
     * @return true if this service might be able to track this tracking number, otherwise false
     */
    boolean canHandleTrackingNumber(@Nonnull String trackingNumber);

    /**
     * Return tracking information from this service for a given tracking number.
     *
     * @param trackingNumber a tracking number that is expected to be for this service
     * @throws NotFoundException could not find tracking information for the given number
     * @return tracking information for the given tracking number
     */
    @Nonnull
    TrackingInformation getTrackingInformation(@Nonnull String trackingNumber)
            throws NotFoundException, IOException;
}
