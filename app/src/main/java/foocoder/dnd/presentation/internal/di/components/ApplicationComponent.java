package foocoder.dnd.presentation.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;
import foocoder.dnd.presentation.view.activity.BaseActivity;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(App app);

    void inject(BaseActivity baseActivity);

    ContactComponent plus(ActivityModule activityModule);
}
