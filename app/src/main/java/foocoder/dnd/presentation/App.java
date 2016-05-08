package foocoder.dnd.presentation;

import android.app.AlarmManager;
import android.app.Application;
import android.media.AudioManager;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.Lazy;
import foocoder.dnd.BuildConfig;
import foocoder.dnd.developer.DeveloperSettings;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.presentation.internal.di.components.ApplicationComponent;
import foocoder.dnd.presentation.internal.di.components.DaggerApplicationComponent;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;
import foocoder.dnd.services.ProfileDBHelper;
import foocoder.dnd.utils.SharedPreferenceUtil;
import timber.log.Timber;

public class App extends Application {

    public static final String START_STOP_ACTION = "foocoder.dnd.startstop";

    public static final String AUTO_TIME_SCHEDULE = "foocoder.dnd.auto";

    private static App instance;

    @Inject
    SharedPreferenceUtil sp;

    @Inject
    Lazy<DeveloperSettings> developerSettingsLazy;

    @Inject
    ProfileDBHelper dbHelper;

    @Inject
    AudioManager audioManager;

    @Inject
    AlarmManager alarmManager;

    private ApplicationComponent applicationComponent;

    public static App getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.applicationComponent = provideApplicationComponent();

        this.applicationComponent.inject(this);
        JodaTimeAndroid.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            ButterKnife.setDebug(BuildConfig.DEBUG);
            developerSettingsLazy.get().apply();
        }

        instance = this;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    protected ApplicationComponent provideApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public SharedPreferenceUtil getSharedPreferenceUtil() {
        return sp;
    }

    public AudioManager getAudioManager() {
        return this.audioManager;
    }

    public AlarmManager getAlarmManager() {
        return this.alarmManager;
    }

    public boolean hasNumber(String number) {
//        long cutOffTime = System.currentTimeMillis() - 3 * 60 * 1000L;
//        Cursor c = getContentResolver().query(
//                CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER},
//                CallLog.Calls.DATE + ">= ? AND " + CallLog.Calls.NUMBER + " = ? ", new String[]{cutOffTime + "", number},
//                CallLog.Calls.DEFAULT_SORT_ORDER);
//
//        return c != null && c.moveToNext();
        return false;
    }

    public void scanNumbers() {
        dbHelper.scanTempNumbers();
    }

    public List<Contact> getContacts() {
        return dbHelper.getContacts();
    }
}
