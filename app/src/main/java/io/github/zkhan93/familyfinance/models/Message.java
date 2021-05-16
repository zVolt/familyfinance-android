package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Objects;

/**
 * Created by zeeshan on 11/13/17.
 */

@Entity
public class Message extends BaseModel {
    @Id
    @Exclude
    String id;
    String content;
    @Exclude
    @ToOne(joinProperty = "senderId")
    Member sender;
    String senderId;
    long timestamp;
    @Exclude
    String familyId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getFamilyId() {
        return familyId;
    }

    @Exclude
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Exclude
    @Keep
    public Member getSender() {
        String __key = this.senderId;
        if (sender__resolvedKey == null || !Objects.equals(sender__resolvedKey, __key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MemberDao targetDao = daoSession.getMemberDao();
            Member senderNew = targetDao.load(__key);
            synchronized (this) {
                sender = senderNew;
                sender__resolvedKey = __key;
            }
        }
        return sender;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Exclude
    @Keep
    public void setSender(Member sender) {
        synchronized (this) {
            this.sender = sender;
            senderId = sender == null ? null : sender.getId();
            sender__resolvedKey = senderId;
        }
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Message() {
    }

    @Override
    @Exclude
    public int describeContents() {
        return 0;
    }

    @Override
    @Exclude
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeParcelable(this.sender, flags);
        dest.writeString(this.senderId);
        dest.writeLong(this.timestamp);
        dest.writeString(this.familyId);
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
    @Generated(hash = 747015224)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMessageDao() : null;
    }

    protected Message(Parcel in) {
        this.id = in.readString();
        this.content = in.readString();
        this.sender = in.readParcelable(Member.class.getClassLoader());
        this.senderId = in.readString();
        this.timestamp = in.readLong();
        this.familyId = in.readString();
    }

    @Generated(hash = 816960819)
    public Message(String id, String content, String senderId, long timestamp,
                   String familyId) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.familyId = familyId;
    }

    @Exclude
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
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
    @Generated(hash = 859287859)
    private transient MessageDao myDao;
    @Generated(hash = 711275118)
    private transient String sender__resolvedKey;
}
