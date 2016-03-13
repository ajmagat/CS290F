package com.rao.tba;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TransitionIntentService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private static final String NOTIFICATION_MAP_NAME = "NotificationMap";
    // Tag for logs
    protected static final String TAG = "TransitionIntentService";

    // Keep track of state across classes
    private static String previousState = "Unknown";
    private static String currentState = "Unknown";

    // Keep track of location
    private static Location sPreviousLocation = null;
    private static Location sCurrentLocation = null;

    // Google API for location service
    private GoogleApiClient mGoogleApi;

    // Handler for looping until location is returned
    private Handler mLocationHandler;

    // Use this to test a notification
    // Set to 0 to receive 1 notification
    // Set to anything else to not receive the test notification
    public static int TEST_INT = 0;

    /**
     * @brief Default constructor
     */
    public TransitionIntentService() {
        super("TransitionIntentService");

    }

    /**
     * @brief Callback from Google FusedLocationAPI for when location is determined via GPS
     * @param location
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "In here?");

        Log.w(TAG, location.toString());

        mLocationHandler.getLooper().quitSafely();
    }

    /**
     * @brief Callback from Google API for when we have successfully connected
     * @param connectionHint
     */
    @Override
         public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        Log.i(TAG, "GoogleAPI successfully connected");
    }

    /**
     * @brief Callback from Google API for when connection has failed
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * @brief Callback from Google API for when connection is suspended
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");

    }

    /**
     * @brief Used to get location using FusedLocationApi
     * @return current location
     */
    private Location getLocation() {
        // Check if we need to set up Google API
        if (mGoogleApi == null) {
            // Build API
            mGoogleApi = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            // Thank the 6 God for blockingConnect
            ConnectionResult what = mGoogleApi.blockingConnect();
        }

        // Prepare looper
        // Looper will be used to wait for location
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        // Create new handler
        mLocationHandler = new Handler();

        // Create new location request
        LocationRequest locRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locRequest.setInterval(5000);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions((Activity)getApplicationContext(),
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//
//        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApi, locRequest, this);

        Looper.loop();
        Location last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApi, this);

        return last;
    }


    /**
     * @brief Called by Google API when activity recogition update is available
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Check to see if the intent has any activity recognition results
        // Return if there are none
        if (! ActivityRecognitionResult.hasResult(intent)) {
            return;
        }

        // If there are results, extract them
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        // Loop through every activity
        for (DetectedActivity d : detectedActivities )
        {
            // Process if the detected activity has confidence over 70
            if ( d.getConfidence() > 70 )
            {

                // Get type of activity in string form
                String detectedType = Constants.getActivityString(getApplicationContext(), d.getType());
                Log.i(TAG, "Detected type: " + detectedType + " with confidence: " + d.getConfidence());


                // TEST FOR NOTIFICATION
                testNotification();

                Log.i(TAG, "currentState: " + currentState);

                // Check if the detected activity is for movement
//                if (!detectedType.equals("Still")) {
//                    // Check for location
//                    sPreviousLocation = sCurrentLocation;
//                    sCurrentLocation = getLocation();
//
//                    // Check if the distance between the current and last position is enough to signify movement
//                    if (sPreviousLocation != null && sCurrentLocation != null) {
//                        float distance = sCurrentLocation.distanceTo(sPreviousLocation);
//                        if (distance < Constants.MINIMUM_CHANGE_DISTANCE) {
//                            detectedType = "Still";
//                        }
//                    }
//                }

                Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

                localIntent.putExtra("Previous State", previousState);
                localIntent.putExtra("Current State", currentState);
                localIntent.putExtra("notif", false);


                // Check if the user has changed states
                if(!detectedType.equals(currentState)) {
                    previousState = currentState;
                    currentState = detectedType;

                    Log.e(TAG, "previousState: " + previousState);
                    Log.e(TAG, "currentState: " + currentState);

                    // Check if this change is part of a recipe
                    if(EditRecipesFragment.mRecipeList != null) {
                        for (Recipe r : EditRecipesFragment.mRecipeList) {
                            if (r.getIfList().contains(previousState) && r.getThenList().contains(currentState)) {
                                localIntent.putExtra("notif", true);
                                Notification temp = createNotification(r);
                                localIntent.putExtra("New Notification", temp.toString());
                            }
                        }
                    }
                }

                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }
    }

    /**
     * @brief Method to handle creation of notification
     */
    private Notification createNotification(Recipe triggered) {
        try {
            // Get shared preferences
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("RAOStore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String jsonString = prefs.getString(NOTIFICATION_MAP_NAME, (new JSONObject()).toString());
            JSONObject jsonObject = new JSONObject(jsonString);

            String action = triggered.getDoList().get(0);
            Log.i(TAG, "Found matching recipe. Performing action: " + action);


            // Create null notification
            Notification not = null;
            System.out.println("Action is : " + action);
            // Check what this action was
            if (action.equals("Drop Pin")) {
                // If drop pin, get location and create notification
                Log.e(TAG, "Creating notification");
                Location notifLoc = getLocation();

                not = new Notification(action, notifLoc);
            } else if (action.equals("Silence Phone")) {
                AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audio.setRingerMode(0);

                not = new Notification(action);
            }

            if (not != null) {
                // Add notification to shared preferences
                jsonObject.put(Integer.toString(jsonObject.length()), not.toString());
                editor.putString(NOTIFICATION_MAP_NAME, jsonObject.toString());
                editor.commit();
            }
            return not;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @brief Method to create test notification
     */
    private void testNotification() {
        try {
            if (TEST_INT != 0) {
                return;
            }
            TEST_INT = 1;
            Log.w(TAG, "About to do test notification for real");
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("RAOStore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String jsonString = prefs.getString(NOTIFICATION_MAP_NAME, (new JSONObject()).toString());
            JSONObject jsonObject = new JSONObject(jsonString);

            Location notifLoc = getLocation();
            if (notifLoc == null) {
                notifLoc = new Location("");
                notifLoc.setLatitude(34.416655);
                notifLoc.setLongitude(-119.845260);
            }

            Notification not = new Notification("Drop Pin", notifLoc);

            // Add notification to shared preferences
            jsonObject.put(Integer.toString(jsonObject.length()), not.toString());
            editor.putString(NOTIFICATION_MAP_NAME, jsonObject.toString());
            editor.commit();
            Log.w(TAG, "Just added test to shared preferences");
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
            localIntent.putExtra("New Notification", not.toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            Log.w(TAG, "Just broadcasted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
