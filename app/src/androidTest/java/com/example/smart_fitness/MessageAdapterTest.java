package com.example.smart_fitness;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


/**
 * Tests message adapter
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MessageAdapterTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void addMessage_Test() {
        MessageAdapter messageAdapter;

        MemberData data = new MemberData("Steve", "#bb0000");

        final Message message = new Message("Hi, I'm Steve! What can I help you?", data, true);

        Context context = mActivityRule.getActivity().getBaseContext();

        messageAdapter = new MessageAdapter(context);

        messageAdapter.add(message);

        int i = (messageAdapter.messages.size()) - 1;

        String expected = "Hi, I'm Steve! What can I help you?";

        final Message message2 = messageAdapter.messages.get(i);

        String actual = message2.getText();

        assertEquals("Messages are not the same", expected, actual);

    }

    @Test
    public void getView_Test() {

        /*MessageAdapter messageAdapter = null;

        MemberData data = new MemberData("Steve", "#bb0000");

        final Message message = new Message("Hi, I'm Steve! What can I help you?", data, true);

        messageAdapter.add(message);

        Context context = null;

        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);*/

    }
}