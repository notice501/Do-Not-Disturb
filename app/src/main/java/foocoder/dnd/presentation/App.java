package foocoder.dnd.presentation;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.tencent.bugly.crashreport.CrashReport;

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

    private static ProfileDBHelper dbHelper;

    private boolean _isUnavailable;

    private SharedPreferenceUtil sp;

    private int originalHeight = 0;

    public static final String START_STOP_ACTION = "foocoder.dnd.startstop";

    public static final String AUTO_TIME_SCHEDULE = "foocoder.dnd.auto";

    private ApplicationComponent applicationComponent;

    @Inject
    Lazy<DeveloperSettings> developerSettingsLazy;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(this, "900021069", BuildConfig.DEBUG);

        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        this.applicationComponent.inject(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            ButterKnife.setDebug(BuildConfig.DEBUG);
            developerSettingsLazy.get().apply();
        }

        dbHelper = new ProfileDBHelper(this, null, null, 1);
        instance = this;
        _isUnavailable = false;
    }

    public static Context getContext() {
        return instance;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public synchronized SharedPreferenceUtil getSharedPreferenceUtil() {
        if (sp == null)
            sp = new SharedPreferenceUtil(this);
        return sp;
    }

    public boolean get_isUnavailable() {
        return _isUnavailable;
    }

    public void set_isUnavailable(boolean _flag) {
        this._isUnavailable = _flag;
    }

//    public void saveNumber(String number,long time){
//        dbHelper.saveNumber(number, time);
//    }


    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }

    public boolean hasNumber(String number) {
        long cutOffTime = System.currentTimeMillis() - 3 * 60 * 1000L;
        Cursor c = getContentResolver().query(
                CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER},
                CallLog.Calls.DATE + ">= ? AND " + CallLog.Calls.NUMBER + " = ? ", new String[]{cutOffTime + "", number},
                CallLog.Calls.DEFAULT_SORT_ORDER);

//        return dbHelper.searchNumber(number) > 0;
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

//    public Schedule getSchedule(long _id){
//        return dbHelper.getSchedule(_id);
//    }

    public boolean delSchedule(Schedule sch) {
        return dbHelper.delSchedule(sch);
    }

    public List<Schedule> getScheduleByDay(String dayOfWeek) {
        return dbHelper.getScheduleByDay(dayOfWeek);
    }

    public void saveContacts(List<Contact> contacts, String state) {
//        dbHelper.clearContacts(state);
//        dbHelper.saveContacts(contacts,state);
    }

    public List<Contact> getContacts() {
        return dbHelper.getContacts();
    }
}
