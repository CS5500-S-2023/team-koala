package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.Nullable;
import java.util.Date;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Package implements Model {

    ObjectId id;
    @Nullable String name;
    @Nonnull String trackingNumber;
    @Nonnull String carrierId;
    @Nonnull String userId;
    @Nullable String status;
    @Nullable Date statusTime;
}
