package edu.northeastern.cs5500.starterbot.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class KeyDeliveryWebhookRequest {
    @SerializedName("carrier_id")
    String carrierId;

    @SerializedName("tracking_number")
    String trackingNumber;
}
