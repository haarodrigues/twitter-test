package com.hrod.mytwitterapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Interface to handle asynchronous responses from the login network requests.
 */
interface LoginCallbacks {
    /**
     * Invoked after response for request token is received
     *
     * @param success       True if successfully retrieved, false otherwise
     * @param mRequestToken The request token
     */
    void onRequestTokenReceived(boolean success, RequestToken mRequestToken);


    /**
     * Invoked after response for access token is received
     *
     * @param success      True if successfully retrieved, false otherwise
     * @param mAccessToken The access token
     */
    void onAccessTokenReceived(boolean success, AccessToken mAccessToken);
}

/**
 * Provides Twitter authentication.
 */
public class SignInActivity extends Activity implements LoginCallbacks {

    /**
     * The retrieved request token
     */
    private RequestToken mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //set click listener for sign in button
        Button mEmailSignInButton = (Button) findViewById(R.id.twitter_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                doTwitterSignIn();
            }
        });
    }

    /**
     * Triggers twitter sign in process.
     */
    public void doTwitterSignIn() {
        RetrieveRequestTokenTask mAuthTask = new RetrieveRequestTokenTask(this);
        mAuthTask.execute();
    }

    @Override
    public void onRequestTokenReceived(boolean success, RequestToken requestToken) {

        if (success) {
            //store token
            mRequestToken = requestToken;

            //proceed with browser authorisation
            //result is handled in onNewIntent()
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mRequestToken.getAuthenticationURL()))
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_FROM_BACKGROUND);

            startActivity(intent);

        } else {
            //show error message
            Util.showMessage(this, R.string.signin_error);
        }

    }

    @Override
    public void onAccessTokenReceived(boolean success, AccessToken accessToken) {
        if (success && accessToken != null) {
            //store credentials and flag user as signed in
            TwitterManager.setUserSignedIn(this, accessToken.getToken(), accessToken.getTokenSecret());

            //proceed to home page
            Intent homeActivityIntent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(homeActivityIntent);

            //end this activity
            finish();

        } else {
            //show error message
            Util.showMessage(this, R.string.signin_error);
        }

    }

    /**
     * Handles the result of external browser authorisation
     *
     * @param intent The new intent with authorisation verification
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals("x-oauthflow-twitter")) {

            //get verification parameter
            String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

            if (oauth_verifier == null) {
                return;
            }

            //use oauth_verifier to retrieve access token
            RetrieveAccessTokenTask retrieveAccessTokenTask = new RetrieveAccessTokenTask(this, mRequestToken, oauth_verifier);
            retrieveAccessTokenTask.execute();

        } else {
            //terminate activity, wrong intent data
            finish();
        }
    }

    /**
     * Asynchronous task to retrieve login request token
     */
    public static class RetrieveRequestTokenTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * Injected callback handler
         */
        private LoginCallbacks mCallback;

        /**
         * Retrieved request token
         */
        private RequestToken mRequestToken;

        public RetrieveRequestTokenTask(LoginCallbacks callback) {
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //setup Twitter handler
            TwitterFactory factory = new TwitterFactory();
            Twitter twitter = factory.getInstance();
            twitter.setOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);

            try {
                mRequestToken = twitter.getOAuthRequestToken(
                        "x-oauthflow-twitter://testapp_callback");

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            //notify callback handler
            mCallback.onRequestTokenReceived(success, mRequestToken);
        }
    }


    /**
     * Asynchronous task to retrieve login access token
     */
    public static class RetrieveAccessTokenTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * Injected callback handler
         */
        private LoginCallbacks mCallback;

        /**
         * The
         */
        private String mOauthVerifier;

        /**
         * Request token to be used
         */
        private RequestToken mRequestToken;

        /**
         * Retrieved access token
         */
        private AccessToken mAccessToken;

        RetrieveAccessTokenTask(LoginCallbacks callback, RequestToken requestToken, String oauthVerifier) {
            mCallback = callback;
            mOauthVerifier = oauthVerifier;
            mRequestToken = requestToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                TwitterFactory factory = new TwitterFactory();
                Twitter twitter = factory.getInstance();
                twitter.setOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);

                mAccessToken = twitter.getOAuthAccessToken(mRequestToken, mOauthVerifier);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //notify callback handler
            mCallback.onAccessTokenReceived(success, mAccessToken);
        }
    }
}

