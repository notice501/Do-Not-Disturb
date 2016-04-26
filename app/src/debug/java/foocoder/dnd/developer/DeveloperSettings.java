package foocoder.dnd.developer;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by xuechi.
 * Time: 2016 四月 26 12:40
 * Project: dnd
 */
public class DeveloperSettings {

    private Application application;

    public DeveloperSettings(Application application) {
        this.application = application;
    }

    public void apply() {
        Stetho.initializeWithDefaults(application);
        LeakCanary.install(application);
        AndroidDevMetrics.initWith(application);
    }
}
