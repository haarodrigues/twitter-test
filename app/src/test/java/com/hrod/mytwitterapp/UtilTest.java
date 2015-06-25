package com.hrod.mytwitterapp;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by hugorodrigues on 23/06/2015.
 */
public class UtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testGetFormattedTimeSince() throws Exception {
        Date now = new Date();
        Date tenSecondsAgo = new Date(now.getTime() - 10000);
        assertEquals("Unexpected result", Util.getFormattedTimeSince(tenSecondsAgo, now), "now");

        Date aMinuteAgo = new Date(now.getTime() - (60 * 1000 + 1));
        assertEquals("Unexpected result", Util.getFormattedTimeSince(aMinuteAgo, now), "1m");

        Date anHourAgo = new Date(now.getTime() - (60 * 60 * 1000 + 1));
        assertEquals("Unexpected result", Util.getFormattedTimeSince(anHourAgo, now), "1h");

        Date aDayAgo = new Date(now.getTime() - (24 * 60 * 60 * 1000 + 1));
        assertEquals("Unexpected result", Util.getFormattedTimeSince(aDayAgo, now), "1d");
    }
}