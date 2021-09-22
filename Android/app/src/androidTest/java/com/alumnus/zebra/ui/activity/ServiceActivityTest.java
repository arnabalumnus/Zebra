package com.alumnus.zebra.ui.activity;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.alumnus.zebra.R;
import com.alumnus.zebra.utils.TestHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class ServiceActivityTest {

    @Rule
    public ActivityScenarioRule<ServiceActivity> mActivityTestRule = new ActivityScenarioRule<>(ServiceActivity.class);


    @Test
    public void verifyLifeTimeServiceRunning() throws UiObjectNotFoundException {
        TestHelper.INSTANCE.delay(300);
        onView(withId(R.id.high_freq)).perform(ViewActions.click());
        onView(withId(R.id.buttonLifeTimeService)).perform(ViewActions.click());
        TestHelper.INSTANCE.delay(500);
        /** verify Zebra notification */
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        mDevice.swipe(100,1,100,500,10);
        TestHelper.INSTANCE.delay(3000);
        UiObject notification = mDevice.findObject(new UiSelector().textContains("Zebra"));

        TestHelper.INSTANCE.delay(1000);
        mDevice.swipe(100,500,100,0,10);
        TestHelper.INSTANCE.delay(1000);

    }
}