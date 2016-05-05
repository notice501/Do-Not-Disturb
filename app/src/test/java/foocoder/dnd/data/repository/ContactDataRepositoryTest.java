package foocoder.dnd.data.repository;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.ContactsContract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboCursor;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.ArrayList;
import java.util.List;

import foocoder.dnd.BuildConfig;
import foocoder.dnd.TestApp;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.services.ProfileDBHelper;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by xuechi.
 * Time: 2016 五月 03 20:16
 * Project: dnd
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class, packageName = "foocoder.dnd", sdk = 21)
public class ContactDataRepositoryTest {

    private ContactDataRepository contactDataRepository;

    private ProfileDBHelper profileDBHelper;

    private ShadowContentResolver shadowContentResolver;

    private Uri uri;

    private Object[][] results;

    @Before
    public void setup() {
        profileDBHelper = mock(ProfileDBHelper.class);
        contactDataRepository = new ContactDataRepository(profileDBHelper, RuntimeEnvironment.application);
        ContentResolver contentResolver = RuntimeEnvironment.application.getContentResolver();
        shadowContentResolver = Shadows.shadowOf(contentResolver);
        uri = ContactsContract.Data.CONTENT_URI;
        RoboCursor baseCursor = new RoboCursor();
        results = new Object[][]{{"1", "test1", "1234"}, {"2", "test2", "1234"}, {"3", "test3", "1234"}};
        baseCursor.setResults(results);
        shadowContentResolver.setCursor(uri, baseCursor);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getOneContactUnsupported() {
        contactDataRepository.contact(1);
    }

    @Test
    public void getContactsFromUri() {
        TestSubscriber<List<Contact>> testSubscriber = new TestSubscriber<>();
        contactDataRepository.contacts().subscribe(testSubscriber);

        verify(profileDBHelper).getContacts();
        testSubscriber.assertValueCount(2);
        testSubscriber.assertCompleted();
        assertThat(testSubscriber.getOnNextEvents().get(0).size(), is(0));
        assertTrue(testSubscriber.getOnNextEvents().get(1).size() == 3);
        assertThat(testSubscriber.getOnNextEvents().get(1).get(1)._id, is(results[1][0]));
    }

    @Test
    public void wrongQuery_getNoContacts() {
        shadowContentResolver.setCursor(uri, null);
        TestSubscriber<List<Contact>> testSubscriber = new TestSubscriber<>();
        contactDataRepository.contacts().subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);
        assertThat(testSubscriber.getOnNextEvents().get(0).size(), is(0));
        testSubscriber.assertCompleted();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void saveContacts() {
        TestSubscriber testSubscriber = new TestSubscriber();
        contactDataRepository.saveContacts(new ArrayList<>()).subscribe(testSubscriber);

        verify(profileDBHelper).clearContacts();
        verify(profileDBHelper).saveContacts(anyListOf(Contact.class));
        testSubscriber.assertCompleted();
    }

    @After
    public void tearDown() {
        profileDBHelper = null;
    }
}
