package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by zeeshan on 7/7/17.
 */
@Entity
public class Member extends BaseModel {
    @Id
    String id;
    String name, email;
    long wasPresentOn;
    boolean smsEnabled;
    String profilePic;

    public Member() {
    }

    @Generated(hash = 271757801)
    public Member(String id, String name, String email, long wasPresentOn, boolean smsEnabled,
            String profilePic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.wasPresentOn = wasPresentOn;
        this.smsEnabled = smsEnabled;
        this.profilePic = profilePic;
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

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean getSmsEnabled() {
        return this.smsEnabled;
    }

    public String getProfilePic() {
        return this.profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getWasPresentOn() {
        return wasPresentOn;
    }

    public void setWasPresentOn(long wasPresentOn) {
        this.wasPresentOn = wasPresentOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeLong(this.wasPresentOn);
        dest.writeByte(this.smsEnabled ? (byte) 1 : (byte) 0);
        dest.writeString(this.profilePic);
    }

    protected Member(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.wasPresentOn = in.readLong();
        this.smsEnabled = in.readByte() != 0;
        this.profilePic = in.readString();
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
