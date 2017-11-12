package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zeeshan on 10/29/17.
 */

public class Email implements Parcelable {
    private String subject;
    private String from;
    private String to;
    private long timestamp;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subject);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeLong(this.timestamp);
    }

    public Email() {
    }

    protected Email(Parcel in) {
        this.subject = in.readString();
        this.from = in.readString();
        this.to = in.readString();
        this.timestamp = in.readLong();
    }

    @Override
    public String toString() {
        return "Email{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel source) {
            return new Email(source);
        }

        @Override
        public Email[] newArray(int size) {
            return new Email[size];
        }
    };
}
