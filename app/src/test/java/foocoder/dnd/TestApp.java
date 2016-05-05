package foocoder.dnd;

import android.app.Application;
import android.support.annotation.NonNull;

import foocoder.dnd.developer.DeveloperModule;
import foocoder.dnd.developer.DeveloperSettings;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.components.ApplicationComponent;
import foocoder.dnd.presentation.internal.di.components.DaggerApplicationComponent;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;

/**
 * Created by xuechi.
 * Time: 2016 五月 05 17:28
 * Project: dnd
 */
public class TestApp extends App {

    @Override
    protected ApplicationComponent provideApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .developerModule(new DeveloperModule() {
                    @NonNull
                    @Override
                    protected DeveloperSettings provideDeveloperSettings(Application application) {
                        return new DeveloperSettings(application) {
                            @Override
                            public void apply() {}
                        };
                    }
                }).build();
    }
}
