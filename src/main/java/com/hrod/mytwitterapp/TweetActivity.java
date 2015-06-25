package com.hrod.mytwitterapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import twitter4j.Twitter;

/**
 * Interface to handle asynchronous responses from the tweet network request.
 */
interface TweetCallbacks {
    /**
     * Tweet response received
     *
     * @param success True if request was successful, false otherwise
     */
    void onTweetCompleted(boolean success);
}

public class TweetActivity extends AppCompatActivity implements TweetCallbacks {

    /**
     * The submit button
     */
    private Button mTweetButton;

    /**
     * The text box
     */
    private EditText mTweetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        //tell activity which toolbar to use
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize references to UI elements
        mTweetButton = (Button) findViewById(R.id.tweet_button);
        mTweetText = (EditText) findViewById(R.id.tweet_text);

        //set char counter to 140 initially
        final TextView charCountText = (TextView) findViewById(R.id.char_count_text);
        charCountText.setText("140");

        //set text change listener
        mTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //update char counter
                int charCount = s.length();
                charCountText.setText("" + (140 - charCount));

                //disable button if there is no text
                mTweetButton.setEnabled(charCount > 0);
            }
        });

        //set submit button click listener
        mTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notify the user
                Util.showMessage(TweetActivity.this, getString(R.string.sending_tweet_x, mTweetText.getText()));

                //get twitter handler
                Twitter twitter = TwitterManager.getTwitterInstance(TweetActivity.this);

                //launch async task to post the tweet
                new UpdateStatusTask(twitter, mTweetText.getText().toString(), TweetActivity.this).execute();
            }
        });
    }

    @Override
    public void onTweetCompleted(boolean success) {
        //set activity result and terminate it
        setResult(success ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public static class UpdateStatusTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * Injected callback handler
         */
        private TweetCallbacks mlistener;

        /**
         * Tweet text to send
         */
        private String statusText;

        /**
         * Twitter handler
         */
        private Twitter mTwitter;

        public UpdateStatusTask(Twitter twitter, String status, TweetCallbacks listener) {
            statusText = status;
            mlistener = listener;
            mTwitter = twitter;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                mTwitter.updateStatus(statusText);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mlistener.onTweetCompleted(success);
        }
    }
}