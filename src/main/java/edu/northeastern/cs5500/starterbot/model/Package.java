package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.Nullable;
import java.sql.Timestamp;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor; // only for test purpose
import lombok.Data;
import org.bson.types.ObjectId;

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
    @Nullable Timestamp statusTime;
}
