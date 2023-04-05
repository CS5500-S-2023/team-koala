package edu.northeastern.cs5500.starterbot.service;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

@Singleton
@Slf4j
public class TrackPackageService implements Service {

    private final String REALTIME_URL = "https://www.kd100.com/api/v1/tracking/realtime";
    // will change to environmental variable later like BOT_TOKEN
    private final String API_KEY = new ProcessBuilder().environment().get("API_KEY");
    private final String SECRET = new ProcessBuilder().environment().get("SECRET");
    private final int CONNECT_TIMEOUT = 1000;
    private final int READ_TIMEOUT = 5000;
    public final String SUCCESS = "success";

    GenericRepository<Package> packageRepository; // data access object

    @Inject
    public TrackPackageService(GenericRepository<Package> packageRepository) {
        this.packageRepository = packageRepository;
    }

    /**
     * Call real-time tracking api to get the latest status Invoked when displaying list of packages
     * Update package info in database
     *
     * @return package1 - If there no delivery updates, status and statusTime in Package will be
     *     null - Otherwise, they are not null
     */
    public void getPackageLatestStatus(Package package1) {
        String carrier_id = package1.getCarrierId();
        String tracking_number = package1.getTrackingNumber();

        String result = getData(REALTIME_URL, carrier_id, tracking_number, null);

        // read the delivery updates
        readDeliveryResponse(result, package1);

        // Update package info in database
        packageRepository.add(package1);
    }

    /**
     * Parse the response from KeyDelivery to get their delivery latest status, time and description
     * Then, update package info in database
     *
     * @param result
     * @return
     */
    private void readDeliveryResponse(String result, Package package1) {
        JSONObject json = new JSONObject(result);
        JSONObject data = json.getJSONObject("data");
        String carrier = data.getString("carrier_id");
        String trackingNumber = data.getString("tracking_number");

        JSONArray deliveryStatuses = data.getJSONArray("items");
        JSONObject latestStatus =
                deliveryStatuses.length() == 0 ? null : (JSONObject) deliveryStatuses.get(0);

        String status = latestStatus.getString("context");

        String statusTime = latestStatus.getString("time");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.parse(statusTime, formatter);

        package1.setStatus(status);
        package1.setStatusTime(time);
    }

    /**
     * Construct JSON strings and send post requests to a KeyDelivery API
     *
     * @param carrier_id
     * @param tracking_number
     * @param webhook_url
     * @return response from KeyDelivery
     */
    private String getData(
            String url, String carrier_id, String tracking_number, String webhook_url) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("carrier_id", carrier_id);
        jsonObject.put("tracking_number", tracking_number);
        jsonObject.put("webhook_url", webhook_url);
        String param = jsonObject.toString();

        String signature = MD5Utils.encode(param + API_KEY + SECRET);
        return this.post(param, signature, url);
    }

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
