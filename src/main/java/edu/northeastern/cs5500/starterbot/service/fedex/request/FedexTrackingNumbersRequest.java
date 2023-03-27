package edu.northeastern.cs5500.starterbot.service.fedex.request;

import com.google.gson.Gson;
import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class FedexTrackingNumbersRequest {
    boolean includeDetailedScans = false;
    FedexTrackingInfo[] trackingInfo;

    public String toJson() {
        return (new Gson()).toJson(this);
    }

    public FedexTrackingNumbersRequest(@Nonnull String trackingNumber) {
        this.trackingInfo = new FedexTrackingInfo[] {new FedexTrackingInfo(trackingNumber)};
    }
}
