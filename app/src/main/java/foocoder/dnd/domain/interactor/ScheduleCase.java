package foocoder.dnd.domain.interactor;

import javax.inject.Inject;

import foocoder.dnd.domain.repository.ScheduleRepository;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xuechi.
 * Time: 2016 四月 29 12:01
 * Project: dnd
 */
public abstract class ScheduleCase<T> {

    @Inject
    ScheduleRepository scheduleRepository;

    public final Subscription execute(Subscriber<T> subscriber) {
        return buildContactCaseObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    protected abstract Observable<T> buildContactCaseObservable();
}
