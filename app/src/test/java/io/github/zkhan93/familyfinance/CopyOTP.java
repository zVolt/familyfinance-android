package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.zkhan93.familyfinance.listeners.NotificationActionsListener;
import io.github.zkhan93.familyfinance.util.Util;

import static io.github.zkhan93.familyfinance.listeners.NotificationActionsListener.ACTION_COPY_OTP;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class CopyOTP {

    private NotificationActionsListener notificationActionsListener;

    @Mock
    Context mockContext;
    @Mock
    Util mockUtil;

    @Before
    public void setup() {
        notificationActionsListener = new NotificationActionsListener();
    }

//    @Parameterized.Parameters
//    public static Collection<Object[]> data() {
//        return Arrays.asList(new Object[][]{
//                {"this is OPT 1234", "1234"}, {"this is another oOTP 342334", "342334"}
//        });
//    }

    public CopyOTP() {
        this.message = "this is OPT 1234";
        this.otp = "1234";
    }

    private String message;
    private String otp;

    @Test
    public void otp_extract() {
//        Intent mockIntent = mock(Intent.class);
//        when(mockIntent.getAction()).thenReturn(ACTION_COPY_OTP);
//        when(mockIntent.getStringExtra("OTP")).thenReturn(message);
//        when(mockUtil.copyToClipboard(any(), any(), any())).thenReturn("");
//        notificationActionsListener.onReceive(mockContext, mockIntent);
    }
}