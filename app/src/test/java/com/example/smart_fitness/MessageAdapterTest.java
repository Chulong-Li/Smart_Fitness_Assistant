package com.example.smart_fitness;

import android.widget.EditText;
import android.widget.ListView;
import android.view.View;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests message adapter
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MessageAdapterTest {
    @Test
    public void addMessage_Test() {

        MessageAdapter messageAdapter = null;
        ListView messagesView;

        MemberData data = new MemberData("Steve", "#bb0000");

        final Message welcome = new Message("Hi, I'm Steve! What can I help you?", data, false);

        messageAdapter.add(welcome);

        int size = (messageAdapter.messages.size()) - 1;

        assertEquals("Hi, I'm Steve! What can I help you?", messageAdapter.messages.get(size));

    }

    @Test
    public void getView_Test() {



    }
}