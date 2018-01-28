package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Created by zeeshan on 12/23/17.
 */
@Entity
public class Credential implements Parcelable {
    @Id
    @Exclude
    String id;
    String username, password, description;

    String typeId;

    @Exclude
    @ToOne(joinProperty = "typeId")
    CredentialType type;

    long updateOn;

    String updatedByMemberId;
    @Exclude
    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;

    @Override
    public String toString() {
        return "Credential{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                ", typeId='" + typeId + '\'' +
                ", type=" + type +
                ", updateOn=" + updateOn +
                ", updatedByMemberId='" + updatedByMemberId + '\'' +
                ", updatedBy=" + updatedBy +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Credential() {
    }



    public long getUpdateOn() {
        return this.updateOn;
    }

    public void setUpdateOn(long updateOn) {
        this.updateOn = updateOn;
    }


    public String getUpdatedByMemberId() {
        return this.updatedByMemberId;
    }

    public void setUpdatedByMemberId(String updatedByMemberId) {
        this.updatedByMemberId = updatedByMemberId;
    }


    /** To-one relationship, resolved on first access. */
    @Exclude
    @Keep
    public Member getUpdatedBy() {
        String __key = this.updatedByMemberId;
        if (updatedBy__resolvedKey == null || updatedBy__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MemberDao targetDao = daoSession.getMemberDao();
            Member updatedByNew = targetDao.load(__key);
            synchronized (this) {
                updatedBy = updatedByNew;
                updatedBy__resolvedKey = __key;
            }
        }
        return updatedBy;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Exclude
    @Keep
    public void setUpdatedBy(Member updatedBy) {
        synchronized (this) {
            this.updatedBy = updatedBy;
            updatedByMemberId = updatedBy == null ? null : updatedBy.getId();
            updatedBy__resolvedKey = updatedByMemberId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1832334603)
    private transient CredentialDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.description);
        dest.writeString(this.typeId);
        dest.writeParcelable(this.type, flags);
        dest.writeLong(this.updateOn);
        dest.writeString(this.updatedByMemberId);
        dest.writeParcelable(this.updatedBy, flags);
    }

    public String getTypeId() {
        return this.typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /** To-one relationship, resolved on first access. */
    @Exclude
    @Keep
    public CredentialType getType() {
        String __key = this.typeId;
        if (type__resolvedKey == null || type__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CredentialTypeDao targetDao = daoSession.getCredentialTypeDao();
            CredentialType typeNew = targetDao.load(__key);
            synchronized (this) {
                type = typeNew;
                type__resolvedKey = __key;
            }
        }
        return type;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Exclude
    @Keep
    public void setType(CredentialType type) {
        synchronized (this) {
            this.type = type;
            typeId = type == null ? null : type.getId();
            type__resolvedKey = typeId;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1895121706)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCredentialDao() : null;
    }

    protected Credential(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.description = in.readString();
        this.typeId = in.readString();
        this.type = in.readParcelable(CredentialType.class.getClassLoader());
        this.updateOn = in.readLong();
        this.updatedByMemberId = in.readString();
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
    }

    @Generated(hash = 244899742)
    public Credential(String id, String username, String password, String description,
            String typeId, long updateOn, String updatedByMemberId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.description = description;
        this.typeId = typeId;
        this.updateOn = updateOn;
        this.updatedByMemberId = updatedByMemberId;
    }

    public static final Creator<Credential> CREATOR = new Creator<Credential>() {
        @Override
        public Credential createFromParcel(Parcel source) {
            return new Credential(source);
        }

        @Override
        public Credential[] newArray(int size) {
            return new Credential[size];
        }
    };
    @Generated(hash = 2140138224)
    private transient String type__resolvedKey;
}
