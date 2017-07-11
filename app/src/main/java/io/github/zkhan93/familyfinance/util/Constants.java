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

    private static List<Member> getDummyMembers(int size) {
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            memberList.add(getNewRandomMember());
        }
        return memberList;
    }

    private static Member getRandomMember(MemberDao memberDao) {
        if (memberDao == null)
            return getNewRandomMember();
        List<Member> members = memberDao.loadAll();
        return members.get(new Random().nextInt(members.size()));
    }

    private static Member getNewRandomMember() {
        int i = new Random().nextInt();
        return new Member("Member " + i, "member" + i + "@gmail.com", i + "", i % 5 == 0);
    }

    private static List<Account> getDummyAccounts(int size, MemberDao memberDao) {
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Member member = getRandomMember(memberDao);
            Account account =
                    new Account("Account " + i, "bank" + i, "BANK00001234", "0000" + i +
                            "00000000", i * 45340.4f, Calendar.getInstance().getTime(),
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
            cCard = new CCard("Card " + i, "000000" + i + "234234234", "bank" + i, "cardholder"
                    + i, cal.getTime(), random.nextInt(30), random.nextInt(30), maxLimit,
                    maxLimit *
                    percentConsumed, maxLimit * (1 - percentConsumed),member.getId());
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
                            .getInstance().getTime());
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

        protected Void doInBackground(Void... params) {
            App app = appWeakReference.get();
            if (app == null)
                return null;
            Random random = new Random();
            DaoSession daoSession = app.getDaoSession();
            boolean deleteAll = false;

            if (deleteAll) daoSession.getMemberDao().deleteAll();
            if (daoSession.getMemberDao().loadAll().size() < 5)
                daoSession.getMemberDao().insertOrReplaceInTx(getDummyMembers(random.nextInt
                        (100)));

            if (deleteAll) daoSession.getAccountDao().deleteAll();
            if (daoSession.getAccountDao().loadAll().size() < 5)
                daoSession.getAccountDao().insertOrReplaceInTx(getDummyAccounts(random
                                .nextInt(100),
                        daoSession.getMemberDao()));

            if (deleteAll) daoSession.getCCardDao().deleteAll();
            if (daoSession.getCCardDao().loadAll().size() < 5)
                daoSession.getCCardDao().insertOrReplaceInTx(getDummyCCards(random.nextInt(100),
                        daoSession.getMemberDao()));

            if (deleteAll) daoSession.getOtpDao().deleteAll();
            if (daoSession.getOtpDao().loadAll().size() < 5)
                daoSession.getOtpDao().insertOrReplaceInTx(getDummyOtps(random.nextInt(100),
                        daoSession
                                .getMemberDao()));
            return null;
        }
    }
}
