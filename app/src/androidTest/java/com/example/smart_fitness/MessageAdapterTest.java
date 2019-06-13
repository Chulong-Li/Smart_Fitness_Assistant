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

    /*@Test
    public void getView_Test() {

        Context context = mActivityRule.getActivity().getBaseContext();

        int i = 0;
        View convertView = new View(context);
        MessageAdapter messageAdapter = new MessageAdapter(context);
        ViewGroup viewGroup = new ViewGroup(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        Object holder = new MessageViewHolder();
        convertView = messageAdapter.getView(i, convertView, viewGroup);
        holder = convertView.getTag();
        String mess = holder.messageBody.getText();
    }*/
}