package io.github.zkhan93.familyfinance.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;

/**
 * Created by zeeshan on 8/7/17.
 */

public class Constants {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM hh:mm aaa");
    public static final SimpleDateFormat PAYMENT_DATE = new SimpleDateFormat("d MMM");

    public static ArrayList<Member> getDummyMembers() {
        ArrayList<Member> data = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(100); i++) {
            data.add(getRandomMember());
        }
        return data;
    }

    private static Member getRandomMember() {
        int i = new Random().nextInt();
        return new Member("Member " + i, "member" + i + "@gmail.com", i + "", i % 5 == 0);
    }

    public static ArrayList<Account> getDummyAccounts() {
        ArrayList<Account> data = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(100); i++) {
            data.add(new Account("Account " + i, "bank" + i, "BANK00001234", "0000" + i +
                    "00000000", i * 45340.4f, Calendar.getInstance().getTime(), getRandomMember()));
        }
        return data;
    }

    public static ArrayList<CCard> getDummyCCards() {
        ArrayList<CCard> data = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(100); i++) {
            data.add(new CCard("Card " + i, "000000" + i + "234234234", "bank" + i, "cardholder"
                    + i, Calendar.getInstance().getTime(), i + 34, getRandomMember(), i *
                    7800.00f, i * 800.00f, i * 500f));
        }
        return data;
    }

    public static ArrayList<Otp> getDummyOtps() {
        ArrayList<Otp> data = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(100); i++) {
//            data.add(new Otp("Member " + i, "member" + i + "@gmail.com", i + "", i % 5 == 0));
        }
        return data;
    }
}
