package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by zeeshan on 7/7/17.
 */

public class Account implements Parcelable{
    String name, bank, ifsc, accountNumber;
    float balance;
    Date updatedOn;
    Member updatedBy;

    public Account(String name, String bank, String ifsc, String accountNumber, float balance,
                   Date updatedOn, Member updatedBy) {
        this.name = name;
        this.bank = bank;
        this.ifsc = ifsc;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.updatedOn = updatedOn;
        this.updatedBy = updatedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Member getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Member updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", bank='" + bank + '\'' +
                ", ifsc='" + ifsc + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", updatedOn=" + updatedOn +
                ", updatedBy=" + updatedBy +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.bank);
        dest.writeString(this.ifsc);
        dest.writeString(this.accountNumber);
        dest.writeFloat(this.balance);
        dest.writeLong(this.updatedOn != null ? this.updatedOn.getTime() : -1);
        dest.writeParcelable(this.updatedBy, flags);
    }

    public Account() {
    }

    protected Account(Parcel in) {
        this.name = in.readString();
        this.bank = in.readString();
        this.ifsc = in.readString();
        this.accountNumber = in.readString();
        this.balance = in.readFloat();
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.updatedBy = in.readParcelable(Member.class.getClassLoader());
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
