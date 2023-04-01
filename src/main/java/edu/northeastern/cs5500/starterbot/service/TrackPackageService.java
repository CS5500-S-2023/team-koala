package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.model.Package;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import javax.inject.Inject;

@Singleton
@Slf4j
public class TrackPackageService implements Service {

    private final String URL = "https://www.kd100.com/api/v1/tracking/create";
    private final String API_KEY = "PoceRRIqNjYd1883";
    private final String SECRET = "1db9a0ce49d341f1b932b12e0fccdfd6";
    private final int CONNECT_TIMEOUT = 1000;
    private final int READ_TIMEOUT = 5000;
    public final String SUCCESS = "success";

    @Inject
    public TrackPackageService() {

    }

    /**
     * Get updates along the way Delivery status is not instantly displayed
     *
     * @return if a package is successfully created
     */
    public String createPackageTracking(Package package1) {
        String carrier_id = package1.getCarrierId();
        String tracking_number = package1.getTrackingNumber();
        // use default for now
        String webhook_url = "https://www.kd100.com/webhook_url";

        String result = getData(carrier_id, tracking_number, webhook_url);

        // read the result and handle errors
        return readResponse(result);
    }

    private String getData(String carrier_id, String tracking_number, String webhook_url) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("carrier_id", carrier_id);
        jsonObject.put("tracking_number", tracking_number);
        jsonObject.put("webhook_url", webhook_url);
        String param = jsonObject.toString();

        String signature = MD5Utils.encode(param + API_KEY + SECRET);
        return this.post(param, signature);
    }

    private String post(String param, String signature) {
        StringBuffer response = new StringBuffer("");

        byte[] data = param.getBytes();

        BufferedReader reader = null;
        OutputStream out = null;
        try {

            URL url = new URL(URL);
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

    /** Invoked when displaying list of packages Calling real-time tracking api */
    public void getPackageCurrentStatus() {}

    /** Updates about packages will be posted via webhook */
    private void handlePackageUpdates() {}

    /**
     * When creating the tracking, if the response code is 407, message is "The tracking_number is
     * invalid", return false
     *
     * @return
     */
    public String readResponse(String result) {
        JSONObject json = new JSONObject(result);

        int code = json.getInt("code");
        String message = json.getString("message");

        if (code != 200) return message;

        return SUCCESS;
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
