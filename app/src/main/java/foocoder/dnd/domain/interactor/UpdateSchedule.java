package foocoder.dnd.domain.interactor;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 五月 03 12:45
 * Project: dnd
 */
public class UpdateSchedule extends ScheduleCase<Boolean> {

    @Inject
    Schedule schedule;

    @Inject
    public UpdateSchedule() {}

    @Override
    protected Observable<Boolean> buildContactCaseObservable() {
        return scheduleRepository.updateSchedule(schedule);
    }
}
