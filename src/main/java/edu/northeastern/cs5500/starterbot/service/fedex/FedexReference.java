package edu.northeastern.cs5500.starterbot.service.fedex;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

@Data
public class FedexReference {
    private String carrierCode;
    private String type;
    private String value;
    private String accountNumber;
    private String shipDateBegin;
    private String shipDateEnd;
    private String destinationCountryCode;
    private String destinationPostalCode;

    public static FedexReference fromJson(String json) {
        Gson gson =
                new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();

        return gson.fromJson(json, FedexReference.class);
    }
}
