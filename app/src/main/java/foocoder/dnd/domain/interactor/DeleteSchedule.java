package foocoder.dnd.domain.interactor;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 五月 03 14:29
 * Project: dnd
 */
public class DeleteSchedule extends ScheduleCase<Boolean> {

    @Inject
    Schedule schedule;

    @Inject
    public DeleteSchedule() {}

    @Override
    protected Observable<Boolean> buildContactCaseObservable() {
        return this.scheduleRepository.deleteSchedule(schedule);
    }
}
