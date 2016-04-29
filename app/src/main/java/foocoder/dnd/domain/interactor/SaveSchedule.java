package foocoder.dnd.domain.interactor;

import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 四月 30 03:30
 * Project: dnd
 */
public class SaveSchedule extends ScheduleCase<Schedule> {

    private List<Schedule> scheduleList;

    @Inject
    public SaveSchedule(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @Override
    protected Observable<Schedule> buildContactCaseObservable() {
        return this.scheduleRepository.saveSchedule(scheduleList.get(scheduleList.size() -1));
    }
}
