package com.hrod.mytwitterapp;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {

    /**
     * Displays a message to the user
     *
     * @param context   Context to use for displaying message
     * @param messageId String id of the text to display
     */
    public static void showMessage(Context context, int messageId) {
        showMessage(context, context.getString(messageId));
    }

    /**
     * Displays a message to the user
     *
     * @param context Context to use for displaying message
     * @param message The text to display
     */
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        System.out.println(message);
    }


    /**
     * Loads a remote image into ImageView.
     *
     * @param mImageView View used to display the image
     * @param imageUrl   Url of the image to load
     */
    public static void loadImage(ImageView mImageView, String imageUrl) {

        Glide.with(mImageView.getContext()).load(imageUrl).into(mImageView);
    }


    /**
     * Returns a formatted string indicating time elapsed since date supplied.
     *
     * @param date The date used to calculate elapsed time until present.
     * @return Formatted string indicating elapsed time since date.
     */
    public static String getFormattedTimeSince(Date date) {
        return getFormattedTimeSince(date, new Date());
    }

    public static String getFormattedTimeSince(Date date, Date now) {
        //number of milliseconds elapsed
        long millisecondsElapsed = now.getTime() - date.getTime();

        //if more than one day, return number of days
        long days = TimeUnit.DAYS.convert(millisecondsElapsed, TimeUnit.MILLISECONDS);
        if (days >= 1)
            return days + "d";

        //if more than one hour, return number of hours
        long hours = TimeUnit.HOURS.convert(millisecondsElapsed, TimeUnit.MILLISECONDS);
        if (hours >= 1)
            return hours + "h";

        //if more than one minute, return number of minutes
        long minutes = TimeUnit.MINUTES.convert(millisecondsElapsed, TimeUnit.MILLISECONDS);
        if (minutes >= 1)
            return minutes + "m";

        //if less than one minute, return "now"
        return "now";
    }
}
