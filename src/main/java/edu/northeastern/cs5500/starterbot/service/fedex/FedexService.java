package edu.northeastern.cs5500.starterbot.service.fedex;

import edu.northeastern.cs5500.starterbot.exception.NotFoundException;
import edu.northeastern.cs5500.starterbot.model.TrackingInformation;
import edu.northeastern.cs5500.starterbot.service.ShipmentTrackingService;
import edu.northeastern.cs5500.starterbot.service.fedex.request.FedexTrackingNumbersRequest;
import edu.northeastern.cs5500.starterbot.service.fedex.response.FedexTrackingNumbersResponse;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
@Slf4j
public class FedexService implements ShipmentTrackingService {
    FedexCredentials fedexCredentials;

    FedexAccessToken accessToken = null;

    @Inject
    public FedexService(FedexCredentials fedexCredentials) {
        this.fedexCredentials = fedexCredentials;
        refreshAccessToken();
    }

    synchronized FedexAccessToken refreshAccessToken() {
        if (accessToken == null) {
            synchronized (this) {
                if (accessToken == null) {
                    try {
                        accessToken = getAccessToken();
                    } catch (IOException e) {
                        log.error("Failed to get access token from Fedex: {}", e);
                    }
                }
            }
        }

        return accessToken;
    }

    @Nullable
    FedexAccessToken getAccessToken() throws IOException {
        String clientId = fedexCredentials.getClientId();
        String clientSecret = fedexCredentials.getClientSecret();

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body =
                RequestBody.create(
                        String.format(
                                "grant_type=client_credentials&client_id=%s&client_secret=%s",
                                clientId, clientSecret),
                        mediaType);

        Request request =
                new Request.Builder()
                        .url("https://apis-sandbox.fedex.com/oauth/token")
                        .post(body)
                        .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            log.error("Failed to get access token from Fedex: {}", response);
            return null;
        }

        return FedexAccessToken.fromJson(response.body().string());
    }

    @Override
    public void register() {
        log.info("FedexService > register");
    }

    @Override
    public boolean canHandleTrackingNumber(@Nonnull String trackingNumber) {
        /*
         * 1. /(\b96\d{20}\b)|(\b\d{15}\b)|(\b\d{12}\b)/
         * 2. /\b((98\d\d\d\d\d?\d\d\d\d|98\d\d) ?\d\d\d\d ?\d\d\d\d( ?\d\d\d)?)\b/
         */
        return trackingNumber.matches("^\\d{15}$");
    }

    @Override
    @Nonnull
    public TrackingInformation getTrackingInformation(@Nonnull String trackingNumber)
            throws NotFoundException, IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        FedexTrackingNumbersRequest fedexTrackingNumbersRequest =
                new FedexTrackingNumbersRequest(trackingNumber);
        RequestBody body = RequestBody.create(fedexTrackingNumbersRequest.toJson(), mediaType);

        Request request =
                new Request.Builder()
                        .url("https://apis-sandbox.fedex.com/track/v1/trackingnumbers")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("X-locale", "en_US")
                        .addHeader("Authorization", refreshAccessToken().toHeader())
                        .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        if (!response.isSuccessful()) {
            log.error("Failed to get tracking information from Fedex: {}", response);
            throw new NotFoundException();
        }

        String responseString = response.body().string();
        FedexTrackingNumbersResponse fedexTrackingNumbersResponse =
                FedexTrackingNumbersResponse.fromJson(responseString);

        if (responseString.contains("Tracking number cannot be found.")) {
            log.error("No such tracking number exists from Fedex: {}", response);
            throw new NotFoundException();
        }

        String description =
                fedexTrackingNumbersResponse
                        .getOutput()
                        .getCompleteTrackResults()[0]
                        .getTrackResults()[0]
                        .getLatestStatusDetail()
                        .getDescription();
        System.out.println("Hi");
        return new TrackingInformation(trackingNumber, description);
    }
}
