package com.hrod.mytwitterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import twitter4j.Twitter;


/**
 * HomeActivity displays the contents of the user's Home timeline.
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * Request code sent to TweetActivity so its result can be determined.
     */
    private static final int REQUEST_CODE_TWEET = 0;

    /**
     * Reference to the SwipeLayout so refresh status can be updated.
     */
    private SwipeRefreshLayout swipeLayout;

    /**
     * Reference to RecyclerView containing the list of tweets.
     */
    private RecyclerView mTweetsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //determine if user has signed in to Twitter
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES_NAME, MODE_PRIVATE);
        boolean userIsSignedIn = preferences.getBoolean(Constants.SIGNED_IN_PREF_KEY, false);

        if (!userIsSignedIn) {
            //user is not signed in, finish this activity and open SignInActivity instead
            showSignInActivity();
            return;
        }

        //user has signed in, continue initializing activity

        setContentView(R.layout.activity_home);

        //tell activity which toolbar to use
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize reference to SwipeRefreshLayout and set refresh listener
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTweets();
            }
        });

        //initialize reference to RecyclerView holding the tweets
        mTweetsRecyclerView = (RecyclerView) findViewById(R.id.tweets_recycler_view);

        // set layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mTweetsRecyclerView.setLayoutManager(mLayoutManager);

        swipeLayout.setRefreshing(true);

        loadTweets();
    }

    /**
     * Requests tweets on the Home timeline for the logged in user.
     */
    public void loadTweets() {

        //start task to load timeline
        FetchTimelineTask fetchTimelineTask = new FetchTimelineTask();
        fetchTimelineTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //id of the selected item
        int id = item.getItemId();

        //TWEET button selected
        if (id == R.id.action_tweet) {

            //start TweetActivity, its result is processed in onActivityResult()
            Intent tweetActivityIntent = new Intent(this, TweetActivity.class);
            startActivityForResult(tweetActivityIntent, REQUEST_CODE_TWEET);
            return true;
        }

        //LOGOUT button selected
        else if (id == R.id.action_logout) {

            TwitterManager.setUserSignedOut(this);

            //open SignInActivity
            showSignInActivity();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts SignInActivity and terminates this one.
     */
    private void showSignInActivity() {
        Intent signInActivityIntent = new Intent(this, SignInActivity.class);
        startActivity(signInActivityIntent);
        finish();
    }

    /**
     * Handles result of TweetActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if this is a result for the TweetActivity we started
        if (requestCode == REQUEST_CODE_TWEET) {

            if (resultCode == RESULT_OK) {
                //status update was successful, reload tweet list
                loadTweets();
            } else {
                //an error occurred, tell the user
                Util.showMessage(this, R.string.status_update_error);
            }
        }
    }

    /**
     * Asynchronous task to fetch the user's Home timeline contents.
     * Updates UI components upon completion.
     */
    public class FetchTimelineTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * List holding the status results
         */
        private List<twitter4j.Status> mStatuses;

        @Override
        protected Boolean doInBackground(Void... params) {

            //retrieve twitter instance
            Twitter twitter = TwitterManager.getTwitterInstance(HomeActivity.this);

            try {

                //fetch the user's timeline, the 20 most recent results are returned
                mStatuses = twitter.getHomeTimeline();

            } catch (Exception e) {
                //an error occurred
                e.printStackTrace();
                return false;
            }

            //executed successfully
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //update UI components with the retrieved items
            swipeLayout.setRefreshing(false);
            mTweetsRecyclerView.setAdapter(new HomeTimelineAdapter(mStatuses));

            //if there was an error, show it to the user
            if (!success)
                Util.showMessage(HomeActivity.this, R.string.timeline_error);
        }
    }
}
