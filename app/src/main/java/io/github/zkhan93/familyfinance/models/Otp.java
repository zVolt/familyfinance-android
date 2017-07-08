package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by zeeshan on 7/7/17.
 */

public class Otp implements Parcelable {
    String id;
    String number, content;
    Member from;
    Date timestamp;

    public Otp(String id, String number, String content, Member from, Date timestamp) {
        this.id = id;
        this.number = number;
        this.content = content;
        this.from = from;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Member getFrom() {
        return from;
    }

    public void setFrom(Member from) {
        this.from = from;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Otp{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", content='" + content + '\'' +
                ", from=" + from +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.number);
        dest.writeString(this.content);
        dest.writeParcelable(this.from, flags);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
    }

    public Otp() {
    }

    protected Otp(Parcel in) {
        this.id = in.readString();
        this.number = in.readString();
        this.content = in.readString();
        this.from = in.readParcelable(Member.class.getClassLoader());
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
    }

    public static final Parcelable.Creator<Otp> CREATOR = new Parcelable.Creator<Otp>() {
        @Override
        public Otp createFromParcel(Parcel source) {
            return new Otp(source);
        }

        @Override
        public Otp[] newArray(int size) {
            return new Otp[size];
        }
    };
}
