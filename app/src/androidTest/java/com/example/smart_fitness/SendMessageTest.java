package com.example.smart_fitness;

import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
import android.widget.ListView;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v1.Assistant;
import com.ibm.watson.assistant.v1.model.MessageInput;
import com.ibm.watson.assistant.v1.model.MessageOptions;
import com.ibm.watson.assistant.v1.model.MessageResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests UI send message activity
 */
@RunWith(AndroidJUnit4.class)
public class SendMessageTest {
    private static final String STRING_TO_BE_TYPED = "Hello World!";

    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private MemberData data = new MemberData("Steve", "#bb0000");

    // tests if message sent is what appears on screen
    @Test
    public void changeText() {
        // Type text and then press the button.
        onView(withId(R.id.editText)).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.send_button)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.message_body)).check(matches(withText(STRING_TO_BE_TYPED)));
    }

    // tests message response to random input
    @Test
    public void randomInputResponseTest() {

        String text = "what's up?";

        if (text.length() > 0) {

            // We provide a question here!
            final Message message = new Message(text, data, true);
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.add(message);
                        messagesView.setSelection(messagesView.getCount() - 1);
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
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

            String response_text = response.getOutput().getGeneric().get(0).getText();

            String expected_response_text = "I didn't understand. You can try rephrasing";

            assertThat(response_text, is(expected_response_text));

        }

    }

}
