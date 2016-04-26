package foocoder.dnd.presentation.internal.di.components;

import android.app.Activity;

import dagger.Component;
import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity activity();
}
