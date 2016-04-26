package foocoder.dnd.presentation.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import foocoder.dnd.developer.DeveloperModule;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;

@Singleton
@Component(modules = {ApplicationModule.class, DeveloperModule.class})
public interface ApplicationComponent {

    void inject(App app);

    ContactComponent plus(ActivityModule activityModule);
}
