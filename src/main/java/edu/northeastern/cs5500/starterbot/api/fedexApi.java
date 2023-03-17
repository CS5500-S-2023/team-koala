package edu.northeastern.cs5500.starterbot.api;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.io.IOException;

public class fedexApi {
    public void authentication() {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        // 'input' refers to JSON Payload
        RequestBody body = RequestBody.create(mediaType, input);
        Request request = new Request.Builder()
            .url("https://apis-sandbox.fedex.com/oauth/token")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
                    
        Response response = client.newCall(request).execute();
    }

    public void track() {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        // 'input' refers to JSON Payload
        RequestBody body = RequestBody.create(mediaType, input);
        Request request = new Request.Builder()
            .url("https://apis-sandbox.fedex.com/track/v1/referencenumbers")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-locale", "en_US")
            .addHeader("Authorization", "Bearer ")
            .build();
                    
        Response response = client.newCall(request).execute();
    }

    public void notification() {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        // 'input' refers to JSON Payload
        RequestBody body = RequestBody.create(mediaType, input);
        Request request = new Request.Builder()
            .url("https://apis-sandbox.fedex.com/track/v1/notifications")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-locale", "en_US")
            .addHeader("Authorization", "Bearer ")
            .build();
                    
        Response response = client.newCall(request).execute();
    }

}