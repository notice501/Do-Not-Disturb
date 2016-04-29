package foocoder.dnd.presentation.internal.di.modules;

import android.app.Application;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import foocoder.dnd.R;
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

    @Provides
    @PerActivity
    TypedArray provideColorArray(Application context) {
        return context.getResources().obtainTypedArray(R.array.colors);
    }

    @Provides
    @PerActivity
    AtomicInteger provideCounter() {
        return new AtomicInteger(0);
    }
}
