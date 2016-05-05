package foocoder.dnd.data.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import foocoder.dnd.data.exception.TimeNotSetException;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.services.ProfileDBHelper;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by xuechi.
 * Time: 2016 五月 04 16:13
 * Project: dnd
 */
public class ScheduleDataRepositoryTest {

    private ScheduleDataRepository scheduleDataRepository;

    private ProfileDBHelper profileDBHelper;

    @Before
    public void setUp() throws Exception {
        profileDBHelper = mock(ProfileDBHelper.class);
        scheduleDataRepository = new ScheduleDataRepository();
        scheduleDataRepository.dbHelper = profileDBHelper;
    }

    @After
    public void tearDown() throws Exception {
        profileDBHelper = null;
    }

    @Test
    public void testSchedules() throws Exception {
        TestSubscriber<List<Schedule>> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.schedules().subscribe(testSubscriber);

        verify(profileDBHelper).getScheduleList();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void testSchedule() throws Exception {
        TestSubscriber<Schedule> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.schedule(anyInt()).subscribe(testSubscriber);

        verify(profileDBHelper).getSchedule(anyInt());
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void testSaveSchedules() throws Exception {
        List<Schedule> schedules = new ArrayList<>();
        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.saveSchedules(schedules).subscribe(testSubscriber);

        verify(profileDBHelper).saveSchedules(schedules);
        testSubscriber.assertValueCount(0);
        testSubscriber.assertCompleted();
    }

    @Test
    public void testSaveSchedule() throws Exception {
        Schedule schedule = new Schedule();
        schedule._id = 1;
        schedule.from = "12:00";
        schedule.to = "14:00";
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.saveSchedule(schedule).subscribe(testSubscriber);

        verify(profileDBHelper).saveSchedule(schedule);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void testUpdateSchedule() throws Exception {
        Schedule schedule = new Schedule();
        schedule.from = "12:00";
        schedule.to = "16:00";
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.updateSchedule(schedule).subscribe(testSubscriber);

        verify(profileDBHelper).updateSchedule(schedule);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void testDeleteSchedule() throws Exception {
        Schedule schedule = new Schedule();
        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.deleteSchedule(schedule).subscribe(testSubscriber);

        verify(profileDBHelper).delSchedule(schedule);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void saveScheduleWithUnsetTime() {
        Schedule schedule = new Schedule();
        schedule._id = 1;
        assertThat(schedule.from).isNull();

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();
        scheduleDataRepository.saveSchedule(schedule).subscribe(testSubscriber);
        testSubscriber.assertError(TimeNotSetException.class);
    }
}