package io.github.zkhan93.familyfinance.util;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
}
