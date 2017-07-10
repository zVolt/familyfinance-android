package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by zeeshan on 7/7/17.
 */
@Entity
public class CCard implements Parcelable {
    @Id
    String number;
    String name, bank, cardholder;
    Date updatedOn;
    int paymentDay;
    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;
    float maxLimit, consumedLimit, remainingLimit;

    private String updatedByMemberId;

    @Keep
    public CCard(String name, String number, String bank, String cardholder, Date updatedOn, int
            paymentDay, Member updatedBy, float maxLimit, float consumedLimit, float
                         remainingLimit) {
        this.name = name;
        this.number = number;
        this.bank = bank;
        this.cardholder = cardholder;
        this.updatedOn = updatedOn;
        this.paymentDay = paymentDay;
        this.updatedBy = updatedBy;
        this.maxLimit = maxLimit;
        this.consumedLimit = consumedLimit;
        this.remainingLimit = remainingLimit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public int getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(int paymentDay) {
        this.paymentDay = paymentDay;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }




    public float getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(float maxLimit) {
        this.maxLimit = maxLimit;
    }

    public float getConsumedLimit() {
        return consumedLimit;
    }

    public void setConsumedLimit(float consumedLimit) {
        this.consumedLimit = consumedLimit;
    }

    public float getRemainingLimit() {
        return remainingLimit;
    }

    public void setRemainingLimit(float remainingLimit) {
        this.remainingLimit = remainingLimit;
    }

    @Override
    public String toString() {
        return "CCard{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", bank='" + bank + '\'' +
                ", cardholder='" + cardholder + '\'' +
                ", paymentDay=" + paymentDay +
                ", updatedOn=" + updatedOn +
                ", updatedBy=" + updatedBy +
                ", maxLimit=" + maxLimit +
                ", consumedLimit=" + consumedLimit +
                ", remainingLimit=" + remainingLimit +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeString(this.bank);
        dest.writeString(this.cardholder);
        dest.writeInt(this.paymentDay);
        dest.writeLong(this.updatedOn != null ? this.updatedOn.getTime() : -1);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeFloat(this.maxLimit);
        dest.writeFloat(this.consumedLimit);
        dest.writeFloat(this.remainingLimit);
    }

    public String getUpdatedByMemberId() {
        return this.updatedByMemberId;
    }

    public void setUpdatedByMemberId(String updatedByMemberId) {
        this.updatedByMemberId = updatedByMemberId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 142537439)
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
    @Generated(hash = 2009088111)
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1480820516)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCCardDao() : null;
    }

    public CCard() {
    }

    protected CCard(Parcel in) {
        this.name = in.readString();
        this.number = in.readString();
        this.bank = in.readString();
        this.cardholder = in.readString();
        this.paymentDay = in.readInt();
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.maxLimit = in.readFloat();
        this.consumedLimit = in.readFloat();
        this.remainingLimit = in.readFloat();
    }

    @Generated(hash = 487725028)
    public CCard(String number, String name, String bank, String cardholder, Date updatedOn,
            int paymentDay, float maxLimit, float consumedLimit, float remainingLimit,
            String updatedByMemberId) {
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.cardholder = cardholder;
        this.updatedOn = updatedOn;
        this.paymentDay = paymentDay;
        this.maxLimit = maxLimit;
        this.consumedLimit = consumedLimit;
        this.remainingLimit = remainingLimit;
        this.updatedByMemberId = updatedByMemberId;
    }

    public static final Parcelable.Creator<CCard> CREATOR = new Parcelable.Creator<CCard>() {
        @Override
        public CCard createFromParcel(Parcel source) {
            return new CCard(source);
        }

        @Override
        public CCard[] newArray(int size) {
            return new CCard[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1513197518)
    private transient CCardDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;
}
