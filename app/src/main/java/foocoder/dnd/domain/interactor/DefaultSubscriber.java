package foocoder.dnd.domain.interactor;

import rx.Subscriber;
import timber.log.Timber;

public class DefaultSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "An error occurred!");
    }

    @Override
    public void onNext(T t) {

    }
}
