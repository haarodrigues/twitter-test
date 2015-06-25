package com.hrod.mytwitterapp;

import android.content.Intent;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import twitter4j.Twitter;

/**
 * Created by hugorodrigues on 25/06/2015.
 */
public class TweetActivityTest extends ActivityUnitTestCase<TweetActivity> {

    private EditText tweetText;
    private Button tweetButton;

    public TweetActivityTest() {
        super(TweetActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);

        Intent mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), TweetActivityTest.class);
        startActivity(mLaunchIntent, null, null);

        tweetText = (EditText) getActivity().findViewById(R.id.tweet_text);
        tweetButton = (Button) getActivity().findViewById(R.id.tweet_button);
    }

    /**
     * Tests if submit button is disabled when no status is entered
     */
    @SmallTest
    public void testButtonDisabledWithEmptyStatus() throws Throwable {
        tweetText.setText("");

        assertFalse("Tweet button enabled with empty tweet", tweetButton.isEnabled());
    }

    /**
     * Tests if submit button is enabled when a valid status is entered
     */
    @SmallTest
    public void testButtonEnabledWithValid() throws Throwable {
        tweetText.setText("a");

        assertTrue("Tweet button disabled but tweet was valid", tweetButton.isEnabled());
    }

    /**
     * Tests if text is trimmed to 140 characters
     */
    @SmallTest
    public void testLongTextIsTrimmed() throws Throwable {
        System.out.println(":1:" + tweetText.getText().length());
        tweetText.setText(
                "12345678901234567890" +
                        "12345678901234567890" +
                        "12345678901234567890" +
                        "12345678901234567890" +
                        "12345678901234567890" +
                        "12345678901234567890" +
                        "123456789012345678901");

        System.out.println(":2:" + tweetText.getText().length());
        assertTrue("Text over 140 characters was not trimmed", tweetText.getText().length() == 140);
    }

    /**
     * Tests if submitting an invalid tweet returns error response
     */
    @LargeTest
    public void testInvalidTweetFails() throws Throwable {

        // create  a signal to let us know when our task is done.
        final CountDownLatch signal = new CountDownLatch(1);

        // Execute the async task on the UI thread!
        runTestOnUiThread(new Runnable() {

            @Override
            public void run() {
                Twitter twitter = TwitterManager.getTwitterInstance(getActivity());

                new TweetActivity.UpdateStatusTask(twitter, "", new TweetCallbacks() {
                    @Override
                    public void onTweetCompleted(boolean success) {
                        assertFalse("Invalid tweet returned success", success);

                        signal.countDown();
                    }
                }).execute();
            }
        });

        //wait until
        signal.await(3, TimeUnit.SECONDS);
    }
}