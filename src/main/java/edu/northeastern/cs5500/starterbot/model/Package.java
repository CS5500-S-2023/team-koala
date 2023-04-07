package edu.northeastern.cs5500.starterbot.model;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor; // only for test purpose
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;

import com.mongodb.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Package implements Model {

    ObjectId id;
    @Nullable String name;
    @Nonnull String trackingNumber;
    @Nonnull String carrierId;
    @Nonnull String userId;
    @Nullable String status;
    @Nullable LocalDateTime statusTime;
    
}
