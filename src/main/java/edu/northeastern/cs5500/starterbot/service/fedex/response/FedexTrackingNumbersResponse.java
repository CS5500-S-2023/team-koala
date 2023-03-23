package edu.northeastern.cs5500.starterbot.service.fedex.response;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class FedexTrackingNumbersResponse {
    String transactionId;
    String customerTransactionId;
    FedexTrackingNumbersResults output;

    public static FedexTrackingNumbersResponse fromJson(String json) {
        return new Gson().fromJson(json, FedexTrackingNumbersResponse.class);
    }
}
