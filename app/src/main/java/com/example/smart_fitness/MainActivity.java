package com.example.smart_fitness;

import android.content.Context;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v1.model.MessageInput;
import com.ibm.watson.assistant.v1.model.MessageOptions;
import com.ibm.watson.assistant.v1.model.MessageResponse;
import com.ibm.watson.assistant.v1.Assistant;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{


    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    TextToSpeech t1;

    private Switch sw;

    private MemberData data = new MemberData("Steve", "#bb0000");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        final Message welcome = new Message("Hi, I'm Steve! What can I help you?", data, false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.add(welcome);
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    public void sendMessage(View view) {
        String text = editText.getText().toString();

        sw = (Switch)findViewById(R.id.switch_button);

        if (text.length() > 0) {

            // We provide a question here!
            final Message message = new Message(text, data, true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
            editText.getText().clear();


            // Set up
            IamOptions iamOptions = new IamOptions.Builder().apiKey("U-3rEEX7HUmUklltEJc3VkGIBbIk_MMQ-DhBnoKYFV9I").build();
            Assistant service = new Assistant("2019-02-28", iamOptions);
            service.setEndPoint("https://gateway.watsonplatform.net/assistant/api");


            String workspaceId = "39ad1c75-4ad0-4048-b5a3-ce07240fb471";



            MessageInput input = new MessageInput();
            input.setText(text);

            MessageOptions options = new MessageOptions.Builder(workspaceId)
                    .input(input)
                    .build();

            MessageResponse response = service.message(options).execute().getResult();

            System.out.println(response);

            final String response_text = response.getOutput().getGeneric().get(0).getText();

            System.out.println(response_text);

            if (sw.isChecked()) {
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        t1.setLanguage(Locale.UK);
                        t1.setSpeechRate((float) 0.5);
                        t1.speak(response_text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
            }


            /*
            // Response by IBM Watson Assistant

            IamOptions iamOptions = new IamOptions.Builder().apiKey("U-3rEEX7HUmUklltEJc3VkGIBbIk_MMQ-DhBnoKYFV9I").build();
            Assistant service = new Assistant("2019-06-01", iamOptions);
            service.setEndPoint("https://gateway.watsonplatform.net/assistant/api");

            String assistant_id = "fc3b982f-eeba-4572-b836-fc2f3fa1c8cb";

            MessageInput input = new MessageInput.Builder()
                    .text(text)
                    .build();

            MessageOptions options = new MessageOptions.Builder()
                    .assistantId(assistant_id)
                    .input(input)
                    .build();


            MessageResponse messageResponse = service.message(options).execute().getResult();


            String response_text = messageResponse.getOutput().getGeneric().get(0).getText();

            final Message response = new Message(response_text, data, false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(response);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });

            */
            final Message response1 = new Message(response_text, data, false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(response1);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        }
    }
}

class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}
