package farijo.com.starcraft_bo_shower;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import farijo.com.starcraft_bo_shower.file_explorer.BOExplorerActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Eikichi on 16/01/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<BOExplorerActivity> mActivityRule =
            new ActivityTestRule(StartActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Parcourir les BO")).check(matches(isDisplayed()));
        onView(withId(R.id.searchBO)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.minus_one)).perform(click());
    }
}
