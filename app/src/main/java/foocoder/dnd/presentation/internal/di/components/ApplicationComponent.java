package foocoder.dnd.presentation.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import foocoder.dnd.developer.DeveloperModule;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.internal.di.modules.ApplicationModule;
import foocoder.dnd.presentation.internal.di.modules.ScheduleModule;
import foocoder.dnd.presentation.internal.di.modules.SystemServiceModule;
import foocoder.dnd.presentation.view.fragment.TimeDialogFragment;
import foocoder.dnd.services.StartStopReceiver;
import foocoder.dnd.services.TimeReceiver;

@Singleton
@Component(modules = {ApplicationModule.class, DeveloperModule.class,
        ScheduleModule.class, SystemServiceModule.class})
public interface ApplicationComponent {

    void inject(App app);

    void inject(TimeDialogFragment fragment);

    void inject(StartStopReceiver receiver);

    void inject(TimeReceiver receiver);

    ContactComponent.Builder contactComponentBuilder();

    MainComponent add(ActivityModule activityModule);
}
