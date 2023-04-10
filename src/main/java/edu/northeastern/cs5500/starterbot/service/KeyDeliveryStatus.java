package edu.northeastern.cs5500.starterbot.service;

import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class KeyDeliveryStatus {
    @SerializedName("context")
    String status;

    @SerializedName("time")
    Timestamp statusTime;
}
