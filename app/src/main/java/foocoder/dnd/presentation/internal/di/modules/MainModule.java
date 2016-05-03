package foocoder.dnd.presentation.internal.di.modules;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.interactor.DeleteSchedule;
import foocoder.dnd.domain.interactor.GetSchedules;
import foocoder.dnd.domain.interactor.SaveSchedule;
import foocoder.dnd.domain.interactor.ScheduleCase;
import foocoder.dnd.domain.interactor.UpdateSchedule;
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

    @Named("saveSchedule")
    @Provides
    @PerActivity
    ScheduleCase<Boolean> provideSaveScheduleCase(SaveSchedule saveSchedule) {
        return saveSchedule;
    }

    @Named("updateSchedule")
    @Provides
    @PerActivity
    ScheduleCase<Boolean> provideUpdateScheduleCase(UpdateSchedule updateSchedule) {
        return updateSchedule;
    }

    @Named("deleteSchedule")
    @Provides
    @PerActivity
    ScheduleCase<Boolean> provideDeleteScheduleCase(DeleteSchedule deleteSchedule) {
        return deleteSchedule;
    }

    @Provides
    @PerActivity
    ScheduleCase<List<Schedule>> provideGetSchedulesCase(GetSchedules getSchedules) {
        return getSchedules;
    }

    @Provides
    @PerActivity
    Schedule provideScheduleForOperation() {
        return new Schedule();
    }
}
