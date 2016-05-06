package foocoder.dnd.domain.interactor;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 四月 30 03:30
 * Project: dnd
 */
public class SaveSchedule extends ScheduleCase<Boolean> {

    private Schedule schedule;

    @Inject
    public SaveSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    protected Observable<Boolean> buildContactCaseObservable() {
        return this.scheduleRepository.saveSchedule(schedule);
    }
}
