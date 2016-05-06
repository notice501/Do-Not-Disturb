package foocoder.dnd.data.repository;

import android.app.Application;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import foocoder.dnd.data.exception.TimeNotSetException;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.repository.ScheduleRepository;
import foocoder.dnd.services.ProfileDBHelper;
import rx.Observable;

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
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(dbHelper.getScheduleList());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Schedule> schedule(int _id) {
        return Observable.fromCallable(() -> dbHelper.getSchedule(_id));
    }

    @Override
    public Observable<Boolean> saveSchedules(List<Schedule> schedules) {
        return Observable.from(schedules)
                .map(schedule -> {
                    if (schedule.from == null || schedule.to == null) {
                        throw new TimeNotSetException();
                    }

                    return dbHelper.saveSchedule(schedule);
                });
    }

    @Override
    public Observable<Boolean> saveSchedule(Schedule schedule) {
        return Observable.create(subscriber -> {
            if (schedule.from == null || schedule.to == null) {
                throw new TimeNotSetException();
            }

            subscriber.onNext(dbHelper.saveSchedule(schedule));
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> updateSchedule(Schedule schedule) {
        return Observable.create(subscriber -> {
            if (schedule.from == null || schedule.to == null) {
                throw new TimeNotSetException();
            }

            subscriber.onNext(dbHelper.updateSchedule(schedule));
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> deleteSchedule(Schedule schedule) {
        return Observable.fromCallable(() -> dbHelper.delSchedule(schedule));
    }
}
