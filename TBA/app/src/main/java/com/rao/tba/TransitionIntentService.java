package com.rao.tba;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;


public class TransitionIntentService extends IntentService {
    static int mNotificationId;

    protected static final String TAG = "TransitionIntentService";
    private String previousState = "Unknown";
    private String currentState = "Unknown";

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
            //localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            Log.e("A-Fresh", "end this");

            for (DetectedActivity d : detectedActivities )
            {
                System.out.println(d.toString() + " with confidence " + d.getConfidence());
                if ( d.getConfidence() > 30 )
                {

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
                    notification.setContentTitle("TBA").setContentInfo("Activity " + d.toString() + " with confidence " + d.getConfidence());
                    notification.setSmallIcon(R.drawable.common_signin_btn_icon_dark);

                    mNotificationId++;
                    System.out.println("Notification " + mNotificationId);
                    if (mNotificationId % 5 == 0 )
                    {
                        System.out.println("about to send notification " + mNotificationId);
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        mNotifyMgr.notify(mNotificationId, notification.build());
                    }


                    Toast.makeText(getApplicationContext(), "A-fuckboy Got: " + d.toString() + " with confidence " + Integer.toString(d.getConfidence()), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "A-fuckboy Got: " + d.toString() + " Of type: " + d.getType());
                    String ourType = Constants.getActivityString(getApplicationContext(), d.getType());
                    Log.e(TAG, "Our type: " + ourType);

                    if(d.getConfidence() > 70 && !d.toString().equals(currentState)) {


                        Toast.makeText(getApplicationContext(), "Changing current activity to: " + ourType, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Changing current activity to: " + ourType);

                        previousState = currentState;
                        currentState = ourType;

                        for(Recipe r : EditRecipesFragment.mRecipeList) {
                            if(r.getIfList().contains(previousState) && r.getThenList().contains(currentState)) {
                                String action = r.getDoList().get(0);

                                Toast.makeText(getApplicationContext(), "Found matching recipe. Performing action: " + action, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Found matching recipe. Performing action: " + action);

                                // dropPinHereBrah();
                            }
                        }
                    }

                }
            }
        }
    }
}
