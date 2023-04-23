package edu.northeastern.cs5500.starterbot.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.northeastern.cs5500.starterbot.exception.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.PackageNotExsitException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TrackPackageService implements Service {

    private static final String REALTIME_URL = "https://www.kd100.com/api/v1/tracking/realtime";
    private static final String API_KEY =
            new ProcessBuilder().environment().get("KEY_DELIVERY_API_KEY");
    private static final String SECRET =
            new ProcessBuilder().environment().get("KEY_DELIVERY_API_SECRET");
    private static final int CONNECT_TIMEOUT = 1000;
    private static final int READ_TIMEOUT = 5000;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final int OK = 200;
    private static final int PACKAGE_NOT_EXIST = 60101;

    GenericRepository<Package> packageRepository;

    @Inject
    public TrackPackageService(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
    }

    /**
     * Call real-time tracking api to get the latest status Invoked when displaying list of packages
     *
     * @throws KeyDeliveryCallException
     * @throws PackageNotExsitException
     */
    public void getPackageLatestStatus(Package package1)
            throws KeyDeliveryCallException, PackageNotExsitException {
        String carrier_id = package1.getCarrierId();
        String tracking_number = package1.getTrackingNumber();

        String result = getData(REALTIME_URL, carrier_id, tracking_number, null);
        log.info("getPackageLatestStatus: " + package1.getId() + " - " + result);

        // read the delivery updates
        readDeliveryResponse(result, package1);
    }

    /**
     * Parse the response from KeyDelivery to get their delivery latest status, time and description
     *
     * @param result - the response from KeyDelivery
     * @param package1 - the provided package object
     * @throws PackageNotExsitException
     * @throws KeyDeliveryCallException
     */
    private void readDeliveryResponse(String result, Package package1)
            throws PackageNotExsitException, KeyDeliveryCallException {
        log.info("readDeliveryResponse: got the delivery status of {}", package1.getId());

        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
        JsonObject response = gson.fromJson(result, JsonElement.class).getAsJsonObject();

        int code = response.get("code").getAsInt();
        String message = response.get("message").getAsString();
        if (code != OK) {
            if (code == PACKAGE_NOT_EXIST) {
                log.error("readDeliveryResponse - error - {} : {}", PACKAGE_NOT_EXIST, message);
                throw new PackageNotExsitException(
                        String.format("Code: %s, Message: %s ", code, message));
            } else {
                log.error("readDeliveryResponse - error - {} : {}", code, message);
                throw new KeyDeliveryCallException(
                        String.format("Code: %s, Message: %s ", code, message));
            }
        }

        JsonArray deliveryStatuses;
        try {
            deliveryStatuses = response.getAsJsonObject("data").getAsJsonArray("items");
        } catch (Exception e) {
            // This should not be reached after the above if else checks
            log.error("readDeliveryResponse: UNKNOWN error {}", result);
            throw e;
        }

        if (deliveryStatuses.size() != 0) {
            JsonObject latestStatus = (JsonObject) deliveryStatuses.get(0);

            KeyDeliveryStatus deliveryStatus = gson.fromJson(latestStatus, KeyDeliveryStatus.class);
            package1.setStatus(deliveryStatus.getStatus());
            package1.setStatusTime(deliveryStatus.getStatusTime());
        }
    }

    /**
     * Construct JSON strings and send post requests to a KeyDelivery API Create tracking API and
     * Realtime tracking API both use this function
     *
     * @param carrier_id
     * @param tracking_number
     * @param webhook_url
     * @return response from KeyDelivery
     */
    private String getData(
            String url, String carrier_id, String tracking_number, String webhook_url) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("carrier_id", carrier_id);
        jsonObject.addProperty("tracking_number", tracking_number);
        if (webhook_url != null) {
            jsonObject.addProperty("webhook_url", webhook_url);
        }
        String param = jsonObject.toString();
        log.info("getData: json is constructed - " + param);

        String signature = MD5Utils.encode(param + API_KEY + SECRET);
        return this.post(param, signature, url);
    }

    /**
     * @see https://www.kd100.com/docs/create-tracking
     */
    private String post(String param, String signature, String urlTarget) {
        StringBuffer response = new StringBuffer("");

        byte[] data = param.getBytes();

        BufferedReader reader = null;
        OutputStream out = null;
        try {

            URL url = new URL(urlTarget);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setRequestProperty("API-Key", API_KEY);
            conn.setRequestProperty("signature", signature);
            conn.setDoOutput(true);

            out = conn.getOutputStream();
            out.write(data);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                    out.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response.toString();
    }

    @Override
    public void register() {
        log.info("TrackPackageService > register");
    }
}

/**
 * @see https://www.kd100.com/docs/create-tracking
 */
class MD5Utils {
    private static MessageDigest mdigest = null;
    private static char digits[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static MessageDigest getMdInst() {
        if (null == mdigest) {
            try {
                mdigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return mdigest;
    }

    public static String encode(String s) {
        if (null == s) {
            return "";
        }

        try {
            byte[] bytes = s.getBytes();
            getMdInst().update(bytes);
            byte[] md = getMdInst().digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = digits[byte0 >>> 4 & 0xf];
                str[k++] = digits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
