package edu.northeastern.cs5500.starterbot.service.packages;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.northeastern.cs5500.starterbot.exception.keydelivery.KeyDeliveryCallException;
import edu.northeastern.cs5500.starterbot.exception.packages.PackageNotExistException;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * This service handles interactions with third-party API KeyDelivery
 *
 * @see <a href="https://www.kd100.com/docs/keydelivery-api">KeyDelivery Dcoumentation</a>
 */
@Singleton
@Slf4j
public class TrackPackageService implements Service {

    private static final String REALTIME_URL = "https://www.kd100.com/api/v1/tracking/realtime";
    private static final int CONNECT_TIMEOUT = 1000;
    private static final int READ_TIMEOUT = 5000;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Secrets
    private static final String API_KEY =
            new ProcessBuilder().environment().get("KEY_DELIVERY_API_KEY");
    private static final String SECRET =
            new ProcessBuilder().environment().get("KEY_DELIVERY_API_SECRET");

    // Error Codes from API response
    private static final int OK = 200;
    private static final int PACKAGE_NOT_EXIST = 60101;

    public static final Map<String, String> carrierMap =
            Map.of(
                    "UPS", "ups",
                    "DHL", "dhl",
                    "FedEx", "fedex",
                    "USPS", "usps",
                    "LaserShip", "lasership",
                    "China-post", "cpcbe",
                    "China Ems International", "china_ems_international",
                    "GLS", "gls",
                    "Canada Post", "canada_post",
                    "Purolator", "purolator");

    GenericRepository<Package> packageRepository;

    @Inject
    public TrackPackageService(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
    }

    /**
     * Call real-time tracking api to get the latest status Invoked when displaying list of packages
     *
     * @throws KeyDeliveryCallException
     * @throws PackageNotExistException
     */
    public void getPackageLatestStatus(Package package1)
            throws KeyDeliveryCallException, PackageNotExistException {
        String carrier_id = package1.getCarrierId();
        String tracking_number = package1.getTrackingNumber();

        String result = getData(REALTIME_URL, carrier_id, tracking_number);
        log.info(String.format("getPackageLatestStatus for package %s", package1.getId()));

        // read the delivery updates
        readDeliveryResponse(result, package1);
    }

    /**
     * Parse the response from KeyDelivery to get their delivery latest status, time and description
     *
     * @param result - the response from KeyDelivery
     * @param package1 - the provided package object
     * @throws PackageNotExistException
     * @throws KeyDeliveryCallException
     */
    @VisibleForTesting
    void readDeliveryResponse(String result, Package package1)
            throws PackageNotExistException, KeyDeliveryCallException {
        log.info("readDeliveryResponse: got the delivery status of {}", package1.getId());

        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
        JsonObject response = new JsonObject();
        try {
            response = gson.fromJson(result, JsonElement.class).getAsJsonObject();
        } catch (NullPointerException e) {
            log.error(String.format("readDeliveryResponse : %s and result is %s", e, result));
            throw e;
        }

        int code = response.get("code").getAsInt();
        String message = response.get("message").getAsString();
        if (code != OK) {
            if (code == PACKAGE_NOT_EXIST) {
                log.error("readDeliveryResponse - error - {} : {}", PACKAGE_NOT_EXIST, message);
                throw new PackageNotExistException(
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
     * Construct JSON strings and send post requests to a KeyDelivery API Realtime tracking
     *
     * @param carrier_id - carrier_id from user input
     * @param tracking_number - tracking_number from user input
     * @return response from KeyDelivery
     */
    private String getData(String url, String carrier_id, String tracking_number) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("carrier_id", carrier_id);
        jsonObject.addProperty("tracking_number", tracking_number);
        String param = jsonObject.toString();
        log.info("getData: json is constructed - " + param);

        String signature = MD5Utils.encode(param + API_KEY + SECRET);
        return this.post(param, signature, url);
    }

    /**
     * @see <a href="https://www.kd100.com/docs/create-tracking">KeyDelivery CreateTracking
     *     Dcoumentation</a>
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
 * @see <a href="https://www.kd100.com/docs/create-tracking">KeyDelivery CreateTracking
 *     Dcoumentation</a>
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
