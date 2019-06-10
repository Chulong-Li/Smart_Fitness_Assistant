package com.example.smart_fitness;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Tests UI send message activity
 */
@RunWith(AndroidJUnit4.class)
public class SendMessageTest {
    private static final String STRING_TO_BE_TYPED = "Hello World!";

    // tests if message sent is what appears on screen
    @Test
    public void changeText() {
        // Type text and then press the button.
        onView(withId(R.id.editText)).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.send_button)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.message_body)).check(matches(withText(STRING_TO_BE_TYPED)));
    }

}
