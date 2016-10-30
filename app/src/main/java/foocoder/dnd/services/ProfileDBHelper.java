package foocoder.dnd.services;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.Schedule;
import foocoder.dnd.presentation.App;

public class ProfileDBHelper extends SQLiteOpenHelper {

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FROM = "start";
    private static final String COLUMN_TO = "end";
    private static final String COLUMN_MON = "monday";
    private static final String COLUMN_TUE = "tuesday";
    private static final String COLUMN_WED = "wednesday";
    private static final String COLUMN_THU = "thursday";
    private static final String COLUMN_FRI = "friday";
    private static final String COLUMN_SAT = "saturday";
    private static final String COLUMN_SUN = "sunday";
    private static final String COLUMN_CONTACT_NAME = "name";
    private static final String COLUMN_CONTACT_PHONENUM = "phoneNum";

    private static final String TABLE_SCHEDULES = "schedules";
    private static final String TABLE_CONTACTS = "contacts";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "profiles.db";

    private ContentResolver contentResolver;

    public ProfileDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contentResolver = context.getContentResolver();
    }

    private static String fixNumber(String number) {
        String fixedNumber = PhoneNumberUtils.normalizeNumber(number);
        if (number.startsWith("+86")) {
            fixedNumber = number.replace("+86", "");
        }

        if (number.startsWith("86") && number.length() > 9) {
            fixedNumber = number.replaceFirst("^86", "");
        }

        if (number.startsWith("+400")) {
            fixedNumber = number.replace("+", "");
        }

        if (fixedNumber.startsWith("12583")) {
            fixedNumber = fixedNumber.replaceFirst("^12583.", "");
        }

        if (fixedNumber.startsWith("1259023")) {
            fixedNumber = number.replaceFirst("^1259023", "");
        }

        if (fixedNumber.startsWith("1183348")) {
            fixedNumber = number.replaceFirst("^1183348", "");
        }

        return fixedNumber;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " +
                TABLE_CONTACTS + "("
                + COLUMN_ID + " TEXT, "
                + COLUMN_CONTACT_NAME + " TEXT, "
                + COLUMN_CONTACT_PHONENUM + " TEXT" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_TABLE_SCHEDULE = "CREATE TABLE " + TABLE_SCHEDULES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_FROM + " INTEGER, "
                + COLUMN_TO + " INTEGER, "
                + COLUMN_MON + " INTEGER, "
                + COLUMN_TUE + " INTEGER, "
                + COLUMN_WED + " INTEGER, "
                + COLUMN_THU + " INTEGER, "
                + COLUMN_FRI + " INTEGER, "
                + COLUMN_SAT + " INTEGER, "
                + COLUMN_SUN + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE_SCHEDULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public boolean searchNumber(String number, boolean isRepeated) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean found;
        try (Cursor c = db.query(TABLE_CONTACTS, null, COLUMN_CONTACT_PHONENUM + "=?",
                new String[]{fixNumber(number)}, null, null, null)) {
            if (!(found = c.getCount() > 0) && isRepeated) {
                if (App.getContext().checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    long cutOffTime = System.currentTimeMillis() - 3 * 60 * 1000L;
                    try (Cursor logC = contentResolver.query(
                            CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER},
                            CallLog.Calls.DATE + ">= ? AND " + CallLog.Calls.NUMBER + " = ? ", new String[]{cutOffTime + "", number},
                            CallLog.Calls.DEFAULT_SORT_ORDER)) {
                        found = logC != null && logC.moveToNext();
                    }
                }
            }
        }

        return found;
    }

    public boolean saveSchedule(Schedule schedule) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, schedule._id);
        values.put(COLUMN_FROM, schedule.from);
        values.put(COLUMN_TO, schedule.to);
        values.put(COLUMN_MON, schedule.checked.get(0));
        values.put(COLUMN_TUE, schedule.checked.get(1));
        values.put(COLUMN_WED, schedule.checked.get(2));
        values.put(COLUMN_THU, schedule.checked.get(3));
        values.put(COLUMN_FRI, schedule.checked.get(4));
        values.put(COLUMN_SAT, schedule.checked.get(5));
        values.put(COLUMN_SUN, schedule.checked.get(6));

        long result = db.insert(TABLE_SCHEDULES, null, values);
        return result >= 0;
    }

    public boolean updateSchedule(Schedule sch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, sch._id);
        values.put(COLUMN_FROM, sch.from);
        values.put(COLUMN_TO, sch.to);
        values.put(COLUMN_MON, sch.checked.get(0));
        values.put(COLUMN_TUE, sch.checked.get(1));
        values.put(COLUMN_WED, sch.checked.get(2));
        values.put(COLUMN_THU, sch.checked.get(3));
        values.put(COLUMN_FRI, sch.checked.get(4));
        values.put(COLUMN_SAT, sch.checked.get(5));
        values.put(COLUMN_SUN, sch.checked.get(6));

        int result = db.update(TABLE_SCHEDULES, values, COLUMN_ID + "=?", new String[]{sch._id + ""});
        return result > 0;
    }

    public boolean delSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_SCHEDULES, COLUMN_ID + "=?", new String[]{String.valueOf(schedule._id)});
        return result > 0;
    }

    public List<Schedule> getScheduleList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Schedule> schedules = new ArrayList<>(5);

        Cursor c = db.query(TABLE_SCHEDULES, null, null, null, null, null, null, null);
        if (c == null) {
            return schedules;
        }

        while (c.moveToNext()) {
            Schedule schedule = new Schedule();
            List<Integer> checked = new ArrayList<>(7);
            schedule._id = c.getInt(0);
            schedule.from = c.getString(1);
            schedule.to = c.getString(2);

            checked.add(c.getInt(3));
            checked.add(c.getInt(4));
            checked.add(c.getInt(5));
            checked.add(c.getInt(6));
            checked.add(c.getInt(7));
            checked.add(c.getInt(8));
            checked.add(c.getInt(9));
            schedule.checked = checked;

            schedules.add(schedule);
        }

        c.close();

        return schedules;
    }

    public Schedule getSchedule(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Schedule schedule = null;

        Cursor c = db.query(TABLE_SCHEDULES, null, COLUMN_ID + "=?", new String[]{_id + ""}, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                schedule = new Schedule();
                List<Integer> checked = new ArrayList<>(7);
                schedule._id = c.getInt(0);
                schedule.from = c.getString(1);
                schedule.to = c.getString(2);

                checked.add(c.getInt(3));
                checked.add(c.getInt(4));
                checked.add(c.getInt(5));
                checked.add(c.getInt(6));
                checked.add(c.getInt(7));
                checked.add(c.getInt(8));
                checked.add(c.getInt(9));
                schedule.checked = checked;
            }
        }

        if (c != null) {
            c.close();
        }

        return schedule;
    }

    public void saveContacts(List<Contact> contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Contact contact : contacts) {
                db.execSQL("insert into contacts(_id,name,phonenum) values(?,?,?)", new Object[]{
                        contact._id, contact.name, fixNumber(contact.phoneNo)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<Contact> getContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Contact> contacts = new ArrayList<>();

        Cursor c = db.query(TABLE_CONTACTS, new String[]{COLUMN_ID, COLUMN_CONTACT_NAME, COLUMN_CONTACT_PHONENUM}, null, null, null, null, null);
        if (c == null) {
            return contacts;
        }

        while (c.moveToNext()) {
            Contact contact = new Contact();
            contact._id = c.getString(0);
            contact.name = c.getString(1);
            contact.phoneNo = c.getString(2);
            contacts.add(contact);
        }

        c.close();

        return contacts;
    }

    public boolean clearContacts() {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_CONTACTS, null, null);
        return result > 0;
    }
}
