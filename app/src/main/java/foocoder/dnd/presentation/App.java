package foocoder.dnd.presentation;

import android.app.AlarmManager;
import android.app.Application;
import android.media.AudioManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.Lazy;
import foocoder.dnd.BuildConfig;
import foocoder.dnd.developer.DeveloperSettings;
import foocoder.dnd.presentation.internal.di.components.ApplicationComponent;
import foocoder.dnd.presentation.internal.di.components.DaggerApplicationComponent;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;
import foocoder.dnd.receivers.CallReceiver;
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
        instance = this;

        this.applicationComponent = provideApplicationComponent();

        this.applicationComponent.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            ButterKnife.setDebug(BuildConfig.DEBUG);
            developerSettingsLazy.get().apply();
        }
        CallReceiver.IncomingListener.init(this);
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
}
