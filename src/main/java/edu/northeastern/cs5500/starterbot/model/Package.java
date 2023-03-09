import lombok.Data;

@Data
public class Package {
    private final String UNDEFINED_NAME = "undefined";

    String name;
    String trackingNumber;
    
    public Package(String name, String trackingNumber) {
        this.name = name;
        this.trackingNumber = trackingNumber;
    }

    /**
     * This constructor is for users who don't like to create a name
     * @param name
     * @param trackingNumber
     */
    public Package(String trackingNumber) {
        this.name = UNDEFINED_NAME;
        this.trackingNumber = trackingNumber;
    }
}