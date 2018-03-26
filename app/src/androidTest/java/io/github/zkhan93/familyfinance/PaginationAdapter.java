package io.github.zkhan93.familyfinance;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.test.espresso.Espresso;

@RunWith(AndroidJUnit4.class)
public class PaginationAdapter {
    @Rule
    public ActivityTestRule<LoginActivity> mLoginRule =
            new ActivityTestRule(LoginActivity.class);

    @Test
    public void test_text() {
//        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
//                .perform(ViewActions.click()).check();
//        Espresso.onView(ViewMatchers.withText("Zeeshan Khan")).perform(ViewActions.click());
//        Espresso.onView(ViewMatchers.withId(R.id.progress_message)).check(ViewAssertions.matches(ViewMatchers.withSpinnerText("Select an account..")));
    }

//    AppCompatActivity activity = Robolectric.setupActivity(LoginActivity.class);
//    activity.findViewById(R.id.sign_in_button).performClick();
//    activity.findViewById(R.id.progress_message)

}
