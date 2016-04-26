package foocoder.dnd.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import foocoder.dnd.domain.Contact;
import foocoder.dnd.domain.Schedule;

@Singleton
public class ProfileDBHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_FROM = "start";
    public static final String COLUMN_TO = "end";
    public static final String COLUMN_MON = "monday";
    public static final String COLUMN_TUE = "tuesday";
    public static final String COLUMN_WED = "wednesday";
    public static final String COLUMN_THU = "thursday";
    public static final String COLUMN_FRI = "friday";
    public static final String COLUMN_SAT = "saturday";
    public static final String COLUMN_SUN = "sunday";
    public static final String COLUMN_CONTACT_NAME = "name";
    public static final String COLUMN_CONTACT_PHONENUM = "phoneNum";

    private static final String TABLE_TEMP_NUMBERS = "temp_numbers";
    private static final String TABLE_SCHEDULES = "schedules";
    private static final String TABLE_CONTACTS = "contacts";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "profiles.db";

    public ProfileDBHelper(Context context, String name, CursorFactory factory,
                           int version) {

        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " +
                TABLE_CONTACTS + "("
                + COLUMN_ID + " TEXT, "
                + COLUMN_CONTACT_NAME + " TEXT, "
                + COLUMN_CONTACT_PHONENUM + " TEXT" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_TEMP_NUMBERS = "CREATE TABLE " + TABLE_TEMP_NUMBERS + "(" + COLUMN_TIME + " INTEGER PRIMARY KEY, " + COLUMN_NUMBER + " INTEGER" + ")";
        db.execSQL(CREATE_TEMP_NUMBERS);

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

    public boolean saveNumber(String number, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_TIME, time);

        long result = db.insert(TABLE_TEMP_NUMBERS, null, values);
        return result > 0;
    }

    public int searchNumber(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_TEMP_NUMBERS, null, COLUMN_NUMBER + "=?", new String[]{number}, null, null, null);

        int result = c.getCount();
        c.close();
        return result;
    }

    public void scanTempNumbers() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.query(true, TABLE_TEMP_NUMBERS, new String[]{COLUMN_NUMBER}, null, null, null, null, null, null);
        while (c.moveToNext()) {
            String number = c.getString(0);
            Cursor cc = db.query(TABLE_TEMP_NUMBERS, new String[]{COLUMN_TIME}, COLUMN_NUMBER + "=?", new String[]{number}, null, null, COLUMN_TIME + " ASC");
            while (cc.moveToFirst()) {
                if (System.currentTimeMillis() - cc.getLong(0) > 3 * 60 * 1000) {
                    db.delete(TABLE_TEMP_NUMBERS, COLUMN_NUMBER + "=?", new String[]{number});
                    break;
                }
            }

            cc.close();
        }

        c.close();
    }

    public boolean saveSchedule(Schedule sch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, sch.getId());
        values.put(COLUMN_FROM, sch.getFrom());
        values.put(COLUMN_TO, sch.getTo());
        values.put(COLUMN_MON, sch.getChecked().get(0));
        values.put(COLUMN_TUE, sch.getChecked().get(1));
        values.put(COLUMN_WED, sch.getChecked().get(2));
        values.put(COLUMN_THU, sch.getChecked().get(3));
        values.put(COLUMN_FRI, sch.getChecked().get(4));
        values.put(COLUMN_SAT, sch.getChecked().get(5));
        values.put(COLUMN_SUN, sch.getChecked().get(6));

        long result = db.insert(TABLE_SCHEDULES, null, values);
        return result > 0;
    }

    public boolean updateSchedule(Schedule sch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, sch.getId());
        values.put(COLUMN_FROM, sch.getFrom());
        values.put(COLUMN_TO, sch.getTo());
        values.put(COLUMN_MON, sch.getChecked().get(0));
        values.put(COLUMN_TUE, sch.getChecked().get(1));
        values.put(COLUMN_WED, sch.getChecked().get(2));
        values.put(COLUMN_THU, sch.getChecked().get(3));
        values.put(COLUMN_FRI, sch.getChecked().get(4));
        values.put(COLUMN_SAT, sch.getChecked().get(5));
        values.put(COLUMN_SUN, sch.getChecked().get(6));

        int result = db.update(TABLE_SCHEDULES, values, COLUMN_ID + "=?", new String[]{sch.getId() + ""});
        return result > 0;
    }

    public boolean delSchedule(Schedule sch) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_SCHEDULES, COLUMN_ID + "=?", new String[]{sch.getId() + ""});
        return result > 0;
    }

    public List<Schedule> getScheduleList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Schedule> schs = new ArrayList<Schedule>(7);

        Cursor c = db.query(TABLE_SCHEDULES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            Schedule sch = new Schedule();
            List<Integer> checked = new ArrayList<Integer>();
            sch.setId(c.getInt(0));
            sch.setFrom(c.getString(1));
            sch.setTo(c.getString(2));

            checked.add(c.getInt(3));
            checked.add(c.getInt(4));
            checked.add(c.getInt(5));
            checked.add(c.getInt(6));
            checked.add(c.getInt(7));
            checked.add(c.getInt(8));
            checked.add(c.getInt(9));
            sch.setChecked(checked);

            schs.add(sch);
        }

        c.close();

        return schs;
    }

    public Schedule getSchedule(int _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Schedule sch = null;

        Cursor c = db.query(TABLE_SCHEDULES, null, COLUMN_ID + "=?", new String[]{_id + ""}, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                sch = new Schedule();
                List<Integer> checked = new ArrayList<Integer>();
                sch.setId(c.getInt(0));
                sch.setFrom(c.getString(1));
                sch.setTo(c.getString(2));

                checked.add(c.getInt(3));
                checked.add(c.getInt(4));
                checked.add(c.getInt(5));
                checked.add(c.getInt(6));
                checked.add(c.getInt(7));
                checked.add(c.getInt(8));
                checked.add(c.getInt(9));
                sch.setChecked(checked);
            }
        }

        if (c != null) {
            c.close();
        }

        return sch;
    }

    public List<Schedule> getScheduleByDay(String dayOfWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Schedule> schs = new ArrayList<Schedule>();

        Cursor c = db.query(TABLE_SCHEDULES, null, dayOfWeek + "=?", new String[]{"1"}, null, null, null);
        while (c.moveToNext()) {
            Schedule sch = new Schedule();
            List<Integer> checked = new ArrayList<Integer>();
            sch.setId(c.getInt(0));
            sch.setFrom(c.getString(1));
            sch.setTo(c.getString(2));

            checked.add(c.getInt(3));
            checked.add(c.getInt(4));
            checked.add(c.getInt(5));
            checked.add(c.getInt(6));
            checked.add(c.getInt(7));
            checked.add(c.getInt(8));
            checked.add(c.getInt(9));
            sch.setChecked(checked);

            schs.add(sch);
        }

        c.close();

        return schs;
    }

    public void saveContacts(List<Contact> contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Contact contact : contacts) {
                db.execSQL("insert into contacts(_id,name,phonenum) values(?,?,?)", new Object[]{contact._id, contact.name, contact.phoneNo});
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

    public void clearContacts() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CONTACTS, null, null);
    }
}
