package com.example.android.popmovies.utils;

import junit.framework.Assert;

import java.util.concurrent.Callable;

/**
 * Created by sheshloksamal on 16/03/16.
 *
 */
public abstract class PollingCheck {

    private static final long TIME_SLICE = 50;
    private long mTimeOut = 3000;

    public PollingCheck(){}

    public PollingCheck(long timeout) {
        this.mTimeOut = timeout;
    }

    protected abstract boolean check();

    public void run(){
        if (check()){
            return;
        }

        long timeout = mTimeOut;
        while (timeout > 0) {
            try {
                Thread.sleep(TIME_SLICE);
            } catch (InterruptedException e) {
                Assert.fail("Unexpected InterruptedException");
            }

            if (check()){
                return;
            }

            timeout -= TIME_SLICE;
        }

        Assert.fail("Unexpected timeout");
    }

    public static void check(CharSequence message, long timeout, Callable<Boolean> condition)
    throws Exception {

        while(timeout > 0) {
            if (condition.call()){
                return;
            }

            Thread.sleep(TIME_SLICE);
            timeout -= TIME_SLICE;
        }

        Assert.fail(message.toString());

    }
}
