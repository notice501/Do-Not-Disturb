package foocoder.dnd.domain.interactor;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 五月 08 01:36
 * Project: dnd
 */
public class GetSchedule extends ScheduleCase<Schedule> {

    @Inject
    Schedule schedule;

    @Inject
    public GetSchedule() {}

    @Override
    protected Observable<Schedule> buildContactCaseObservable() {
        return this.scheduleRepository.schedule(schedule._id);
    }
}
