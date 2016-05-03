package foocoder.dnd.domain.interactor;

import android.support.annotation.CheckResult;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@SuppressWarnings("unchecked")
public abstract class ContactCase {

    ContactCase() {}

    @CheckResult
    public final Subscription execute(Subscriber subscriber) {
        return buildContactCaseObservable()
                .compose(getTransformer())
                .subscribe(subscriber);
    }

    protected abstract Observable buildContactCaseObservable();

    private Observable.Transformer<?, ?> getTransformer() {
        return objectObservable -> objectObservable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
