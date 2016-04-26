package foocoder.dnd.domain.repository;

import java.util.List;

import foocoder.dnd.domain.Contact;
import rx.Observable;

public interface ContactRepository {

    Observable<List<Contact>> contacts();

    Observable<Contact> contact(int _id);

    Observable saveContacts(List<Contact> contacts);
}
