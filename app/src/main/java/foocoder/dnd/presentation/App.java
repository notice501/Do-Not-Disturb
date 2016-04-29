package foocoder.dnd.presentation;

import android.app.Application;
import android.database.Cursor;
import android.provider.CallLog;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.Lazy;
import foocoder.dnd.BuildConfig;
import foocoder.dnd.developer.DeveloperSettings;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.presentation.internal.di.components.ApplicationComponent;
import foocoder.dnd.presentation.internal.di.components.DaggerApplicationComponent;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;
import foocoder.dnd.services.ProfileDBHelper;
import foocoder.dnd.utils.SharedPreferenceUtil;
import timber.log.Timber;

public class App extends Application {

    private static App instance;

    private boolean _isUnavailable;

    public static final String START_STOP_ACTION = "foocoder.dnd.startstop";

    public static final String AUTO_TIME_SCHEDULE = "foocoder.dnd.auto";

    private ApplicationComponent applicationComponent;

    @Inject
    SharedPreferenceUtil sp;

    @Inject
    Lazy<DeveloperSettings> developerSettingsLazy;

    @Inject
    ProfileDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashReport.initCrashReport(this, "900021069", BuildConfig.DEBUG);

        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        this.applicationComponent.inject(this);
        JodaTimeAndroid.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            ButterKnife.setDebug(BuildConfig.DEBUG);
            developerSettingsLazy.get().apply();
        }

        instance = this;
        _isUnavailable = false;
    }

    public static App getContext() {
        return instance;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public synchronized SharedPreferenceUtil getSharedPreferenceUtil() {
        return sp;
    }

    public boolean get_isUnavailable() {
        return _isUnavailable;
    }

    public void set_isUnavailable(boolean _flag) {
        this._isUnavailable = _flag;
    }

    public boolean hasNumber(String number) {
        long cutOffTime = System.currentTimeMillis() - 3 * 60 * 1000L;
        Cursor c = getContentResolver().query(
                CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER},
                CallLog.Calls.DATE + ">= ? AND " + CallLog.Calls.NUMBER + " = ? ", new String[]{cutOffTime + "", number},
                CallLog.Calls.DEFAULT_SORT_ORDER);

        return c != null && c.moveToNext();
    }

    public void scanNumbers() {
        dbHelper.scanTempNumbers();
    }

    public boolean saveSchedule(Schedule sch) {
        return dbHelper.saveSchedule(sch);
    }

    public Schedule getScheduleById(int _id) {
        return dbHelper.getSchedule(_id);
    }

    public boolean updateSchedule(Schedule sch) {
        return dbHelper.updateSchedule(sch);
    }

    public List<Schedule> getScheduleList() {
        return dbHelper.getScheduleList();
    }

    public boolean delSchedule(Schedule sch) {
        return dbHelper.delSchedule(sch);
    }

    public List<Contact> getContacts() {
        return dbHelper.getContacts();
    }
}
