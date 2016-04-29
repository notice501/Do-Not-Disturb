package foocoder.dnd.presentation.internal.di.modules;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.interactor.GetSchedules;
import foocoder.dnd.domain.interactor.SaveSchedule;
import foocoder.dnd.domain.interactor.ScheduleCase;
import foocoder.dnd.presentation.internal.di.PerActivity;

/**
 * Created by xuechi.
 * Time: 2016 四月 27 02:00
 * Project: dnd
 */
@Module
public class MainModule {

    @Provides
    @PerActivity
    List<Schedule> provideScheduleList() {
        return new ArrayList<>();
    }

    @Provides
    @PerActivity
    ScheduleCase<Schedule> provideSaveScheduleCase(SaveSchedule saveSchedule) {
        return saveSchedule;
    }

    @Provides
    @PerActivity
    ScheduleCase<List<Schedule>> provideGetSchedulesCase(GetSchedules getSchedules) {
        return getSchedules;
    }
}
