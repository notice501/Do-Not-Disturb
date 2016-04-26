package foocoder.dnd.presentation.view.activity;

import android.support.v7.app.AppCompatActivity;

import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.components.ApplicationComponent;
import foocoder.dnd.presentation.internal.di.modules.ActivityModule;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity<T> extends AppCompatActivity {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    protected ApplicationComponent getApplicationComponent() {
        return ((App) getApplication()).getApplicationComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    protected void addSubscriptionsForUnbinding(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            this.subscriptions.add(subscription);
        }
    }

    protected abstract T getComponent();

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.subscriptions.clear();
    }
}
