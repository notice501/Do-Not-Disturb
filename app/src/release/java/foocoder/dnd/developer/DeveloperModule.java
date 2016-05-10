package foocoder.dnd.developer;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xuechi.
 * Time: 2016 五月 10 17:58
 * Project: dnd
 */
@Module
public class DeveloperModule {

    @NonNull
    @Provides
    @Singleton
    protected DeveloperSettings provideDeveloperSettings(Application application) {
        return new DeveloperSettings(application);
    }
}