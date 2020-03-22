package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import java.util.Objects;

@Entity
public class Wallet extends BaseModel {
    @Id
    String username;
    String password;
    float balance;
    String description;
    int type;

    @ToOne(joinProperty = "updatedByMemberId")
    Member updatedBy;

    @Exclude
    private String updatedByMemberId;

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

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeFloat(this.balance);
        dest.writeString(this.description);
        dest.writeInt(this.type);
        dest.writeString(this.updatedByMemberId);

    }

    public String getUpdatedByMemberId() {
        return this.updatedByMemberId;
    }

    public void setUpdatedByMemberId(String updatedByMemberId) {
        this.updatedByMemberId = updatedByMemberId;
    }

    /** To-one relationship, resolved on first access. */
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
    @Generated(hash = 657468544)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWalletDao() : null;
    }

    public Wallet() {
    }

    protected Wallet(Parcel in) {
        this.username = in.readString();
        this.password = in.readString();
        this.balance = in.readFloat();
        this.description = in.readString();
        this.type = in.readInt();
        this.updatedByMemberId = in.readString();
    }

    @Keep
    public Wallet(String username, String password, float balance,
            String description, int type, String updatedByMemberId) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.description = description;
        this.type = type;
        this.updatedByMemberId = updatedByMemberId;
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel source) {
            return new Wallet(source);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 741381941)
    private transient WalletDao myDao;
    @Generated(hash = 1066823846)
    private transient String updatedBy__resolvedKey;
}
