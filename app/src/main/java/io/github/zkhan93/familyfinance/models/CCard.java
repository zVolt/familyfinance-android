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
    String id;
    String number;
    String name, bank, cardholder, userid, password;
    Date updatedOn;
    int paymentDay, billingDay;
    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;
    float maxLimit, consumedLimit, remainingLimit;

    private String updatedByMemberId;

    Date localModifiedOn;

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

    public String getFormattedNumber(char delimiter) {
        StringBuilder strb = new StringBuilder(19);
        int i = 1;
        for (char c : number.toCharArray()) {
            strb.append(c);
            if (i % 4 == 0)
                strb.append(delimiter);
            i++;
        }
        return strb.toString();
    }

    @Override
    public String toString() {
        return "CCard{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", bank='" + bank + '\'' +
                ", cardholder='" + cardholder + '\'' +
                ", userid='" + userid + '\'' +
                ", password='" + password + '\'' +
                ", updatedOn=" + updatedOn +
                ", paymentDay=" + paymentDay +
                ", billingDay=" + billingDay +
                ", updatedBy=" + updatedBy +
                ", maxLimit=" + maxLimit +
                ", consumedLimit=" + consumedLimit +
                ", remainingLimit=" + remainingLimit +
                ", updatedByMemberId='" + updatedByMemberId + '\'' +
                ", localModifiedOn=" + localModifiedOn +
                '}';
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

    /**
     * called by internal mechanisms, do not call yourself.
     */
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

    public CCard() {
    }

    @Keep
    public CCard(String name, String number, String bank, String cardholder, Date updatedOn,
                 int paymentDay, int billingDay, float maxLimit, float consumedLimit,
                 float remainingLimit, String updatedByMemberId) {
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.cardholder = cardholder;
        this.updatedOn = updatedOn;
        this.paymentDay = paymentDay;
        this.billingDay = billingDay;
        this.maxLimit = maxLimit;
        this.consumedLimit = consumedLimit;
        this.remainingLimit = remainingLimit;
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
    @Generated(hash = 1513197518)
    private transient CCardDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;

    public int getBillingDay() {
        return this.billingDay;
    }

    public void setBillingDay(int billingDay) {
        this.billingDay = billingDay;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Generated(hash = 2046377622)
    public CCard(String id, String number, String name, String bank, String cardholder, String userid,
            String password, Date updatedOn, int paymentDay, int billingDay, float maxLimit,
            float consumedLimit, float remainingLimit, String updatedByMemberId, Date localModifiedOn) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.cardholder = cardholder;
        this.userid = userid;
        this.password = password;
        this.updatedOn = updatedOn;
        this.paymentDay = paymentDay;
        this.billingDay = billingDay;
        this.maxLimit = maxLimit;
        this.consumedLimit = consumedLimit;
        this.remainingLimit = remainingLimit;
        this.updatedByMemberId = updatedByMemberId;
        this.localModifiedOn = localModifiedOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.number);
        dest.writeString(this.name);
        dest.writeString(this.bank);
        dest.writeString(this.cardholder);
        dest.writeString(this.userid);
        dest.writeString(this.password);
        dest.writeLong(this.updatedOn != null ? this.updatedOn.getTime() : -1);
        dest.writeInt(this.paymentDay);
        dest.writeInt(this.billingDay);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeFloat(this.maxLimit);
        dest.writeFloat(this.consumedLimit);
        dest.writeFloat(this.remainingLimit);
        dest.writeString(this.updatedByMemberId);
        dest.writeLong(this.localModifiedOn != null ? this.localModifiedOn.getTime() : -1);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLocalModifiedOn() {
        return this.localModifiedOn;
    }

    public void setLocalModifiedOn(Date localModifiedOn) {
        this.localModifiedOn = localModifiedOn;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1480820516)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCCardDao() : null;
    }

    protected CCard(Parcel in) {
        this.id = in.readString();
        this.number = in.readString();
        this.name = in.readString();
        this.bank = in.readString();
        this.cardholder = in.readString();
        this.userid = in.readString();
        this.password = in.readString();
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.paymentDay = in.readInt();
        this.billingDay = in.readInt();
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.maxLimit = in.readFloat();
        this.consumedLimit = in.readFloat();
        this.remainingLimit = in.readFloat();
        this.updatedByMemberId = in.readString();
        long tmpLocalModifiedOn = in.readLong();
        this.localModifiedOn = tmpLocalModifiedOn == -1 ? null : new Date(tmpLocalModifiedOn);
    }

    public static final Creator<CCard> CREATOR = new Creator<CCard>() {
        @Override
        public CCard createFromParcel(Parcel source) {
            return new CCard(source);
        }

        @Override
        public CCard[] newArray(int size) {
            return new CCard[size];
        }
    };
}
