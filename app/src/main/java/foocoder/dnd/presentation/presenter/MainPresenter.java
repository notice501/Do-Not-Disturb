package foocoder.dnd.presentation.presenter;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.interactor.DefaultSubscriber;
import foocoder.dnd.domain.interactor.ScheduleCase;
import foocoder.dnd.presentation.App;
import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.view.MainSettingView;
import foocoder.dnd.utils.AlarmUtil;
import foocoder.dnd.utils.SharedPreferenceUtil;

@PerActivity
public class MainPresenter extends Presenter<MainSettingView> {

    @Inject
    SharedPreferenceUtil spUtil;

    @Inject
    List<Schedule> schedules;

    @Inject
    ScheduleCase<Schedule> saveSchedule;

    @Inject
    ScheduleCase<List<Schedule>> getScheduleList;

    @Inject
    public MainPresenter() {}

    @Override
    public void start() {
        addSubscriptionsForUnbinding(
                getScheduleList.
                        execute(new DefaultSubscriber<List<Schedule>>() {
                            @Override
                            public void onNext(List<Schedule> scheduleList) {
                                schedules.addAll(scheduleList);
                                if (getView() != null) {
                                    getView().showSchedules();
                                }
                            }
                        }));
    }

    @Override
    public void bindView(@NonNull MainSettingView view) {
        super.bindView(view);

        view.changeState(false);
        view.changeAutoRecoverState(spUtil.isRecoverable());
        view.changeLauncherState(spUtil.isLaunched());
        view.changeTimerState(spUtil.isStarted());
        view.changeVibrationState(spUtil.isVib());
        view.changeRepetitionState(spUtil.isRepeated());
    }

    public List<Schedule> getSchedules() {
        return this.schedules;
    }

    public void addSchedule(Schedule schedule) {
        int _id = spUtil.getId() + 2;
        schedule._id = _id;
        spUtil.setId(_id);
        if (!spUtil.isStarted()) {
            AlarmUtil.startSchedule(App.getContext(), schedule);
        }
        schedules.add(schedule);
        addSubscriptionsForUnbinding(
                saveSchedule.execute(
                        new DefaultSubscriber<Schedule>() {
            @Override
            public void onNext(Schedule schedule) {
                if (getView() != null) {
                    getView().showSchedules();
                }
            }
        }));
    }
}
