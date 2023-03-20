package edu.northeastern.cs5500.starterbot.service;

public class FedexToken {
    private String grantType;
    private String clientID;
    private String clientSecret;
    private String childKey;
    private String childSecret;

    public FedexToken (String grantType, String clientID, String clientSecret, String childKey, String childSecret) {
        this.grantType = grantType;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.childKey = childKey;
        this.childSecret = childSecret;
    }

    public FedexToken(String grantType, String clientID, String clientSecret) {
        this.grantType = grantType;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getChildKey() {
        return childKey;
    }

    public String getChildSecret() {
        return childSecret;
    }
}