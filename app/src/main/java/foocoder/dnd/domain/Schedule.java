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
    public List<Integer> checked;
    public boolean del;
    public boolean running;

    public Schedule() {}

    public Schedule(String from, String to, List<Integer> checked) {
        this.from = from;
        this.to = to;
        this.checked = checked;
        this.del = false;
        this.running = false;
    }

    protected Schedule(Parcel in) {
        this._id = in.readInt();
        this.from = in.readString();
        this.to = in.readString();
        this.checked = new ArrayList<>();
        in.readList(this.checked, Integer.class.getClassLoader());
        this.del = in.readByte() != 0;
        this.running = in.readByte() != 0;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Integer> getChecked() {
        return checked;
    }

    public void setChecked(List<Integer> checked) {
        this.checked = checked;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public int getId() {
        return this._id;
    }

    public void setId(int _id) {
        this._id = _id;
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
        dest.writeList(this.checked);
        dest.writeByte(del ? (byte) 1 : (byte) 0);
        dest.writeByte(running ? (byte) 1 : (byte) 0);
    }

    public void copy(Schedule schedule) {
        this._id = schedule._id;
        this.from = schedule.from;
        this.to = schedule.to;
        this.checked = schedule.checked;
    }
}
