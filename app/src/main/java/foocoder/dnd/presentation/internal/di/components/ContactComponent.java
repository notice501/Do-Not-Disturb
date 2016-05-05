package foocoder.dnd.presentation.internal.di.components;

import dagger.Subcomponent;
import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import foocoder.dnd.presentation.internal.di.modules.ContactModule;
import foocoder.dnd.presentation.view.activity.ContactListActivity;

@PerActivity
@Subcomponent(modules = {ActivityModule.class, ContactModule.class})
public interface ContactComponent extends ActivityComponent {

    void inject(ContactListActivity activity);

    @Subcomponent.Builder
    interface Builder {
        Builder activityModule(ActivityModule activityModule);
        ContactComponent build();
    }
}
