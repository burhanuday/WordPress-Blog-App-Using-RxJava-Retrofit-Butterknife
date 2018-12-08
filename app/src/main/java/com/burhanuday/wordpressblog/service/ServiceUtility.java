package com.burhanuday.wordpressblog.service;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by burhanuday on 08-12-2018.
 */
public class ServiceUtility {

    private static final int REMINDER_INTERVAL_MINUTES = 60*3;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SEONDS = 60;

    private static final String TAG = ServiceUtility.class.getSimpleName();
    private static boolean sInitialised;

    synchronized public static void scheduleChargingReminder(final Context context){
        if (sInitialised) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(GetLatestPostService.class)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS, REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SEONDS
                ))
                .setReplaceCurrent(true)
                .setTag(TAG)
                .build();
        dispatcher.schedule(constraintReminderJob);
        sInitialised = true;
    }
}
