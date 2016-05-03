package foocoder.dnd.presentation.internal.di.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.interactor.ContactCase;
import foocoder.dnd.domain.interactor.GetContactList;
import foocoder.dnd.domain.interactor.SaveContacts;
import foocoder.dnd.presentation.internal.di.PerActivity;

@Module
public class ContactModule {

    @Named("contactList")
    @Provides
    @PerActivity
    ContactCase provideGetContactListCase(GetContactList getContactList) {
        return getContactList;
    }

    @Named("saveContacts")
    @Provides
    @PerActivity
    ContactCase provideSaveContactsCase(SaveContacts saveContactsCase) {
        return saveContactsCase;
    }

    @Provides
    List<Contact> provideContacts() {
        return new CopyOnWriteArrayList<>();
    }

    @Named("selected")
    @Provides
    @PerActivity
    List<Contact> provideSelectedContacts() {
        return new ArrayList<>();
    }
}
