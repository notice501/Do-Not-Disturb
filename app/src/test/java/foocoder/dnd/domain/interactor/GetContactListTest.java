package foocoder.dnd.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import foocoder.dnd.domain.repository.ContactRepository;
import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by xuechi.
 * Time: 2016 五月 03 19:09
 * Project: dnd
 */
public class GetContactListTest {

    private GetContactList getContactList;

    @Mock
    private ContactRepository contactRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        getContactList = new GetContactList(contactRepository);
    }

    @Test
    public void testGetContactListUseCaseObservableHappyCase() {
        when(contactRepository.contacts()).thenReturn(Observable.empty());
        getContactList.buildContactCaseObservable();

        verify(contactRepository).contacts();
        verifyNoMoreInteractions(contactRepository);
    }
}
