package foocoder.dnd.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Schedule implements Parcelable {

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {
            return new Schedule(source);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
    public int _id;
    public String from;
    public String to;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
    public List<Integer> days;
    public boolean del;

    public Schedule() {}

    public Schedule(String from, String to, List<Integer> checked) {
        this.from = from;
        this.to = to;
        this.days = checked;
        this.del = false;
    }

    protected Schedule(Parcel in) {
        this._id = in.readInt();
        this.from = in.readString();
        this.to = in.readString();
        this.startHour = in.readInt();
        this.startMinute = in.readInt();
        this.endHour = in.readInt();
        this.endMinute = in.readInt();
        this.days = new ArrayList<>();
        in.readList(this.days, Integer.class.getClassLoader());
        this.del = in.readByte() != 0;
    }

    public void copy(Schedule schedule) {
        this._id = schedule._id;
        this.from = schedule.from;
        this.to = schedule.to;
        this.days = schedule.days;
        this.endHour = schedule.startHour;
        this.startMinute = schedule.startMinute;
        this.endHour = schedule.endHour;
        this.endMinute = schedule.endMinute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeInt(this.startHour);
        dest.writeInt(this.startMinute);
        dest.writeInt(this.endHour);
        dest.writeInt(this.endMinute);
        dest.writeList(this.days);
        dest.writeByte(this.del ? (byte) 1 : (byte) 0);
    }
}
