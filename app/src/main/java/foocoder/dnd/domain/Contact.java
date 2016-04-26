package foocoder.dnd.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Contact implements Parcelable {

    @NonNull
    public String _id = "";

    public String name;

    public String phoneNo;

    public boolean selected;

    public int color;

    public Contact() {}

    public Contact(@NonNull String _id, String name, String phoneNo) {
        this._id = _id;
        this.name = name;
        this.phoneNo = phoneNo;
        this.selected = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.name);
        dest.writeString(this.phoneNo);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.color);
    }

    protected Contact(Parcel in) {
        this._id = in.readString();
        this.name = in.readString();
        this.phoneNo = in.readString();
        this.selected = in.readByte() != 0;
        this.color = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        return _id.equals(contact._id);

    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }
}
