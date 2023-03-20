package edu.northeastern.cs5500.starterbot.service;
import java.io.IOException;
import okhttp3.*;

public class FedexApi {
    //private String accessToken;

    public String getAccessToken(FedexToken token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String input = "{" +
            "\"grant_type\":" + token.getGrantType() +
            ",\"client_id\":" + token.getClientID() +
            ",\"client_secret\": ," + token.getClientSecret() +
            "}";
        MediaType mediaType = MediaType.parse("application/json");
        // 'input' refers to JSON Payload
        RequestBody body = RequestBody.create(mediaType, input);
        Request request = new Request.Builder()
            .url("https://apis-sandbox.fedex.com/oauth/token")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
                    
        Response response = client.newCall(request).execute();

        return "";
    }

    public String getFakeToken() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String input =  "{" +
        "\"grant_type\":" + "\"client_credentials\"" +
        ",\"client_id\":" + "\"l761ceae70781f4085b811a852d96251a1\"" +
        ",\"client_secret\": \"c281c72592bf42d1b1f389e8e2e93269\"" +
        "}";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // 'input' refers to JSON Payload
        RequestBody body = RequestBody.create(input, mediaType);
        System.out.println(input);

        Request request = new Request.Builder()
            .url("https://apis-sandbox.fedex.com/oauth/token")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
                    
        Response response = client.newCall(request).execute();
        System.out.println(response.toString());
        return response.toString();
    }

    // public void trackByReference(FedexReference input) {
    //     OkHttpClient client = new OkHttpClient();

    //     MediaType mediaType = MediaType.parse("application/json");
    //     // 'input' refers to JSON Payload
    //     RequestBody body = RequestBody.create(mediaType, input);
    //     Request request = new Request.Builder()
    //         .url("https://apis-sandbox.fedex.com/track/v1/referencenumbers")
    //         .post(body)
    //         .addHeader("Content-Type", "application/json")
    //         .addHeader("X-locale", "en_US")
    //         .addHeader("Authorization", "Bearer ")
    //         .build();
                    
    //     Response response = client.newCall(request).execute();
    // }

    // public void trackByTrackingNumber(int input) {
    //     OkHttpClient client = new OkHttpClient();

    //     MediaType mediaType = MediaType.parse("application/json");
    //     // 'input' refers to JSON Payload
    //     RequestBody body = RequestBody.create(mediaType, input);
    //     Request request = new Request.Builder()
    //         .url("https://apis-sandbox.fedex.com/track/v1/trackingnumbers")
    //         .post(body)
    //         .addHeader("Content-Type", "application/json")
    //         .addHeader("X-locale", "en_US")
    //         .addHeader("Authorization", "Bearer")
    //         .build();
                    
    //     Response response = client.newCall(request).execute();
    // }
}