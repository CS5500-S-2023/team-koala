package edu.northeastern.cs5500.starterbot.service.fedex.response;

import lombok.Data;

@Data
public class FedexTrackingNumberResult {
    String trackingNumber;
    FedexTrackResult[] trackResults;
}
