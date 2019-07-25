package com.example.smart_fitness;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.ImageButton;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton;

import java.io.InputStream;
import com.ibm.cloud.sdk.core.service.security.IamOptions;

import com.ibm.watson.assistant.v1.model.MessageInput;
import com.ibm.watson.assistant.v1.model.MessageOptions;
import com.ibm.watson.assistant.v1.model.MessageResponse;
import com.ibm.watson.assistant.v1.Assistant;

import com.ibm.watson.discovery.v1.Discovery;
import com.ibm.watson.discovery.v1.model.QueryOptions;
import com.ibm.watson.discovery.v1.model.QueryResponse;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.SynthesizeOptions;


public class MainActivity extends AppCompatActivity{


    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private Switch sw;

    private MemberData data = new MemberData("Steve", "#bb0000");

    private TextToSpeech textService;
    private MicrophoneHelper microphoneHelper;
    private SpeechToText speechService;
    private Assistant assistantService;
    private MicrophoneInputStream capture;
    private ImageButton mic;
    private boolean listening = false;
    private static String result;
    private MessageResponse response;
    private String text;


    String last_text = "";
    String[] array_of_steps = {};
    public int mIfCounter = 0;
    public int mIfCounter2 = 0;
    public TextView mViewLabel;
    int size;
    String s2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        textService = initTextToSpeechService();
        microphoneHelper = new MicrophoneHelper(this);
        speechService = initSpeechToTextService();
        assistantService = initAssistantService();

        //mic = findViewById(R.id.mic);

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

        /*

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
                    sendMessage(null);
                    editText.getText().clear();
                    listening = false;
                }
            }
        });
        */

        // Voice Mode Enable Button
        Switch sButton = (Switch) findViewById(R.id.switch_button);

