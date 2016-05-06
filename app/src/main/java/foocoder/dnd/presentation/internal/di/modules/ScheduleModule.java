package foocoder.dnd.presentation.internal.di.modules;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.domain.interactor.DeleteSchedule;
import foocoder.dnd.domain.interactor.GetSchedules;
import foocoder.dnd.domain.interactor.SaveSchedule;
import foocoder.dnd.domain.interactor.ScheduleCase;
import foocoder.dnd.domain.interactor.UpdateSchedule;

/**
 * Created by xuechi.
 * Time: 2016 五月 07 01:59
 * Project: dnd
 */
@Module
public class ScheduleModule {

    @Named("saveSchedule")
    @Provides
    @Singleton
    ScheduleCase<Boolean> provideSaveScheduleCase(SaveSchedule saveSchedule) {
        return saveSchedule;
    }

    @Named("updateSchedule")
    @Provides
    @Singleton
    ScheduleCase<Boolean> provideUpdateScheduleCase(UpdateSchedule updateSchedule) {
        return updateSchedule;
    }

    @Named("deleteSchedule")
    @Provides
    @Singleton
    ScheduleCase<Boolean> provideDeleteScheduleCase(DeleteSchedule deleteSchedule) {
        return deleteSchedule;
    }

    @Provides
    @Singleton
    ScheduleCase<List<Schedule>> provideGetSchedulesCase(GetSchedules getSchedules) {
        return getSchedules;
    }

    @Provides
    @Singleton
    Schedule provideScheduleForOperation() {
        return new Schedule();
    }
}
