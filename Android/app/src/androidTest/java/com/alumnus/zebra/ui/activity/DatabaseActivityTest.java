package com.alumnus.zebra.ui.activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.alumnus.zebra.R;
import com.alumnus.zebra.utils.TestHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(JUnit4.class)
public class DatabaseActivityTest {

    @Rule
    public ActivityScenarioRule<DatabaseActivity> scenario = new ActivityScenarioRule<>(DatabaseActivity.class);

    @Test
    public void getTotalCount() {
        onView(withId(R.id.buttonGetAllEvent)).perform(click());
        TestHelper.INSTANCE.delay(300);
    }

    @Test
    public void getLastTimeStamp() {
        onView(withId(R.id.buttonGetAllEvent)).perform(click());
        TestHelper.INSTANCE.delay(300);
    }

    @Test
    public void getCsvListTable() {
        onView(withId(R.id.btn_csv_list)).perform(click());
        TestHelper.INSTANCE.delay(300);
        onView(withId(R.id.tv_csv_list_table)).check(matches(isDisplayed()));
    }
}