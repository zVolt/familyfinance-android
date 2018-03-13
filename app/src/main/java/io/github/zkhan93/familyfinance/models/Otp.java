package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Comparator;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by zeeshan on 7/7/17.
 */
@IgnoreExtraProperties
public class Otp extends BaseModel {
    @Id
    String id;
    String number, content;
    long timestamp;

    @Exclude
    Member from;
    String fromMemberId;

    @Exclude
    Member claimedby;
    String claimedByMemberId;

    public Otp() {
    }

    public Otp(String id, String number, String content, long timestamp, String
            fromMemberId, String claimedByMemberId) {
        this.id = id;
        this.number = number;
        this.content = content;
        this.timestamp = timestamp;
        this.fromMemberId = fromMemberId;
        this.claimedByMemberId = claimedByMemberId;
    }
    @Exclude
    public Member getFrom() {
        return from;
    }
    @Exclude
    public void setFrom(Member from) {
        this.from = from;
    }
    @Exclude
    public Member getClaimedby() {
        return claimedby;
    }
    @Exclude
    public void setClaimedby(Member claimedby) {
        this.claimedby = claimedby;
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


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getClaimedByMemberId() {
        return claimedByMemberId;
    }

    public void setClaimedByMemberId(String claimedByMemberId) {
        this.claimedByMemberId = claimedByMemberId;
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
        dest.writeLong(this.timestamp);
    }

    public String getFromMemberId() {
        return this.fromMemberId;
    }

    public void setFromMemberId(String fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    protected Otp(Parcel in) {
        this.id = in.readString();
        this.number = in.readString();
        this.content = in.readString();
        this.from = in.readParcelable(Member.class.getClassLoader());
        this.timestamp = in.readLong();
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
    public static final Comparator<Otp> BY_TIMESTAMP = new Comparator<Otp>() {
        @Override
        public int compare(Otp o1, Otp o2) {
            return Long.compare(o2.getTimestamp(), o1.getTimestamp());
        }
    };
}
