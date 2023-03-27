package edu.northeastern.cs5500.starterbot.service;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.northeastern.cs5500.starterbot.service.fedex.FedexCredentials;
import edu.northeastern.cs5500.starterbot.service.fedex.FedexCredentialsFromEnvironment;
import edu.northeastern.cs5500.starterbot.service.fedex.FedexService;

@Module
public class ServiceModule {
    @Provides
    public FedexCredentials provideFedexCredentials(
            FedexCredentialsFromEnvironment fedexCredentials) {
        return fedexCredentials;
    }

    @Provides
    @IntoSet
    public ShipmentTrackingService provideFedexService(FedexService service) {
        return service;
    }

    // @Provides
    // @IntoSet
    // public ShipmentTrackingService provideUpsService(UpsService service) {
    //     return service;
    // }
}
