

@Singleton
@Slf4j
public class MongoDBService implements Service {

    /**
     * Get updates along the way, not instantly displayed
     * @return
     */
    public boolean createPackageTracking() {
        return true;
    }

    /**
     * Invoked when displaying list of packages
     */
    public void getPackageCurrentStatus() {

    }

    /**
     * Updates about packages will be posted via webhook
     */
    private void handlePackageUpdates() {

    }

    /**
     * When creating the tracking, if the response code is 407,
     * message is "The tracking_number is invalid", return false
     * 
     * @return
     */
    public boolean verifyTrackingNumber() {
        return true;
    }
    
}