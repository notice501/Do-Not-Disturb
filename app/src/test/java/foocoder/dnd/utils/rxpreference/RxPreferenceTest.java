package foocoder.dnd.utils.rxpreference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import foocoder.dnd.BuildConfig;
import foocoder.dnd.presentation.App;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by xuechi.
 * Time: 2016 五月 05 15:54
 * Project: dnd
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, application = App.class, sdk = 21)
public class RxPreferenceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getInstanceNotNull() {
        RxPreference rxPreference = getRxPreference();
        assertNotNull(rxPreference);
    }

    @Test
    public void sameNameShouldGetSameInstance() {
        RxPreference rxPreference = getRxPreference();
        assertNotNull(rxPreference);

        RxPreference rxPreference1 = getRxPreference();
        assertNotNull(rxPreference1);
        assertEquals(rxPreference, rxPreference1);

        RxPreference rxPreference2 = getRxPreference("test1");
        assertNotNull(rxPreference2);
        assertNotEquals(rxPreference, rxPreference2);
    }

    @Test
    public void getOneStringValue() {
        RxPreference rxPreference = getRxPreference();
        String value = rxPreference.getValue("test1", "nothing");
        assertEquals(value, "nothing");

        String expected = "Leon";
        Observable.<String>create(subscriber -> {
            subscriber.onNext(expected);
        }).subscribe(rxPreference.getAction("agent"));

        String actual = rxPreference.getValue("agent", "none");
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReceivePreferenceChange() {
        String expected = "Chris";
        RxPreference rxPreference = getRxPreference();
        rxPreference.getChangeObservable("name")
                .subscribe(s -> {
                    assertNotNull(s);
                    assertEquals(expected, s);
                });

        rxPreference.putValue("name", expected);
    }

    private RxPreference getRxPreference() {
        return getRxPreference("test");
    }

    private RxPreference getRxPreference(String name) {
        return RxPreference.getInstance(name, RuntimeEnvironment.application);
    }
}