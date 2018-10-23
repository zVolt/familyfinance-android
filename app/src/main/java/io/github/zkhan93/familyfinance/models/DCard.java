package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class DCard extends BaseModel {

    public static final SimpleDateFormat EXPIRE_ON = new SimpleDateFormat("mm/yy", Locale.US);
    @Id
    String number;

    String name, bank, cardholder, phoneNumber, pin, email, username, password, cvv;
    long updatedOn, expireOn;

    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;

    @Exclude
    private String updatedByMemberId;

    @Exclude
    public String getFormattedNumber(char delimiter) {
        return getFormattedNumber(delimiter, false);
    }

    @Exclude
    public String getFormattedNumber(char delimiter, boolean hideDigits) {
        StringBuilder strb = new StringBuilder(19);
        int i;
        if (hideDigits) {
            for (i = 0; i < this.number.length() - 4; i++)
                strb.append("X");
            strb.append(number.substring(number.length() - 4));
        } else
            strb.append(this.number);
        String number = strb.toString();
        strb.setLength(0);
        i = 1;
        for (char c : number.toCharArray()) {
            strb.append(c);
            if (i % 4 == 0)
                strb.append(delimiter);
            i++;
        }
        strb.deleteCharAt(strb.length() - 1);
        return strb.toString();
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

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank() {
        return this.bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCardholder() {
        return this.cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public String getCvv() {
        return this.cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public long getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(long expireOn) {
        this.expireOn = expireOn;
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
    @Exclude
    @Keep
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
    @Exclude
    @Keep
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
    @Exclude
    @Keep
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public DCard() {
    }


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1662660490)
    private transient DCardDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;

    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Generated(hash = 1279168689)
    public DCard(String number, String name, String bank, String cardholder,
            String phoneNumber, String pin, String email, String username, String password,
            String cvv, long updatedOn, long expireOn, String updatedByMemberId) {
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.cardholder = cardholder;
        this.phoneNumber = phoneNumber;
        this.pin = pin;
        this.email = email;
        this.username = username;
        this.password = password;
        this.cvv = cvv;
        this.updatedOn = updatedOn;
        this.expireOn = expireOn;
        this.updatedByMemberId = updatedByMemberId;
    }

    @Override
    public String toString() {
        return "DCard{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", bank='" + bank + '\'' +
                ", cardholder='" + cardholder + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", pin='" + pin + '\'' +
                ", email='" + email + '\'' +
                ", cvv='" + cvv + '\'' +
                ", updatedOn=" + updatedOn +
                ", expireOn=" + expireOn +
                ", updatedBy=" + updatedBy +
                ", updatedByMemberId='" + updatedByMemberId + '\'' +
                '}';
    }

    @Exclude
    public String getReadableContent() {
        return "Card Holder: " + cardholder + "\n" +
                "Number: " + number + "\n" +
                "CVV: " + cvv + "\n" +
                "Expire On (MM/YY): " + EXPIRE_ON.format(new Date(expireOn));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.name);
        dest.writeString(this.bank);
        dest.writeString(this.cardholder);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.pin);
        dest.writeString(this.email);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.cvv);
        dest.writeLong(this.updatedOn);
        dest.writeLong(this.expireOn);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeString(this.updatedByMemberId);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1432222888)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDCardDao() : null;
    }

    protected DCard(Parcel in) {
        this.number = in.readString();
        this.name = in.readString();
        this.bank = in.readString();
        this.cardholder = in.readString();
        this.phoneNumber = in.readString();
        this.pin = in.readString();
        this.email = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.cvv = in.readString();
        this.updatedOn = in.readLong();
        this.expireOn = in.readLong();
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.updatedByMemberId = in.readString();
    }

    public static final Creator<DCard> CREATOR = new Creator<DCard>() {
        @Override
        public DCard createFromParcel(Parcel source) {
            return new DCard(source);
        }

        @Override
        public DCard[] newArray(int size) {
            return new DCard[size];
        }
    };
}
