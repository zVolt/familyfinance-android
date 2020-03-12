package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by zeeshan on 7/7/17.
 */
@Entity
public class CCard extends BaseModel {
    public static final SimpleDateFormat EXPIRE_ON = new SimpleDateFormat("mm/yy", Locale.US);
    @Id
    String number;
    String name, bank, cardholder, userid, password, cvv, phoneNumber, email;
    long updatedOn, expireOn;
    int paymentDay, billingDay;
    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;
    float maxLimit, consumedLimit;

    @ToMany(referencedJoinProperty = "mainCardNumber")
    @OrderBy("updatedOn DESC")
    @Exclude
    List<AddonCard> addonCards;

    public void updateFrom(CCard cCard) {
        number = cCard.getNumber();
        name = cCard.getName();
        bank = cCard.getBank();
        cardholder = cCard.getCardholder();
        userid = cCard.getUserid();
        password = cCard.getPassword();
        updatedOn = cCard.getUpdatedOn();
        paymentDay = cCard.getPaymentDay();
        billingDay = cCard.getBillingDay();
        maxLimit = cCard.getMaxLimit();
        consumedLimit = cCard.getConsumedLimit();
        cvv = cCard.getCvv();
        expireOn = cCard.getExpireOn();
        addonCards = cCard.addonCards;
        phoneNumber = cCard.getPhoneNumber();
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public long getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(long expireOn) {
        this.expireOn = expireOn;
    }

    @Exclude
    public String getReadableContent() {
        return "Card Holder: " + cardholder + "\n" +
                "Number: " + number + "\n" +
                "CVV: " + cvv + "\n" +
                "Expire On (MM/YY): " + EXPIRE_ON.format(new Date(expireOn)) +
                "\n" +
                "Remaining Limit: " + getRemainingLimit() + "\n";
    }

    @Exclude
    private String updatedByMemberId;

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

    public long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public Date getPaymentDate() {
        Calendar today = Calendar.getInstance();
        Calendar paymentDate = Calendar.getInstance();
        paymentDate.set(Calendar.DAY_OF_MONTH, getPaymentDay());
        if (today.get(Calendar.DAY_OF_MONTH) > getPaymentDay())
            paymentDate.add(Calendar.MONTH, 1);
        return paymentDate.getTime();
    }

    @Exclude
    public float getRemainingLimit() {
        return maxLimit - consumedLimit;
    }

    public String getFormattedNumber(char delimiter) {
        return getFormattedNumber(delimiter, false);
    }

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

    @Override
    public String toString() {
        return "CCard{" +
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
                ", phoneNumber=" + phoneNumber +
                ", consumedLimit=" + consumedLimit +
                ", updatedByMemberId='" + updatedByMemberId + '\'' +
                ", email=" + email +
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

    public CCard() {
    }

    @Keep
    public CCard(String name, String number, String bank, String cardholder, long updatedOn,
                 int paymentDay, int billingDay, float maxLimit, float consumedLimit, String
                         updatedByMemberId) {
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.cardholder = cardholder;
        this.updatedOn = updatedOn;
        this.paymentDay = paymentDay;
        this.billingDay = billingDay;
        this.maxLimit = maxLimit;
        this.consumedLimit = consumedLimit;
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

    @Generated(hash = 66803068)
    public CCard(String number, String name, String bank, String cardholder, String userid,
            String password, String cvv, String phoneNumber, String email, long updatedOn,
            long expireOn, int paymentDay, int billingDay, float maxLimit, float consumedLimit,
            String updatedByMemberId) {
        this.number = number;
        this.name = name;
        this.bank = bank;
        this.cardholder = cardholder;
        this.userid = userid;
        this.password = password;
        this.cvv = cvv;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.updatedOn = updatedOn;
        this.expireOn = expireOn;
        this.paymentDay = paymentDay;
        this.billingDay = billingDay;
        this.maxLimit = maxLimit;
        this.consumedLimit = consumedLimit;
        this.updatedByMemberId = updatedByMemberId;
    }

    public static final Comparator<CCard> BY_UPDATED_ON = new Comparator<CCard>() {
        @Override
        public int compare(CCard o1, CCard o2) {
            return Long.compare(o2.getUpdatedOn(), o1.getUpdatedOn());
        }
    };

    public static final Comparator<CCard> BY_PAYMENT_DATE = new Comparator<CCard>() {
        @Override
        public int compare(CCard o1, CCard o2) {
            return Long.compare(o1.getPaymentDate().getTime(), o2.getPaymentDate().getTime());
        }
    };

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        dest.writeString(this.userid);
        dest.writeString(this.password);
        dest.writeString(this.cvv);
        dest.writeString(this.phoneNumber);
        dest.writeLong(this.updatedOn);
        dest.writeLong(this.expireOn);
        dest.writeInt(this.paymentDay);
        dest.writeInt(this.billingDay);
        dest.writeParcelable(this.updatedBy, flags);
        dest.writeFloat(this.maxLimit);
        dest.writeFloat(this.consumedLimit);
        dest.writeTypedList(this.addonCards);
        dest.writeString(this.updatedByMemberId);
        dest.writeString(this.email);
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Keep
    @Exclude
    public List<AddonCard> getAddonCards() {
        if (addonCards == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AddonCardDao targetDao = daoSession.getAddonCardDao();
            List<AddonCard> addonCardsNew = targetDao._queryCCard_AddonCards(number);
            synchronized (this) {
                if (addonCards == null) {
                    addonCards = addonCardsNew;
                }
            }
        }
        return addonCards;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 124690502)
    public synchronized void resetAddonCards() {
        addonCards = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1480820516)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCCardDao() : null;
    }

    protected CCard(Parcel in) {
        this.number = in.readString();
        this.name = in.readString();
        this.bank = in.readString();
        this.cardholder = in.readString();
        this.userid = in.readString();
        this.password = in.readString();
        this.cvv = in.readString();
        this.phoneNumber = in.readString();
        this.updatedOn = in.readLong();
        this.expireOn = in.readLong();
        this.paymentDay = in.readInt();
        this.billingDay = in.readInt();
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
        this.maxLimit = in.readFloat();
        this.consumedLimit = in.readFloat();
        this.addonCards = in.createTypedArrayList(AddonCard.CREATOR);
        this.updatedByMemberId = in.readString();
        this.email = in.readString();
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
