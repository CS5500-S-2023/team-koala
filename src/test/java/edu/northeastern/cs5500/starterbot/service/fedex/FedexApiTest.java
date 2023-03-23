package edu.northeastern.cs5500.starterbot.service.fedex;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import edu.northeastern.cs5500.starterbot.exception.NotFoundException;
import edu.northeastern.cs5500.starterbot.model.TrackingInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "FEDEX_CLIENT_ID", matches = ".+")
class FedexApiTest {
    @Test
    void testCanGetToken() throws Exception {
        FedexService f = new FedexService(new FedexCredentialsFromEnvironment());
        FedexAccessToken accessToken = f.getAccessToken();
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.getAccessToken()).isNotEmpty();
        assertThat(accessToken.getExpiresIn()).isAtLeast(1);
        assertThat(accessToken.getScope()).isNotEmpty();
        assertThat(accessToken.getTokenType()).isNotEmpty();
    }

    @Test
    void testCanGetTrackingDescription() throws Exception {
        FedexService f = new FedexService(new FedexCredentialsFromEnvironment());
        TrackingInformation trackingInformation = f.getTrackingInformation("123456789012");
        assertThat(trackingInformation).isNotNull();
        assertThat(trackingInformation.getTrackingNumber()).isNotEmpty();
        assertThat(trackingInformation.getDescription()).isNotEmpty();
    }

    @Test
    void testInvalidTrackingNumberRaisesNotFoundException() throws Exception {
        FedexService f = new FedexService(new FedexCredentialsFromEnvironment());
        try {
            f.getTrackingInformation("123456789012345");
            fail("Expected NotFoundException");
        } catch (NotFoundException e) {
            // expected
        }
    }
}
