package com.hrod.mytwitterapp;

import android.app.Activity;
import android.content.SharedPreferences;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Helper class to abstract twitter credental storage/retrieval.
 */
public class TwitterManager {

    /**
     * Returns an initialised twitter instance for the signed in used.
     *
     * @param activity The current activity
     * @return Twitter handler
     */
    public static Twitter getTwitterInstance(Activity activity) {

        SharedPreferences preferences = activity.getSharedPreferences(Constants.PREFERENCES_NAME, Activity.MODE_PRIVATE);

        String token = preferences.getString(OAuth.OAUTH_TOKEN, "");
        String tokenSecret = preferences.getString(OAuth.OAUTH_TOKEN_SECRET, "");
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET);
        twitter.setOAuthAccessToken(accessToken);

        return twitter;
    }

    /**
     * Returns an initialised twitter instance for the signed in used.
     *
     * @param activity          The current activity.
     * @param accessToken       The user's access token
     * @param accessTokenSecret The user's access token
     */
    public static void setUserSignedIn(Activity activity, String accessToken, String accessTokenSecret) {
        final SharedPreferences.Editor edit = activity.getSharedPreferences(Constants.PREFERENCES_NAME, Activity.MODE_PRIVATE).edit();
        edit.putString(OAuth.OAUTH_TOKEN, accessToken);
        edit.putString(OAuth.OAUTH_TOKEN_SECRET, accessTokenSecret);
        edit.putBoolean(Constants.SIGNED_IN_PREF_KEY, true);
        edit.commit();
    }

    /**
     * Resets user's twitter credentials and marks him as signed out
     *
     * @param activity The current activity.
     */
    public static void setUserSignedOut(Activity activity) {
        //reset stored authentication data
        final SharedPreferences.Editor edit = activity.getSharedPreferences(Constants.PREFERENCES_NAME, Activity.MODE_PRIVATE).edit();
        edit.remove(OAuth.OAUTH_TOKEN);
        edit.remove(OAuth.OAUTH_TOKEN_SECRET);
        edit.putBoolean(Constants.SIGNED_IN_PREF_KEY, false);
        edit.commit();
    }
}
