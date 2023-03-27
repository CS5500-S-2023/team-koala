package edu.northeastern.cs5500.starterbot.service.fedex;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

@Data
public class FedexAccessToken {
    private final String accessToken;
    private final String tokenType;
    private final Long expiresIn;
    private final String scope;

    public static FedexAccessToken fromJson(String json) {
        Gson gson =
                new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();

        return gson.fromJson(json, FedexAccessToken.class);
    }

    public String toHeader() {
        return String.format("Bearer %s", accessToken);
    }
}
