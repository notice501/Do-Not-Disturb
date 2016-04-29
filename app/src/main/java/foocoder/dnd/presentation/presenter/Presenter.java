package foocoder.dnd.presentation.presenter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import foocoder.dnd.presentation.exception.PreviousViewUnboundedException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xuechi.
 * Time: 2016 四月 24 07:40
 * Project: dnd
 */
abstract class Presenter<VIEW> {

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Nullable
    private VIEW view;

    @CallSuper
    public void bindView(@NonNull VIEW view) {
        if (this.view != null) {
            throw new PreviousViewUnboundedException();
        }

        this.view = view;
    }

    @CallSuper
    public void unbind() {
        this.view = null;

        subscriptions.clear();
    }

    protected final void addSubscriptionsForUnbinding(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            this.subscriptions.add(subscription);
        }
    }

    @Nullable
    public final VIEW getView() {
        return this.view;
    }

    public abstract void start();
}
