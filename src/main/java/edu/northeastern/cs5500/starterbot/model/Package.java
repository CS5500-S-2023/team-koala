package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Package implements Model {

    ObjectId id;
    String name;
    @Nonnull String trackingNumber;
    @Nonnull String carrierId;

    public Package() {
        this.name = null;
        this.trackingNumber = null;
        this.carrierId = null;
    }

    public Package(
            @Nonnull String name, @Nonnull String trackingNumber, @Nonnull String carrierId) {
        this.name = name;
        this.trackingNumber = trackingNumber;
        this.carrierId = carrierId;
    }

    /**
     * This constructor is for users who don't like to create a name
     *
     * @param carrierId - passed from select list in frontend
     * @param trackingNumber
     */
    public Package(@Nonnull String trackingNumber, @Nonnull String carrierId) {
        this.name = null;
        this.trackingNumber = trackingNumber;
        this.carrierId = carrierId;
    }

    public void reset() {
        this.name = null;
        this.trackingNumber = null;
        this.carrierId = null;
    }
}
