package foocoder.dnd.domain.repository;

import java.util.List;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 四月 29 12:00
 * Project: dnd
 */
public interface ScheduleRepository {
    Observable<List<Schedule>> schedules();

    Observable<Schedule> schedule(int _id);

    Observable<Boolean> saveSchedules(List<Schedule> schedules);

    Observable<Boolean> saveSchedule(Schedule schedule);

    Observable<Boolean> updateSchedule(Schedule schedule);

    Observable<Boolean> deleteSchedule(Schedule schedule);
}
