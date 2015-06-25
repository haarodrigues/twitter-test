package com.hrod.mytwitterapp;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class SignInActivityTest extends ActivityUnitTestCase<SignInActivity> {

    public SignInActivityTest() {
        super(SignInActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), HomeActivity.class);
        startActivity(mLaunchIntent, null, null);
    }

    /**
     * Tests request token retrieval.
     */
    @LargeTest
    public void testRetrieveRequestToken() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);

        //run on the UI thread
        runTestOnUiThread(new Runnable() {

            @Override
            public void run() {
                new SignInActivity.RetrieveRequestTokenTask(new LoginCallbacks() {
                    @Override
                    public void onRequestTokenReceived(boolean success, RequestToken mRequestToken) {

                        //check that token was received successfully
                        assertTrue("Retrieving request token failed", success);

                        //check that token is not null
                        assertNotNull("Request token is null", mRequestToken);

                        //check that correct activity is launched
                        getActivity().onRequestTokenReceived(success, mRequestToken);

                        Intent launchIntent = getStartedActivityIntent();

                        assertNotNull("No activity was lauched", launchIntent);

                        assertTrue("Incorrect activity launched", launchIntent.getAction().equals(Intent.ACTION_VIEW));

                        signal.countDown();
                    }

                    @Override
                    public void onAccessTokenReceived(boolean success, AccessToken mAccessToken) {
                        //requires user input, unable to test here
                    }
                }).execute();
            }
        });

        //wait until
        signal.await(5, TimeUnit.SECONDS);
    }

    /**
     * Tests if sign in process fails when request token is not retrieved
     */
    @SmallTest
    public void testSigninFailOnAccessTokenError() throws Throwable {

        getActivity().onAccessTokenReceived(false, new AccessToken("", ""));

        Intent launchIntent = getStartedActivityIntent();

        assertNull("Sign in went through after access token retrieval error", launchIntent);

    }

    /**
     * Tests if sign in process fails when request token is null
     */
    @SmallTest
    public void testSigninFailOnAccessTokenNull() throws Throwable {
        getActivity().onAccessTokenReceived(true, null);

        Intent launchIntent = getStartedActivityIntent();

        assertNull("Sign in went through with a null access token", launchIntent);
    }

    /**
     * Tests if sign in process succeeds after successfull request token retrieval
     */
    @SmallTest
    public void testSigninSuccess() throws Throwable {
        getActivity().onAccessTokenReceived(true, new AccessToken("", ""));

        Intent launchIntent = getStartedActivityIntent();

        assertNotNull("Sign in failed with valid credentials", launchIntent.getClass().equals(HomeActivity.class));
    }
}