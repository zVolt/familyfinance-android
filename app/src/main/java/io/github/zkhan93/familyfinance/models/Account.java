package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;

/**
 * Created by zeeshan on 7/7/17.
 */
@Entity
public class Account implements Parcelable {
    @Id
    String accountNumber;
    String accountHolder, bank, ifsc, userid,password;
    float balance;
    Date updatedOn;
    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;

    private String updatedByMemberId;

    @Keep
    public Account(String accountHolder, String bank, String ifsc, String accountNumber, float balance,
                   Date updatedOn, Member updatedBy) {
        this.accountHolder = accountHolder;
        this.bank = bank;
        this.ifsc = ifsc;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.updatedOn = updatedOn;
        this.updatedBy = updatedBy;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Account))
//            return super.equals(obj);
//        return ((Account) obj).accountNumber.trim().equals(this.accountNumber.trim());
//    }

    @Override
    public String toString() {
        return "Account{" +
                "accountHolder='" + accountHolder + '\'' +
                ", bank='" + bank + '\'' +
                ", ifsc='" + ifsc + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", updatedOn=" + updatedOn +
                ", updatedBy=" + updatedBy +
                '}';
    }


    public String getAccountNumber() {
        return this.accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public String getAccountHolder() {
        return this.accountHolder;
    }


    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }


    public String getBank() {
        return this.bank;
    }


    public void setBank(String bank) {
        this.bank = bank;
    }


    public String getIfsc() {
        return this.ifsc;
    }


    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }


    public float getBalance() {
        return this.balance;
    }


    public void setBalance(float balance) {
        this.balance = balance;
    }


    public Date getUpdatedOn() {
        return this.updatedOn;
    }


    public void setUpdatedOn(Date updatedOn) {
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


    public Account() {
    }


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 335469827)
    private transient AccountDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accountNumber);
        dest.writeString(this.accountHolder);
        dest.writeString(this.bank);
        dest.writeString(this.ifsc);
        dest.writeString(this.userid);
        dest.writeString(this.password);
        dest.writeFloat(this.balance);
        dest.writeLong(this.updatedOn != null ? this.updatedOn.getTime() : -1);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeString(this.updatedByMemberId);
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1812283172)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAccountDao() : null;
    }

    protected Account(Parcel in) {
        this.accountNumber = in.readString();
        this.accountHolder = in.readString();
        this.bank = in.readString();
        this.ifsc = in.readString();
        this.userid = in.readString();
        this.password = in.readString();
        this.balance = in.readFloat();
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.updatedByMemberId = in.readString();
    }

    @Generated(hash = 547547302)
    public Account(String accountNumber, String accountHolder, String bank, String ifsc, String userid,
            String password, float balance, Date updatedOn, String updatedByMemberId) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.bank = bank;
        this.ifsc = ifsc;
        this.userid = userid;
        this.password = password;
        this.balance = balance;
        this.updatedOn = updatedOn;
        this.updatedByMemberId = updatedByMemberId;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
