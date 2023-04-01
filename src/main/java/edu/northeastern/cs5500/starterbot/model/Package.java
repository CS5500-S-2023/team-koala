package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor; // only for test purpose
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Package implements Model {

    ObjectId id;
    String name;
    @Nonnull String trackingNumber;
    @Nonnull String carrierId;
    @Nonnull String userId;
}
