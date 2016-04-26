package foocoder.dnd.developer;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xuechi.
 * Time: 2016 四月 26 12:37
 * Project: dnd
 */
@Module
public class DeveloperModule {

    @NonNull
    @Provides
    @Singleton
    DeveloperSettings provideDeveloperSettings(Application application) {
        return new DeveloperSettings(application);
    }
}
