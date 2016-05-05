package foocoder.dnd.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import foocoder.dnd.domain.interactor.DefaultSubscriber;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by xuechi.
 * Time: 2016 五月 05 11:40
 * Project: dnd
 */
public final class RxBus {

    private static volatile RxBus instance;

    private SerializedSubject<Object, Object> bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    @NonNull
    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void post(@Nullable Object event) {
        bus.onNext(event);
    }

    public <T> void register(Class<T> eventType, DefaultSubscriber<T> defaultSubscriber) {
        bus.ofType(eventType).subscribe(defaultSubscriber);
    }
}
