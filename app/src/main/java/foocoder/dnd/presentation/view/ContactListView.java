package foocoder.dnd.presentation.view;

import android.support.annotation.UiThread;

import java.util.List;

import foocoder.dnd.domain.Contact;

public interface ContactListView {

    @UiThread
    void showContacts(List<Contact> contactList);

    @UiThread
    void startLoading();

    @UiThread
    void stopLoading();
}
