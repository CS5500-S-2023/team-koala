package edu.northeastern.cs5500.starterbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
public class Package implements Model {

    ObjectId id;
    String name;
    String trackingNumber;
    String carrierId;
    String status;
    LocalDateTime statusTime;
    
}
