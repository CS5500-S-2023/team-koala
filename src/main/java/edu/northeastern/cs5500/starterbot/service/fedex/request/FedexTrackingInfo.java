package edu.northeastern.cs5500.starterbot.service.fedex.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class FedexTrackingInfo {
    @Nullable String shipDateBegin;
    @Nullable String shipDateEnd;
    @Nonnull FedexTrackingNumberInfo trackingNumberInfo;

    public FedexTrackingInfo(@Nonnull String trackingNumber) {
        shipDateBegin = null;
        shipDateEnd = null;
        trackingNumberInfo = new FedexTrackingNumberInfo(trackingNumber);
    }
}