        //Set a CheckedChange Listener for Switch Button
        sButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on){
                if(on) {
                    // record PCM data and encode it with the ogg codec

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
                    if (listening) {
                        microphoneHelper.closeInputStream();
                        sendMessage(null);
                        editText.getText().clear();
                        listening = false;
                    }
                }
            }
        });
    }



    public void sendMessage(final View view) {
        text = editText.getText().toString();

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

            //editText.getText().clear();



            // Set up Assistant

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessageInput input = new MessageInput();
                    input.setText(text);
                    MessageOptions options = new MessageOptions.Builder(getString(R.string.assistant_workspaceId))
                            .input(input)
                            .build();

                    response = assistantService.message(options).execute().getResult();

                }
            });



            final String response_text = response.getOutput().getGeneric().get(0).getText();

            //System.out.println(response_text);

            // Set up Discovery

            IamOptions option = new IamOptions.Builder()
                    .apiKey(getString(R.string.discovery_apikey))
                    .build();

            Discovery discovery = new Discovery("2019-04-30", option);

            String environmentId = getString(R.string.discovery_environmentId);
            String collectionId = getString(R.string.discovery_collectionId);

            QueryOptions.Builder queryBuilder = new QueryOptions.Builder(environmentId, collectionId);
            queryBuilder.deduplicate(true);
            queryBuilder.passagesCharacters(700);
            queryBuilder.returnFields("Name , Guide");
            queryBuilder.query("Name:\"" + text + "\"");

            QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute().getResult();


            // The final result text to response

            if (!queryResponse.getPassages().isEmpty()) {
                if ( queryResponse.getResults().get(0).get("Name").toString().toLowerCase().equals(text.toLowerCase()) ) {
                    result = queryResponse.getResults().get(0).get("Guide").toString();
                }

                else
                {
                    if (queryResponse.getMatchingResults() == 1)
                    {
                        result = "Yes. Here is the exercise guide we can provide: " + queryResponse.getResults().get(0).get("Name").toString() + " ?";
                    }

                    else if (queryResponse.getMatchingResults() == 2)
                    {
                        result = "Yes. Here are some exercise guides we can provide: " + queryResponse.getResults().get(0).get("Name").toString() + " or "
                                + queryResponse.getResults().get(1).get("Name").toString() + " ?";
                    }

                    else
                    {
                        result = "Yes. Here are some exercise guides we can provide: \n" + "1) " + queryResponse.getResults().get(0).get("Name").toString() + "\n"
                                + "2) " + queryResponse.getResults().get(1).get("Name").toString() + "\n" +
                                "3) " + queryResponse.getResults().get(2).get("Name").toString() + "\n" + "or something else? ";
                    }
                }

            } else {
                result = response.getOutput().getGeneric().get(0).getText();;
            }

            if (result.contains("exercise guide")) {
                // Return the "done" message
                final Message response1 = new Message(result, data, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.add(response1);
                        messagesView.setSelection(messagesView.getCount() - 1);
                    }
                });

                mIfCounter = 0;
                editText.getText().clear();
            }

            else {
                array_of_steps = result.split("[0-9][.]", 50);
                for (int i = 0; i< array_of_steps.length; i++) {
                    System.out.println(array_of_steps[i]);
                    // If there are some newlines at the end, then remove them.
                    if (array_of_steps[i].indexOf('\n') != -1) {
                        int l = array_of_steps[i].length();
                        array_of_steps[i] = array_of_steps[i].substring(0, (l - 3));
                    }
                }

                size = array_of_steps.length;

                // case where it's the first response step
                if (mIfCounter == 0) {
                    result = array_of_steps[mIfCounter];

                    // replace "Steps" with directional message (if string contains "Steps : "
                    if (result.contains("Steps :")) {
                        //String result2 = result.replace("Steps :", "Type next for the Steps :");
                        String result2 = "Type NEXT for the steps: ";
                        result = result2;
                    }

                    // Return the text response
                    final Message response1 = new Message(result, data, false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.add(response1);
                            messagesView.setSelection(messagesView.getCount() - 1);
                        }
                    });

                    // case where there are more steps (messages) to be sent
                    if (size > 1) {
                        // special case - "how do i get there?" response
                        if (result.contains("John")) {
                            size = 1;
                            editText.getText().clear();
                        }

                        else {
                            mIfCounter++;
                            editText.getText().clear();
                        }

                    }
                    else {
                        editText.getText().clear();
                    }

                    // Make some voice if Voice Mode is enable
                    if (sw.isChecked()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                                            .text(result)
                                            .accept(SynthesizeOptions.Accept.AUDIO_WAV) // specifying that we want a WAV file
                                            .build();
                                    InputStream streamResult = textService.synthesize(synthesizeOptions).execute();

                                    StreamPlayer player = new StreamPlayer();
                                    player.playStream(streamResult); // should work like a charm
                                } catch (Exception e) {

                                }
                            }
                        }).start();


                    }
                }

                //case where there is more than 1 response
                if (size > 1) {
                    editText.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            //text = editText.getText().toString();

                            s2 = s.toString().replaceAll("\\s+", "");

                            if(s2.contains("next")) {
                                // case where we haven't reached end of the responses
                                if (mIfCounter < array_of_steps.length) {

                                    result = array_of_steps[mIfCounter];

                                    // replace "Steps" with directional message
                                    if (result.contains("Steps :")) {
                                        //String result2 = result.replace("Steps :", "Type next for the Steps :");
                                        String result2 = "Type NEXT for the steps: ";
                                        result = result2;
                                    }

                                    // replace "Tips" with directional message
                                    if (result.contains("Tips :")) {
                                        //String result2 = result.replace("Tips :", "Type next for some Tips :");
                                        String result2 = "Type NEXT for some tips: ";
                                        result = result2;
                                    }

                                    // case where it's not the first step/messages
                                    if (mIfCounter > 0 && !result.contains("tips")) {
                                        int j = mIfCounter;
                                        String res = Integer.toString(j);
                                        result = res + result;
                                    }

                                    // Return the text response
                                    final Message response1 = new Message(result, data, false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            messageAdapter.add(response1);
                                            messagesView.setSelection(messagesView.getCount() - 1);
                                        }
                                    });

                                    // Make some voice if Voice Mode is enable
                                    if (sw.isChecked()) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                                                            .text(result)
                                                            .accept(SynthesizeOptions.Accept.AUDIO_WAV) // specifying that we want a WAV file
                                                            .build();
                                                    InputStream streamResult = textService.synthesize(synthesizeOptions).execute();

                                                    StreamPlayer player = new StreamPlayer();
                                                    player.playStream(streamResult); // should work like a charm
                                                } catch (Exception e) {

                                                }
                                            }
                                        }).start();
                                    }
                                    editText.getText().clear();

                                    mIfCounter++;

                                    //sendMessage(view);
                                }
                                else {
                                    result = "DONE";

                                    // Return the "done" message
                                    final Message response1 = new Message(result, data, false);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            messageAdapter.add(response1);
                                            messagesView.setSelection(messagesView.getCount() - 1);
                                        }
                                    });

                                    mIfCounter = 0;
                                    editText.getText().clear();

                                    editText.removeTextChangedListener(this);
                                }
                            }
                        }
                    });
                }

            }

        }

    }




    private Assistant initAssistantService() {
        IamOptions iamOptions = new IamOptions.Builder()
                .apiKey(getString(R.string.assistant_apikey))
                .build();
        Assistant service = new Assistant("2019-02-28", iamOptions);
        service.setEndPoint(getString(R.string.assistant_url));
        return service;
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
                .inactivityTimeout(30000)
                .build();
    }



    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                showMicText(text);

                Boolean final_result = speechResults.getResults().get(0).isFinalResults();
                System.out.println("================================= " + final_result);
                if ((text.toLowerCase().indexOf("hi steve") != -1 || text.toLowerCase().indexOf("please") != -1 )&& final_result) {
                    sendText(text);
                }
            }
        }

        @Override
        public void onDisconnected() {
            microphoneHelper.closeInputStream();
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

    private void sendText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendMessage(null);
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
