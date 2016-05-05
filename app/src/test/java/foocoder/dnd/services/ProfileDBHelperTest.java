package foocoder.dnd.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import foocoder.dnd.BuildConfig;
import foocoder.dnd.TestApp;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.Schedule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by xuechi.
 * Time: 2016 五月 04 17:56
 * Project: dnd
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class, packageName = "foocoder.dnd", sdk = 21)
public class ProfileDBHelperTest {

    private ProfileDBHelper profileDBHelper;

    @Before
    public void setUp() throws Exception {
        profileDBHelper = new ProfileDBHelper(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() throws Exception {
        profileDBHelper = null;
    }

    @Test
    public void testSaveNumber() throws Exception {

    }

    @Test
    public void testSearchNumber() throws Exception {

    }

    @Test
    public void testScanTempNumbers() throws Exception {

    }

    @Test
    public void testUpdateSchedule() throws Exception {
        Schedule schedule = new Schedule();
        schedule._id = 1;
        schedule.checked = Arrays.asList(0, 0, 0, 0, 0, 0, 0);

        boolean saveResult = profileDBHelper.saveSchedule(schedule);
        assertTrue(saveResult);

        schedule.from = "10:00";
        boolean result = profileDBHelper.updateSchedule(schedule);
        assertTrue(result);

        Schedule updated = profileDBHelper.getSchedule(schedule._id);
        assertNotNull(updated);
        assertThat(updated.from, is("10:00"));
    }

    @Test
    public void testDelSchedule() throws Exception {
        Schedule schedule = new Schedule();
        schedule._id = 1;
        schedule.checked = Arrays.asList(0, 0, 0, 0, 0, 0, 0);

        boolean saveResult = profileDBHelper.saveSchedule(schedule);
        assertTrue(saveResult);

        boolean result = profileDBHelper.delSchedule(schedule);
        assertTrue(result);

        Schedule deleted = profileDBHelper.getSchedule(schedule._id);
        assertNull(deleted);
    }

    @Test
    public void saveSchedules_getScheduleList() throws Exception {
        Schedule schedule = new Schedule("12:00", "13:00", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        schedule._id = 11;
        Schedule schedule1 = new Schedule("07:00", "13:00", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        schedule1._id = 21;
        Schedule schedule2 = new Schedule("09:00", "19:00", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        schedule2._id = 31;
        Schedule schedule3 = new Schedule("14:00", "13:00", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        schedule3._id = 41;

        List<Schedule> scheduleList = Arrays.asList(schedule, schedule1, schedule2, schedule3);
        profileDBHelper.saveSchedules(scheduleList);

        List<Schedule> schedules = profileDBHelper.getScheduleList();
        assertThat(schedules.size(), is(scheduleList.size()));

        Random random = new Random();
        int index = random.nextInt(scheduleList.size());
        assertThat(schedules.get(index)._id, is(scheduleList.get(index)._id));
        assertThat(schedules.get(index).from, is(scheduleList.get(index).from));
        assertThat(schedules.get(index).to, is(scheduleList.get(index).to));
        assertThat(schedules.get(index).checked, is(scheduleList.get(index).checked));
    }

    @Test
    public void saveSchedule_getSchedule() throws Exception {
        Schedule schedule = new Schedule("12:00", "13:00", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        schedule._id = 12;

        boolean saveResult = profileDBHelper.saveSchedule(schedule);
        assertTrue(saveResult);

        Schedule schedule1 = profileDBHelper.getSchedule(schedule._id);
        assertThat(schedule._id, is(schedule1._id));
        assertThat(schedule.from, is(schedule1.from));
        assertThat(schedule.to, is(schedule1.to));
        assertThat(schedule.checked, is(schedule1.checked));
    }

    @Test
    public void testGetScheduleByDay() throws Exception {

    }

    @Test
    public void saveContacts_getContacts() throws Exception {
        Contact contact = new Contact("11", "test1", "1234");
        Contact contact1 = new Contact("21", "test2", "1234");
        Contact contact2 = new Contact("31", "test3", "1234");
        Contact contact3 = new Contact("41", "test4", "1234");

        List<Contact> contactList = Arrays.asList(contact, contact1, contact2, contact3);
        profileDBHelper.saveContacts(contactList);

        List<Contact> contacts = profileDBHelper.getContacts();
        assertThat(contacts.size(), is(contactList.size()));
        Random random = new Random();
        int index = random.nextInt(contacts.size());
        assertThat(contacts.get(index), is(contactList.get(index)));
        assertThat(contacts.get(index).name, is(contactList.get(index).name));
        assertThat(contacts.get(index).phoneNo, is(contactList.get(index).phoneNo));
    }

    @Test
    public void saveContacts_clearContacts_getNoContact() throws Exception {
        Contact contact = new Contact("12", "test1", "1234");
        Contact contact1 = new Contact("22", "test2", "1234");
        Contact contact2 = new Contact("32", "test3", "1234");
        Contact contact3 = new Contact("42", "test4", "1234");

        List<Contact> contactList = Arrays.asList(contact, contact1, contact2, contact3);
        profileDBHelper.saveContacts(contactList);

        List<Contact> contacts = profileDBHelper.getContacts();
        assertThat(contacts.size(), is(contactList.size()));

        boolean result = profileDBHelper.clearContacts();
        assertTrue(result);

        contacts = profileDBHelper.getContacts();
        assertThat(contacts.size(), is(0));
    }
}