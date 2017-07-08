package io.github.zkhan93.familyfinance.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCard implements Parcelable {
    String name, number, bank, cardholder;
    Date updatedOn;
    int paymentDay;
    Member updatedBy;
    float maxLimit, consumedLimit, remainingLimit;

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

    public Member getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Member updatedBy) {
        this.updatedBy = updatedBy;
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
}
