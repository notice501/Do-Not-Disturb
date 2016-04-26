package foocoder.dnd.domain.interactor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.repository.ContactRepository;
import rx.Observable;

/**
 * Created by xuechi.
 * Time: 2016 四月 24 09:13
 * Project: dnd
 */
public class SaveContacts extends ContactCase {

    private final ContactRepository contactRepository;

    private List<Contact> contacts;

    @Inject
    public SaveContacts(ContactRepository contactRepository, @Named("selected") List<Contact> contacts) {
        this.contacts = contacts;
        this.contactRepository = contactRepository;
    }

    @Override
    protected Observable buildContactCaseObservable() {
        return this.contactRepository.saveContacts(contacts);
    }
}
