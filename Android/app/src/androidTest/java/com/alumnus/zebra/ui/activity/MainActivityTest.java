package com.alumnus.zebra.ui.activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alumnus.zebra.R;
import com.alumnus.zebra.utils.TestHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void verifyAccelerometerGraphAvailability() {
        TestHelper.INSTANCE.delay(300);
        onView(withId(R.id.buttonAccelerometer)).perform(click());
        TestHelper.INSTANCE.delay(300);
        onView(withId(R.id.chart_xyz)).check(matches(isDisplayed()));
        onView(withId(R.id.chart_tsv)).check(matches(isDisplayed()));
        TestHelper.INSTANCE.delay(3000);
        pressBack();

    }

    @Test
    public void exportDataValidationTest() {
        onView(withId(R.id.buttonExportData)).perform(click());

    }
}