package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Package implements Model {

    ObjectId id;
    String name;
    @Nonnull String trackingNumber;
    @Nonnull String carrierId;

    public void reset() {
        this.name = null;
        this.trackingNumber = null;
        this.carrierId = null;
    }
}
