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

import java.util.Date;

/**
 * Created by zeeshan on 28/7/17.
 */
@Entity
public class AddonCard extends BaseModel implements Parcelable {
    @Id
    String number;
    String phoneNumber, name, mainCardNumber;
    long expiresOn;
    int cvv;
    long updatedOn;

    @ToOne(joinProperty = "updatedByMemberId")
    @Exclude
    Member updatedBy;

    private String updatedByMemberId;

    public AddonCard() {
    }

    @Exclude
    public String getReadableContent() {
        StringBuilder strb = new StringBuilder();
        strb.append("Card Holder: ").append(name).append("\n")
                .append("Number: ").append(number).append("\n")
                .append("CVV: ").append(cvv).append("\n")
                .append("Expire On (MM/YY): ").append(CCard.EXPIRE_ON.format(new Date(expiresOn)))
                .append("\n");
        return strb.toString();
    }

    @Override
    public String toString() {
        return "AddonCard{" +
                "number='" + number + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", mainCardNumber='" + mainCardNumber + '\'' +
                ", expiresOn=" + expiresOn +
                ", cvv=" + cvv +
                ", updatedOn=" + updatedOn +
                ", updatedBy=" + updatedBy +
                ", updatedByMemberId='" + updatedByMemberId + '\'' +
                '}';
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExpiresOn() {
        return this.expiresOn;
    }

    public void setExpiresOn(long expiresOn) {
        this.expiresOn = expiresOn;
    }

    public int getCvv() {
        return this.cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public long getUpdatedOn() {
        return this.updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedByMemberId() {
        return this.updatedByMemberId;
    }

    public void setUpdatedByMemberId(String updatedByMemberId) {
        this.updatedByMemberId = updatedByMemberId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
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

    /**
     * called by internal mechanisms, do not call yourself.
     */
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

    @Generated(hash = 1678265474)
    public AddonCard(String number, String phoneNumber, String name, String mainCardNumber,
                     long expiresOn, int cvv, long updatedOn, String updatedByMemberId) {
        this.number = number;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.mainCardNumber = mainCardNumber;
        this.expiresOn = expiresOn;
        this.cvv = cvv;
        this.updatedOn = updatedOn;
        this.updatedByMemberId = updatedByMemberId;
    }

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1343022914)
    private transient AddonCardDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.name);
        dest.writeLong(this.expiresOn);
        dest.writeInt(this.cvv);
        dest.writeLong(this.updatedOn);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeString(this.updatedByMemberId);
    }

    public String getMainCardNumber() {
        return this.mainCardNumber;
    }

    public void setMainCardNumber(String mainCardNumber) {
        this.mainCardNumber = mainCardNumber;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 104181889)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAddonCardDao() : null;
    }

    protected AddonCard(Parcel in) {
        this.number = in.readString();
        this.phoneNumber = in.readString();
        this.name = in.readString();
        this.expiresOn = in.readLong();
        this.cvv = in.readInt();
        this.updatedOn = in.readLong();
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.updatedByMemberId = in.readString();
    }

    public static final Creator<AddonCard> CREATOR = new Creator<AddonCard>() {
        @Override
        public AddonCard createFromParcel(Parcel source) {
            return new AddonCard(source);
        }

        @Override
        public AddonCard[] newArray(int size) {
            return new AddonCard[size];
        }
    };
}
