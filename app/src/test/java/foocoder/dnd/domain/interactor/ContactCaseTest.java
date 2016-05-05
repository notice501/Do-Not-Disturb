package foocoder.dnd.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by xuechi.
 * Time: 2016 五月 03 18:08
 * Project: dnd
 */
public class ContactCaseTest {

    private ContactCaseTestClass contactCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        contactCase = new ContactCaseTestClass();
    }

    @Test
    public void testBuildContactCaseObservableReturnCorrectResult() {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        Subscription subscription = contactCase.execute(testSubscriber);

        assertThat(testSubscriber.getOnNextEvents().size(), is(0));
    }

    @Test
    public void testSubscriptionAfterExecutingCase() {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        Subscription subscription = contactCase.execute(testSubscriber);
        subscription.unsubscribe();

        assertThat(testSubscriber.isUnsubscribed(), is(true));
    }

    private static class ContactCaseTestClass extends ContactCase {

        @Override
        protected Observable buildContactCaseObservable() {
            return Observable.empty();
        }
    }
}
