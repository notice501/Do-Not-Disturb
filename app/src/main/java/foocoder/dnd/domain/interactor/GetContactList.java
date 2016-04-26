package foocoder.dnd.domain.interactor;

import javax.inject.Inject;

import foocoder.dnd.domain.repository.ContactRepository;
import rx.Observable;

public class GetContactList extends ContactCase {

    private final ContactRepository contactRepository;

    @Inject
    public GetContactList(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    protected Observable buildContactCaseObservable() {
        return this.contactRepository.contacts().toList();
    }
}
