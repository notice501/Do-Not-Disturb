package foocoder.dnd.data.repository;

import android.app.Application;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.repository.ScheduleRepository;
import foocoder.dnd.services.ProfileDBHelper;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by xuechi.
 * Time: 2016 四月 30 02:17
 * Project: dnd
 */
@Singleton
public class ScheduleDataRepository implements ScheduleRepository {

    @Inject
    ProfileDBHelper dbHelper;

    @Inject
    Application context;

    @Inject
    public ScheduleDataRepository() {}

    @Override
    public Observable<List<Schedule>> schedules() {
        return Observable.create(new Observable.OnSubscribe<List<Schedule>>() {
            @Override
            public void call(Subscriber<? super List<Schedule>> subscriber) {
                try {
                    subscriber.onNext(dbHelper.getScheduleList());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Schedule> schedule(int _id) {
        return null;
    }

    @Override
    public Observable saveSchedules(List<Schedule> schedules) {
        return Observable.create(subscriber -> {
            try {
                dbHelper.saveSchedules(schedules);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable saveSchedule(Schedule schedule) {
        return Observable.create(subscriber -> {
            try {
                dbHelper.saveSchedule(schedule);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
