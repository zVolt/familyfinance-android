package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by zeeshan on 7/7/17.
 */
@Entity
@IgnoreExtraProperties
public class Account extends BaseModel {
    @Id
    String accountNumber;
    String accountHolder, bank, ifsc, userid, password, email, phoneNumber;
    float balance;
    long updatedOn;

    @ToOne(joinProperty = "updatedByMemberId")
    @Exclude
    Member updatedBy;

    private String updatedByMemberId;

    @Keep
    public Account(String accountHolder, String bank, String ifsc, String accountNumber, float
            balance,
                   long updatedOn, Member updatedBy) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateFrom(Account account) {
        setAccountNumber(account.getAccountNumber());
        setAccountHolder(account.getAccountHolder());
        setBank(account.getBank());
        setIfsc(account.getIfsc());
        setUserid(account.getUserid());
        setPassword(account.getPassword());
        setBalance(account.getBalance());
        setUpdatedOn(account.getUpdatedOn());
        setUpdatedByMemberId(account.getUpdatedByMemberId());
        setEmail(account.getEmail());
        setPhoneNumber(account.getPhoneNumber());
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Exclude
    @Keep
    public Member getUpdatedBy() {
        String __key = this.updatedByMemberId;
        if (updatedBy__resolvedKey == null || !Objects.equals(updatedBy__resolvedKey, __key)) {
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


    @Keep
    @Exclude
    public String getReadableContent() {
        return  "Account Number: " + getAccountNumber() + "\n" +
                "Name: " + getAccountHolder() + "\n" +
                "IFSC: " + getIfsc() + "\n" +
                "Bank: " + getBank() + "\n";
    }

    @Generated(hash = 594599301)
    public Account(String accountNumber, String accountHolder, String bank, String ifsc, String userid,
            String password, String email, String phoneNumber, float balance, long updatedOn,
            String updatedByMemberId) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.bank = bank;
        this.ifsc = ifsc;
        this.userid = userid;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.updatedOn = updatedOn;
        this.updatedByMemberId = updatedByMemberId;
    }

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
        dest.writeLong(this.updatedOn);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeString(this.updatedByMemberId);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);

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
        this.updatedOn = in.readLong();
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.updatedByMemberId = in.readString();
        this.email = in.readString();
        this.phoneNumber = in.readString();
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
    public static final Comparator<Account> BY_UPDATED_ON = new Comparator<Account>() {
        @Override
        public int compare(Account o1, Account o2) {
            return Long.compare(o2.getUpdatedOn(), o1.getUpdatedOn());
        }
    };
}
