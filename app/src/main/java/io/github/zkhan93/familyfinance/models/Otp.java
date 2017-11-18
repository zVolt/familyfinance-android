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
@Entity
public class Otp extends BaseModel{
    @Id
    String id;
    String number, content;
    @Exclude
    @ToOne(joinProperty = "fromMemberId")
    Member from;
    long timestamp;
    String fromMemberId;

    @Keep
    public Otp(String id, String number, String content, Member from, long timestamp) {
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


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
        dest.writeLong(this.timestamp);
    }

    public String getFromMemberId() {
        return this.fromMemberId;
    }

    public void setFromMemberId(String fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Exclude
    @Keep
    public Member getFrom() {
        String __key = this.fromMemberId;
        if (from__resolvedKey == null || from__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MemberDao targetDao = daoSession.getMemberDao();
            Member fromNew = targetDao.load(__key);
            synchronized (this) {
                from = fromNew;
                from__resolvedKey = __key;
            }
        }
        return from;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Exclude
    @Keep
    public void setFrom(Member from) {
        synchronized (this) {
            this.from = from;
            fromMemberId = from == null ? null : from.getId();
            from__resolvedKey = fromMemberId;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1776113371)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getOtpDao() : null;
    }

    public Otp() {
    }

    protected Otp(Parcel in) {
        this.id = in.readString();
        this.number = in.readString();
        this.content = in.readString();
        this.from = in.readParcelable(Member.class.getClassLoader());
        this.timestamp = in.readLong();
    }

    @Generated(hash = 792587313)
    public Otp(String id, String number, String content, long timestamp,
               String fromMemberId) {
        this.id = id;
        this.number = number;
        this.content = content;
        this.timestamp = timestamp;
        this.fromMemberId = fromMemberId;
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
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 567553970)
    private transient OtpDao myDao;
    @Generated(hash = 126835137)
    private transient String from__resolvedKey;
}
