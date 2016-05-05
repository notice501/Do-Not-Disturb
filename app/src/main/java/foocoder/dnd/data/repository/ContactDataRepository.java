package foocoder.dnd.data.repository;

import android.app.Application;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import foocoder.dnd.R;
import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.repository.ContactRepository;
import foocoder.dnd.services.ProfileDBHelper;
import foocoder.dnd.utils.PinyinUtil;
import rx.Observable;

@Singleton
public class ContactDataRepository implements ContactRepository {

    private static final String[] PROJECTION = new String[]{
            Phone.CONTACT_ID,
            Phone.DISPLAY_NAME,
            Phone.NUMBER,
            Phone.MIMETYPE};
    private ProfileDBHelper helper;
    private Application context;
    private String[] colors;

    @Inject
    public ContactDataRepository(ProfileDBHelper helper, Application context) {
        this.helper = helper;
        this.context = context;
        this.colors = context.getResources().getStringArray(R.array.colors);
    }

    @Override
    public Observable<List<Contact>> contacts() {
        return Observable.create(subscriber -> {
            try {
                List<Contact> selected = new ArrayList<>();

                Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, PROJECTION,
                        Phone.IN_VISIBLE_GROUP + "=? " + "AND " + Phone.MIMETYPE + "=?",
                        new String[]{"1", Phone.CONTENT_ITEM_TYPE},
                        Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

                if (cursor == null) {
                    subscriber.onNext(selected);
                    subscriber.onCompleted();
                    return;
                }

                List<Contact> contactList = new ArrayList<>();
                selected = helper.getContacts();
                Contact contact = null;
                int index = 0;
                String lastName = "";
                int lastColor = 0x0;

                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    String name = cursor.getString(1);
                    String phoneNo = cursor.getString(2);

                    contact = new Contact(id, name, phoneNo);
//                    int color = colors.getColor(index % 5, context.getResources().getColor(R.color.switch_color));
                    int color = Color.parseColor(colors[index % 5]);
                    if (lastColor == 0) {
                        contact.color = color;
                    } else {
                        if (PinyinUtil.getPinYinInitChar(name).equals(PinyinUtil.getPinYinInitChar(lastName))) {
                            contact.color = lastColor;
                        } else {
                            index++;
                            contact.color = color;
                        }
                    }
                    if (selected.contains(contact)) {
                        contact.selected = true;
                        selected.set(selected.indexOf(contact), contact);
                    } else {
                        contactList.add(contact);
                    }

                    lastName = name;
                    lastColor = color;
                }

                subscriber.onNext(selected);

                cursor.close();
                contactList.addAll(0, selected);

                subscriber.onNext(contactList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Contact> contact(int _id) {
        throw new UnsupportedOperationException("目前没用");
    }

    @Override
    public Observable saveContacts(List<Contact> contacts) {

        return Observable.create(subscriber -> {
            try {
                helper.clearContacts();
                helper.saveContacts(contacts);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
