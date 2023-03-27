package edu.northeastern.cs5500.starterbot.service.fedex.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class FedexTrackingNumberInfo {
    @Nonnull String trackingNumber;
    @Nullable String carrierCode;
    @Nullable String trackingNumberUniqueId;

    public FedexTrackingNumberInfo(@Nonnull String trackingNumber) {
        this.trackingNumber = trackingNumber;
        this.carrierCode = null;
        this.trackingNumberUniqueId = null;
    }
}
