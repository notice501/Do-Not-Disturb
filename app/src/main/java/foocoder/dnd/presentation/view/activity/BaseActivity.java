package foocoder.dnd.presentation.view.activity;

import android.app.Activity;

import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.components.ApplicationComponent;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity<COMPONENT> extends Activity {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    ApplicationComponent getApplicationComponent() {
        return ((App) getApplication()).getApplicationComponent();
    }

    ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    void addSubscriptionsForUnbinding(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            this.subscriptions.add(subscription);
        }
    }

    protected abstract COMPONENT getComponent();

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.subscriptions.clear();
    }
}
