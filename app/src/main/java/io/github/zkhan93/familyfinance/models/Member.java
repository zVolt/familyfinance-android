package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zeeshan on 7/7/17.
 */
@Entity
public class Member implements Parcelable {
    @Id
    String id;
    String name, email;
    boolean canRecieveSms;

    @Generated(hash = 1704121277)
    public Member(String id, String name, String email, boolean canRecieveSms) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.canRecieveSms = canRecieveSms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCanRecieveSms() {
        return canRecieveSms;
    }

    public void setCanRecieveSms(boolean canRecieveSms) {
        this.canRecieveSms = canRecieveSms;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", canRecieveSms=" + canRecieveSms +
                '}';
    }

    public Member() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.id);
        dest.writeByte(this.canRecieveSms ? (byte) 1 : (byte) 0);
    }

    public boolean getCanRecieveSms() {
        return this.canRecieveSms;
    }

    @Keep
    protected Member(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.id = in.readString();
        this.canRecieveSms = in.readByte() != 0;
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}
