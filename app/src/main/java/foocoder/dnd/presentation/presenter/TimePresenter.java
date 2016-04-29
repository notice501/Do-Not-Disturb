package foocoder.dnd.presentation.presenter;

import android.support.annotation.CheckResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import foocoder.dnd.presentation.view.TimeSelectView;

/**
 * Created by xuechi.
 * Time: 2016 四月 27 07:47
 * Project: dnd
 */
public class TimePresenter extends Presenter<TimeSelectView> {

    private List<Integer> checkedDays;

    private Schedule schedule;

    @Inject
    public TimePresenter() {
        checkedDays = new ArrayList<>(7);
//        checkedDays.add(0);
//        checkedDays.add(0);
//        checkedDays.add(0);
//        checkedDays.add(0);
//        checkedDays.add(0);
//        checkedDays.add(0);
//        checkedDays.add(0);
    }

    @Override
    public void start() {

    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        checkedDays = schedule.getChecked();
    }

    public void modifyCheckedDay(int index, int checked) {
        checkedDays.set(index, checked);
    }

    @CheckResult
    public Schedule modifySchedule(String from, String to) {
        if(schedule == null){
            schedule = new Schedule(from, to, checkedDays);
        } else {
            schedule.setFrom(from);
            schedule.setTo(to);
            schedule.setChecked(checkedDays);
        }
        return schedule;
    }

    public List<Integer> getCheckedDays() {
        return this.checkedDays;
    }
}
