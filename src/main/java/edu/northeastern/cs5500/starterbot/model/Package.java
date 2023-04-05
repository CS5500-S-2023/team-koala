package edu.northeastern.cs5500.starterbot.model;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor; // only for test purpose
import lombok.Data;
import org.bson.types.ObjectId;

import com.mongodb.lang.Nullable;

@Data
@AllArgsConstructor
public class Package implements Model {

    ObjectId id;
    @Nullable final String name;
    @Nonnull String trackingNumber;
    @Nonnull String carrierId;
    @Nullable String status;
    @Nullable LocalDateTime statusTime;
    
}
