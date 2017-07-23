package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by zeeshan on 16/7/17.
 */
@Entity
public class Request extends BaseModel {
    @Exclude
    @Id(autoincrement = true)
    long id;
    @Index
    String familyId, userId;
    boolean approved;
    boolean blocked;
    long requestedOn;
    long updatedOn;
    String email, name, profilePic;

    @Override
    public String toString() {
        return "Request{" +
                "familyId='" + familyId + '\'' +
                ", approved=" + approved +
                ", blocked=" + blocked +
                ", requestedOn=" + requestedOn +
                ", updatedOn=" + updatedOn +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", profilePic='" + profilePic + '\'' +
                '}';
    }

    public String getFamilyId() {
        return this.familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }


    public Request() {
    }

    @Generated(hash = 1367759548)
    public Request(long id, String familyId, String userId, boolean approved,
            boolean blocked, long requestedOn, long updatedOn, String email,
            String name, String profilePic) {
        this.id = id;
        this.familyId = familyId;
        this.userId = userId;
        this.approved = approved;
        this.blocked = blocked;
        this.requestedOn = requestedOn;
        this.updatedOn = updatedOn;
        this.email = email;
        this.name = name;
        this.profilePic = profilePic;
    }

    public boolean getApproved() {
        return this.approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean getBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public long getRequestedOn() {
        return this.requestedOn;
    }

    public void setRequestedOn(long requestedOn) {
        this.requestedOn = requestedOn;
    }

    public long getUpdatedOn() {
        return this.updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return this.profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.familyId);
        dest.writeByte(this.approved ? (byte) 1 : (byte) 0);
        dest.writeByte(this.blocked ? (byte) 1 : (byte) 0);
        dest.writeLong(this.requestedOn);
        dest.writeLong(this.updatedOn);
        dest.writeString(this.userId);
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.profilePic);
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public long getId() {
        return this.id;
    }

    @Exclude
    public void setId(long id) {
        this.id = id;
    }

    protected Request(Parcel in) {
        this.familyId = in.readString();
        this.approved = in.readByte() != 0;
        this.blocked = in.readByte() != 0;
        this.requestedOn = in.readLong();
        this.updatedOn = in.readLong();
        this.userId = in.readString();
        this.email = in.readString();
        this.name = in.readString();
        this.profilePic = in.readString();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel source) {
            return new Request(source);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };
}
