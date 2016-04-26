package foocoder.dnd.presentation.internal.di.components;

import dagger.Subcomponent;
import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.internal.di.modules.MainModule;
import foocoder.dnd.presentation.view.activity.MainActivity;

/**
 * Created by xuechi.
 * Time: 2016 四月 27 02:00
 * Project: dnd
 */
@PerActivity
@Subcomponent(modules = {ActivityModule.class, MainModule.class})
public interface MainComponent extends ActivityComponent {

    void inject(MainActivity mainActivity);
}
