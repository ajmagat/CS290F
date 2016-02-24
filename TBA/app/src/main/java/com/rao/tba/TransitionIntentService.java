package com.rao.tba;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;


public class TransitionIntentService extends IntentService {
    public TransitionIntentService() {
        super("TransitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            // Log each activity.
            Log.i("A-Fresh", "activities detected");
            for (DetectedActivity da: detectedActivities) {
                Log.i("A-Fresh", Constants.getActivityString(
                                getApplicationContext(),
                                da.getType()) + " " + da.getConfidence() + "%"
                );
            }

            Log.e("A-Fresh", "about to do this");
            // Broadcast the list of detected activities.
            localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            Log.e("A-Fresh", "end this");
        }
    }
}
