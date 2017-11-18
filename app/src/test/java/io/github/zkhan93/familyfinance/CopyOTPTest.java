package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import io.github.zkhan93.familyfinance.listeners.CopyOtpListener;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class CopyOTPTest {

    private CopyOtpListener copyOtpListener;
    private Context context;

    @Before
    public void setup() {
        copyOtpListener = new CopyOtpListener();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"this is OPT 1234", "1234"}, {"this is another oOTP 342334", "342334"}
        });
    }

    public CopyOTPTest(String message, String otp) {
        this.message = message;
        this.otp = otp;
    }

    private String message;
    private String otp;

    @Test
    public void otp_extract() {
        Log.d("test", String.format("%s", message));
        Intent intent = new Intent();
        intent.putExtra("OTP", message);
        copyOtpListener.onReceive(context, intent);
//        assertEquals(otp, copyOtpListener.otp);
    }
}