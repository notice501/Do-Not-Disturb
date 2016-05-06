package foocoder.dnd.presentation.internal.di.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.data.repository.ContactDataRepository;
import foocoder.dnd.data.repository.ScheduleDataRepository;
import foocoder.dnd.domain.repository.ContactRepository;
import foocoder.dnd.domain.repository.ScheduleRepository;
import foocoder.dnd.presentation.App;
import foocoder.dnd.services.ProfileDBHelper;
import foocoder.dnd.utils.SharedPreferenceUtil;
import foocoder.dnd.utils.rxpreference.RxPreference;

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
    @Singleton
    App provideApp() {
        return this.app;
    }

    @Provides
    @Singleton
    ProfileDBHelper provideProfileDBHelper() {
        return new ProfileDBHelper(app);
    }

    @Provides
    @Singleton
    ContactRepository provideContactRepository(ContactDataRepository contactDataRepository) {
        return contactDataRepository;
    }

    @Provides
    @Singleton
    ScheduleRepository provideScheduleRepository(ScheduleDataRepository scheduleRepository) {
        return scheduleRepository;
    }

    @Provides
    @Singleton
    SharedPreferenceUtil provideSharedPreferenceUtil(RxPreference rxPreference) {
        return new SharedPreferenceUtil(rxPreference);
    }

    @Provides
    @Singleton
    RxPreference provideRxPreference(Application context) {
        return RxPreference.getInstance("dnd", context);
    }
}
