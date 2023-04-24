package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.Package;
import edu.northeastern.cs5500.starterbot.model.ReminderEntry;

@Module
public class RepositoryModule {

    @Provides
    public GenericRepository<ReminderEntry> provideReminderEntryRepository(
            MongoDBRepository<ReminderEntry> repository) {
        return repository;
    }

    @Provides
    public Class<ReminderEntry> provideReminderEntry() {
        return ReminderEntry.class;
    }

    @Provides
    public GenericRepository<Package> providePackageRepository(
            MongoDBRepository<Package> repository) {
        return repository;
    }

    @Provides
    public Class<Package> providePackage() {
        return Package.class;
    }
}
