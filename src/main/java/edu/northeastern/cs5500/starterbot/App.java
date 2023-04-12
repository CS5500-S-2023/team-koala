package edu.northeastern.cs5500.starterbot;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import com.google.gson.Gson;
import edu.northeastern.cs5500.starterbot.model.KeyDeliveryWebhookRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main(String[] arg) {

        DaggerBotComponent.create().bot().start();

        port(8080);

        get("/", (request, response) -> "{\"status\": \"OK\"}");

        get("/getDeliveryUpdates", (request, response) -> "{\"status\": \"getDeliveryUpdates\"}");
        // Will need to move the callback function as an individual function
        post(
                "/getDeliveryUpdates",
                (request, response) -> {
                    // recognize which package this is by carrier_id and tracking_number
                    String carrier_id = request.attribute("carrier_id");
                    String tracking_number = request.attribute("tracking_number");

                    Gson gson = new Gson();
                    KeyDeliveryWebhookRequest webhookRequest =
                            gson.fromJson(request.body(), KeyDeliveryWebhookRequest.class);

                    log.info(
                            webhookRequest.getCarrierId()
                                    + " "
                                    + webhookRequest.getTrackingNumber());
                    return "{\"status\": \"OK\"}";

                    // read the first/latest item's context, time, order_status_description

                    // invoke dao - update into database

                    // notify corresponding user - read user_id from the returned update
                });
    }
}
