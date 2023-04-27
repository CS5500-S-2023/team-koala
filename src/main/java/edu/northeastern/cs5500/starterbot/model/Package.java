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

/** A model that contains information about a package/shipment */
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

    /** Discord user id */
    @Nonnull String userId;

    /**
     * Package's latest status;
     *
     * <p>Default as null; Updated in creating/updating/displaying packages
     */
    @Nullable String status;

    /** Time for package's latest status happen */
    @Nullable Date statusTime;
}
