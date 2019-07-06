package com.example.smart_fitness;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.service.security.IamOptions;

import com.ibm.watson.assistant.v1.model.MessageInput;
import com.ibm.watson.assistant.v1.model.MessageOptions;
import com.ibm.watson.assistant.v1.model.MessageResponse;
import com.ibm.watson.assistant.v1.Assistant;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.android.library.camera.GalleryHelper;
import com.ibm.watson.developer_cloud.language_translator.v3.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslationResult;
import com.ibm.watson.developer_cloud.language_translator.v3.util.Language;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity{


    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private Switch sw;

    private MemberData data = new MemberData("Steve", "#bb0000");

    private TextToSpeech textService;
    private MicrophoneHelper microphoneHelper;
    private SpeechToText speechService;
    private MicrophoneInputStream capture;
    private ImageButton mic;
    private boolean listening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        textService = initTextToSpeechService();
        microphoneHelper = new MicrophoneHelper(this);
        speechService = initSpeechToTextService();
        mic = findViewById(R.id.mic);

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


        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listening) {
                    // Update the icon background
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mic.setBackgroundColor(Color.GREEN);
                        }
                    });
                    capture = microphoneHelper.getInputStream(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                speechService.recognizeUsingWebSocket(getRecognizeOptions(capture),
                                        new MicrophoneRecognizeDelegate());
                            } catch (Exception e) {

                            }
                        }
                    }).start();

                    listening = true;
                } else {
                    // Update the icon background
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mic.setBackgroundColor(Color.LTGRAY);
                        }
                    });
                    microphoneHelper.closeInputStream();
                    listening = false;
                }
            }
        });


    }

    public void sendMessage(View view) {

        // Voice Mode Enable Button
        sw = (Switch)findViewById(R.id.switch_button);

        if (sw.isChecked()) {
            // record PCM data and encode it with the ogg codec
            capture = microphoneHelper.getInputStream(true);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    speechService.recognizeUsingWebSocket(getRecognizeOptions(capture),
                            new MicrophoneRecognizeDelegate());

                }
            });
        }

        String text = editText.getText().toString();

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
            IamOptions iamOptions = new IamOptions.Builder().apiKey(getString(R.string.assistant_apikey)).build();
            Assistant service = new Assistant("2019-02-28", iamOptions);
            service.setEndPoint(getString(R.string.assistant_url));

            String workspaceId = getString(R.string.assistant_workspaceId);

            MessageInput input = new MessageInput();
            input.setText(text);

            MessageOptions options = new MessageOptions.Builder(workspaceId)
                    .input(input)
                    .build();

            MessageResponse response = service.message(options).execute().getResult();
            final String response_text = response.getOutput().getGeneric().get(0).getText();

            System.out.println(response_text);


            // Return the text response
            final Message response1 = new Message(response_text, data, false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(response1);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });


            // Make some voice if Voice Mode is enabled
            if (sw.isChecked()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                                .text(response_text)
                                .accept(SynthesizeOptions.Accept.AUDIO_WAV) // specifying that we want a WAV file
                                .build();
                        InputStream streamResult = textService.synthesize(synthesizeOptions).execute();

                        StreamPlayer player = new StreamPlayer();
                        player.playStream(streamResult); // should work like a charm
                    }
                });
            }


        }

    }


    private TextToSpeech initTextToSpeechService() {

        com.ibm.watson.developer_cloud.service.security.IamOptions options = new com.ibm.watson.developer_cloud.service.security.IamOptions.Builder()
                .apiKey(getString(R.string.text_speech_apikey))
                .build();
        TextToSpeech service = new TextToSpeech(options);
        service.setEndPoint(getString(R.string.text_speech_url));
        return service;
    }

    private SpeechToText initSpeechToTextService() {
        com.ibm.watson.developer_cloud.service.security.IamOptions options = new com.ibm.watson.developer_cloud.service.security.IamOptions.Builder()
                .apiKey(getString(R.string.speech_text_apikey))
                .build();
        SpeechToText service = new SpeechToText(options);
        service.setEndPoint(getString(R.string.speech_text_url));
        return service;
    }

    private RecognizeOptions getRecognizeOptions(InputStream captureStream) {
        return new RecognizeOptions.Builder()
                .audio(captureStream)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }

    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                showMicText(text);
            }
        }

    }
    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setText(text);
            }
        });
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
