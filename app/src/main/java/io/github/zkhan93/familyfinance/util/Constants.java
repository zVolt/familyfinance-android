package io.github.zkhan93.familyfinance.util;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.MemberDao;
import io.github.zkhan93.familyfinance.models.Otp;

/**
 * Created by zeeshan on 8/7/17.
 */

public class Constants {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM hh:mm " +
            "aaa", Locale.US);
    public static final SimpleDateFormat PAYMENT_DATE = new SimpleDateFormat("d MMM", Locale.US);


    //TODO: delete the following methods, they are just to provide dummy data
    public static void generateDummyData(App app) {
        new WriteDataTask(app).execute();
    }

    private static List<Member> getDummyMembers() {
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            memberList.add(new Member(i + "", names[i], emails[i], names[i].length() % 2 == 0,
                    ""));
        }
        return memberList;
    }

    public static Member getRandomMember(MemberDao memberDao) {
        if (memberDao == null)
            return null;
        List<Member> members = memberDao.loadAll();
        return members.get(new Random().nextInt(members.size()));
    }

    private static String[] names = {
            "Zeeshan Khan",
            "Somnath Saha",
            "Bushra Rehman Khan",
            "Sarfaraz Nawaz",
            "Dileep Gupta",
            "Anurag Khare",
            "Khushboo Asthana",
            "Soumya Ojha",
            "Vyom Srivastava",
            "Rohit Karpoor",
            "Swati Kesarwani",
            "Ambuj Pandey",
            "Danish Kamal"
    };
    private static String[] emails = {
            "zeeshan.khan@gmail.com",
            "somnath.saha@gmail.com",
            "bushra.rehman.khan@gmail.com",
            "sarfaraz.nawaz@gmail.com",
            "dileep.gupta@gmail.com",
            "anurag.khare@gmail.com",
            "khushboo.asthana@gmail.com",
            "soumya.ojha@gmail.com",
            "vyom.srivastava@gmail.com",
            "rohit.karpoor@gmail.com",
            "swati.kesarwani@gmail.com",
            "ambuj.pandey@gmail.com",
            "danish.kamal@gmail.com"
    };
    private static String[] banks = {
            "SBI",
            "ICICI", "Bank of Baroda", "PNB", "Allahabad Bank", "IndusInd Bank", "HDFC Bank"
    };
//    private static Member getNewRandomMember() {
//        int i = new Random().nextInt();
//        return new Member("Member " + i, "member" + i + "@gmail.com", i + "", i % 5 == 0);
//    }

    private static List<Account> getDummyAccounts(int size, MemberDao memberDao) {
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Member member = getRandomMember(memberDao);
            Account account =
                    new Account("Account " + i, banks[i % banks.length], "BANK0000234", "0000" +
                            i +
                            "00000000", new Random().nextFloat() * 10000, Calendar
                            .getInstance().getTimeInMillis(),
                            member);
            account.setUpdatedByMemberId(member.getId());
            accountList.add(account);
        }
        return accountList;
    }

    private static ArrayList<CCard> getDummyCCards(int size, MemberDao memberDao) {
        ArrayList<CCard> data = new ArrayList<>();
        CCard cCard;
        Member member;
        Calendar cal = Calendar.getInstance();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            member = getRandomMember(memberDao);
            float percentConsumed = new Random().nextFloat();
            float maxLimit = new Random().nextFloat() * 10000;
            cCard = new CCard("Card " + i, "000000" + i + "234234234", banks[i % banks.length],
                    getRandomMember(memberDao).getName(), cal.getTimeInMillis(), random.nextInt(30),
                    random.nextInt(30), maxLimit,
                    maxLimit *
                            percentConsumed, maxLimit * (1 - percentConsumed), member.getId());
            data.add(cCard);
        }
        return data;
    }

    private static ArrayList<Otp> getDummyOtps(int size, MemberDao memberDao) {
        ArrayList<Otp> data = new ArrayList<>();
        Otp otp;
        Member member;
        for (int i = 0; i < size; i++) {
            member = getRandomMember(memberDao);
            otp = new Otp("otp_ " + i, "8932061116", "Lorem Ipsum is simply dummy text of the " +
                    "printing and typesetting industry. Lorem Ipsum has been the industry's " +
                    "standard dummy text ever since the 1500s,", member,
                    Calendar
                            .getInstance().getTimeInMillis());
            otp.setFromMemberId(member.getId());
            data.add(otp);
        }
        return data;
    }

    private static class WriteDataTask extends AsyncTask<Void, Void, Void> {
        WeakReference<App> appWeakReference;

        WriteDataTask(App app) {
            appWeakReference = new WeakReference<>(app);
        }

        boolean recreate = false;
        boolean deleteAll = false;
        int max = 20;
        Random random = new Random();

        protected Void doInBackground(Void... params) {
            App app = appWeakReference.get();
            if (app == null)
                return null;
            DaoSession daoSession = app.getDaoSession();
            if (deleteAll) daoSession.getMemberDao().deleteAll();
            if (recreate)
                daoSession.getMemberDao().insertOrReplaceInTx(getDummyMembers());

            if (deleteAll) daoSession.getAccountDao().deleteAll();
            if (recreate)
                daoSession.getAccountDao().insertOrReplaceInTx(getDummyAccounts(random
                                .nextInt(max),
                        daoSession.getMemberDao()));

            if (deleteAll) daoSession.getCCardDao().deleteAll();
            if (recreate)
                daoSession.getCCardDao().insertOrReplaceInTx(getDummyCCards(random.nextInt(max),
                        daoSession.getMemberDao()));

            if (deleteAll) daoSession.getOtpDao().deleteAll();
            if (recreate)
                daoSession.getOtpDao().insertOrReplaceInTx(getDummyOtps(random.nextInt(max),
                        daoSession
                                .getMemberDao()));
            return null;
        }
    }

    interface CARD_TYPE {
        int VISA = 1;
        int AMERICAL_EXPRESS = 2;
        int CHINA_PAY = 3;
        int DINERS_CLUB = 4;
        int DISCOVER_CARD = 5;
        int JBC = 6;
        int LASER = 7;
        int MASTERO = 8;
        int MASTERCARD = 9;
        int DANKORT = 10;
        int UNKNOWN = 11;
    }

    private static int getCardType(int cardPrefix4d) {

        if (cardPrefix4d / 1000 == 4) return CARD_TYPE.VISA;

        switch (cardPrefix4d / 100) {
            case 34:
            case 37:
                return CARD_TYPE.AMERICAL_EXPRESS;
            case 62:
            case 68:
                return CARD_TYPE.CHINA_PAY;
            case 36:
            case 38:
            case 39:
                return CARD_TYPE.DINERS_CLUB;
            case 65:
                return CARD_TYPE.DISCOVER_CARD;
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
                return CARD_TYPE.MASTERCARD;
        }
        switch (cardPrefix4d / 10) {

            case 300:
            case 301:
            case 302:
            case 303:
            case 304:
            case 305:
            case 309:
                return CARD_TYPE.DINERS_CLUB;

            case 644:
            case 645:
            case 646:
            case 647:
            case 648:
            case 649:
                return CARD_TYPE.DISCOVER_CARD;
        }
        switch (cardPrefix4d) {
            case 6011:
            case 3528:
            case 3589:
            default:
                return CARD_TYPE.UNKNOWN;
        }
    }
}
