package farijo.com.starcraft_bo_shower.file_explorer;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Teddy on 08/01/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BOExplorerActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<BOExplorerActivity> mActivityRule =
            new ActivityTestRule(BOExplorerActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Hello world!")).check(matches(isDisplayed()));
    }
}
