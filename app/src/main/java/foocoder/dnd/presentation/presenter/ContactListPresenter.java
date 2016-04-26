package foocoder.dnd.presentation.presenter;

import android.app.Application;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.interactor.ContactCase;
import foocoder.dnd.domain.interactor.DefaultSubscriber;
import foocoder.dnd.presentation.internal.di.PerActivity;
import foocoder.dnd.presentation.view.ContactListView;
import foocoder.dnd.utils.PinyinUtil;
import timber.log.Timber;

@PerActivity
public class ContactListPresenter extends Presenter<ContactListView> {

    private ContactCase getContactList;

    private ContactCase saveContacts;

    @Inject
    List<Contact> contacts;

    @Inject
    List<Contact> original;

    @Inject
    @Named("selected")
    List<Contact> selectedContacts;

    @Inject
    Application context;

    @Inject
    public ContactListPresenter(@Named("contactList") ContactCase getContactList, @Named("saveContacts") ContactCase saveContacts) {
        this.getContactList = getContactList;
        this.saveContacts = saveContacts;
    }

    @Override
    public void start() {
        if (getView() != null) {
            getView().startLoading();
        }
        loadContactList();
    }

    private void loadContactList() {
        addSubscriptionsForUnbinding(getContactList.execute(new ContactListSubscriber()));
    }

    public void saveContactList() {
        if (selectedContacts.size() >= 0) {
            addSubscriptionsForUnbinding(saveContacts.execute(new DefaultSubscriber<>()));
        }
    }

    public void onTextChanged(String name) throws Exception {
        if ("".equals(name)) {
            contacts.clear();
            contacts.addAll(original);
        } else {
            contacts.clear();
            for (Contact contact : original) {
                if (PinyinUtil.getPinYin(contact.name).contains(name)
                        || PinyinUtil.getPinYinHeadChar(contact.name).contains(name)
                        || contact.name.toLowerCase().contains(name)) {
                    contacts.add(contact);
                }
            }
        }
    }

    public void onContactCheckedChange(boolean checked, int position) {
        Contact selected = contacts.get(position);
        original.get(original.indexOf(selected)).selected = true;
        if (checked) {
            selectedContacts.add(selected);
        } else {
            selectedContacts.remove(selected);
        }
    }

    private final class ContactListSubscriber extends DefaultSubscriber<List<List<Contact>>> {

        @Override
        public void onNext(List<List<Contact>> contactList) {
            selectedContacts.addAll(contactList.get(0));
            contacts = contactList.get(1);
            original.addAll(contacts);
        }

        @Override
        public void onCompleted() {
            Timber.d("加载联系人完成 %d", contacts.size());
            if (getView() != null) {
                getView().showContacts(contacts);
                getView().stopLoading();
            }
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "加载联系人失败");
            if (getView() != null) {
                getView().stopLoading();
            }
        }
    }
}
