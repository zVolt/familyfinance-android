package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zeeshan on 1/25/18.
 */

@Entity
public class CredentialType implements Parcelable {
    @Id
    @Exclude
    String id;
    String name;
    String iconUrl;

    @Exclude
    public boolean expanded;

    @Generated(hash = 627700874)
    public CredentialType(String id, String name, String iconUrl,
            boolean expanded) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.expanded = expanded;
    }
    @Generated(hash = 1687604242)
    public CredentialType() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIconUrl() {
        return this.iconUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.iconUrl);
    }
    public boolean getExpanded() {
        return this.expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    protected CredentialType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.iconUrl = in.readString();
    }

    public static final Parcelable.Creator<CredentialType> CREATOR = new Parcelable
            .Creator<CredentialType>() {
        @Override
        public CredentialType createFromParcel(Parcel source) {
            return new CredentialType(source);
        }

        @Override
        public CredentialType[] newArray(int size) {
            return new CredentialType[size];
        }
    };
}
