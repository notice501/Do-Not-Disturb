package foocoder.dnd.domain.interactor;

import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 四月 30 05:50
 * Project: dnd
 */
public class GetSchedules extends ScheduleCase<List<Schedule>> {

    @Inject
    public GetSchedules() {}

    @Override
    protected Observable<List<Schedule>> buildContactCaseObservable() {
        return scheduleRepository.schedules();
    }
}
