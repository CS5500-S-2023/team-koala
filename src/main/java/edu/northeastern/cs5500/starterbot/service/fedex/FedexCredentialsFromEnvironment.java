package edu.northeastern.cs5500.starterbot.service.fedex;

import javax.inject.Inject;

public class FedexCredentialsFromEnvironment implements FedexCredentials {
    @Inject
    public FedexCredentialsFromEnvironment() {
        // empty for dependency injection
    }

    public String getClientId() {
        return new ProcessBuilder().environment().get("FEDEX_CLIENT_ID");
    }

    public String getClientSecret() {
        return new ProcessBuilder().environment().get("FEDEX_CLIENT_SECRET");
    }
}
