package edu.northeastern.cs5500.starterbot.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Package implements Model {
    private final String UNDEFINED_NAME = "undefined";

    ObjectId id;
    String name;
    String trackingNumber;
    String carrierId;

    public Package() {
        this.name = UNDEFINED_NAME;
        this.trackingNumber = null;
        this.carrierId = null;
    }

    public Package(String name, String trackingNumber, String carrierId) {
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
    public Package(String trackingNumber, String carrierId) {
        this.name = UNDEFINED_NAME;
        this.trackingNumber = trackingNumber;
        this.carrierId = carrierId;
    }

    public void reset() {
        this.name = UNDEFINED_NAME;
        this.trackingNumber = null;
        this.carrierId = null;
    }
}
