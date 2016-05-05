package foocoder.dnd.presentation.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import foocoder.dnd.developer.DeveloperModule;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;
import foocoder.dnd.presentation.view.fragment.TimeDialogFragment;

@Singleton
@Component(modules = {ApplicationModule.class, DeveloperModule.class})
public interface ApplicationComponent {

    void inject(App app);

    void inject(TimeDialogFragment fragment);

    ContactComponent.Builder contactComponentBuilder();

    MainComponent add(ActivityModule activityModule);
}
