package foocoder.dnd.presentation.presenter;

import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
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

    @NonNull
    private List<Integer> checkedDays;

    @Nullable
    private Schedule schedule;

    @Inject
    public TimePresenter() {
        checkedDays = Arrays.asList(0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public void start() {

    }

    public void setSchedule(@Nullable Schedule schedule) {
        this.schedule = schedule;
        if (schedule != null) {
            checkedDays = schedule.days;
        }
    }

    public void modifyCheckedDay(@IntRange(from = 0, to = 6) int index,
                                 @IntRange(from = 0, to = 1) int checked) {
        checkedDays.set(index, checked);
    }

    @CheckResult
    public Schedule modifySchedule(String from, String to) {
        if(schedule == null){
            schedule = new Schedule(from, to, checkedDays);
        } else {
            schedule.from = from;
            schedule.to = to;
            schedule.days = checkedDays;
        }
        return schedule;
    }

    @NonNull
    public List<Integer> getCheckedDays() {
        return this.checkedDays;
    }
}
