package foocoder.dnd.presentation.internal.di.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.data.repository.ContactDataRepository;
import foocoder.dnd.domain.repository.ContactRepository;
import foocoder.dnd.presentation.App;
import foocoder.dnd.services.ProfileDBHelper;

@Module
public class ApplicationModule {

    private final App app;

    public ApplicationModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplicationContext() {
        return this.app;
    }

    @Provides
//    @Singleton
    ProfileDBHelper provideProfileDBHelper() {
        return new ProfileDBHelper(app, null, null, 1);
    }

    @Provides
    @Singleton
    ContactRepository provideContactRepository(ContactDataRepository contactDataRepository) {
        return contactDataRepository;
    }
}
