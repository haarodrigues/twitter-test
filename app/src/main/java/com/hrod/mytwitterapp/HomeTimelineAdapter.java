package com.hrod.mytwitterapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import twitter4j.Status;

/**
 * Adapter for the list of tweets/statuses.
 */
public class HomeTimelineAdapter extends RecyclerView.Adapter<HomeTimelineAdapter.StatusViewHolder> {

    private List<Status> mDataset;

    /**
     * Initializes adapter with supplied status list.
     *
     * @param statusList The list of Status items to display
     */
    public HomeTimelineAdapter(List<Status> statusList) {
        mDataset = statusList;
    }

    /**
     * Creates new views, invoked by LayoutManager.
     *
     * @param parent   Parent ViewGroup
     * @param viewType Type of view, always the same in this case
     * @return StatusViewHolder for the inflated view
     */
    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_item_layout, parent, false);

        StatusViewHolder vh = new StatusViewHolder(v);
        return vh;
    }

    /**
     * Sets the contents of the view, invoked by LayoutManager.
     *
     * @param holder   The StatusViewHolder with references to views
     * @param position Position of the item being populated
     */
    @Override
    public void onBindViewHolder(StatusViewHolder holder, int position) {
        //retrieve correct item from dataset
        Status data = mDataset.get(position);

        //populate views
        holder.mStatusText.setText(data.getText());
        holder.mNameText.setText(data.getUser().getName());
        holder.mTimeText.setText(Util.getFormattedTimeSince(data.getCreatedAt()));
        holder.mTwitterHandleText.setText("@" + data.getUser().getScreenName());

        Util.loadImage(holder.mAvatarImage, data.getUser().getBiggerProfileImageURL());

    }

    /**
     * Invoked by LayoutManager, returns the size of the dataset.
     *
     * @return Number of items in dataset
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Stores references to the views for each of the items on the list
     */
    public static class StatusViewHolder extends RecyclerView.ViewHolder {

        /**
         * The status text
         */
        public TextView mStatusText;

        /**
         * The user's name
         */
        public TextView mNameText;

        /**
         * The user's twitter handle
         */
        public TextView mTwitterHandleText;

        /**
         * Time of post
         */
        public TextView mTimeText;

        /**
         * The user's avatar
         */
        public ImageView mAvatarImage;

        /**
         * Constructor initializes references to all the child views
         *
         * @param rootView The parent view containing all other views.
         */
        public StatusViewHolder(View rootView) {
            super(rootView);

            mStatusText = (TextView) rootView.findViewById(R.id.tweet_text);
            mNameText = (TextView) rootView.findViewById(R.id.name_text);
            mTwitterHandleText = (TextView) rootView.findViewById(R.id.handle_text);
            mTimeText = (TextView) rootView.findViewById(R.id.date_text);
            mAvatarImage = (ImageView) rootView.findViewById(R.id.avatar_image);
        }
    }
}