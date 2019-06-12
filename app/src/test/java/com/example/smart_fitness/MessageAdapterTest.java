package com.example.smart_fitness;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Tests message adapter
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MessageAdapterTest {

    @Test
    public void addMessage_Test() {
        MessageAdapter messageAdapter;

        MemberData data = new MemberData("Steve", "#bb0000");

        final Message message = new Message("Hi, I'm Steve! What can I help you?", data, true);

        messageAdapter = new MessageAdapter();

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