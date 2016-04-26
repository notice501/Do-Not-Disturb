package foocoder.dnd.presentation.internal.di.modules;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.presentation.internal.di.PerActivity;

@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @PerActivity
    @Provides
    Activity provideActivity() {
        return this.activity;
    }
}
