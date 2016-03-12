package com.rao.tba;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    // Google API for location service
    private GoogleApiClient mGoogleApi;

    // Handler for looping until location is returned
    private Handler mLocationHandler;

    // Going to need theses later to update notification list
    public static Object sNotificationLock = new Object();
    public static List<String> sNotificationList = new ArrayList<>();

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
        Log.e(TAG, "IN HERE");

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
        if (ActivityRecognitionResult.hasResult(intent)) {
            // If there are results, extract them
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            // Loop through every activity
            for (DetectedActivity d : detectedActivities )
            {
                if ( d.getConfidence() > 30 )
                {
                    SharedPreferences prefs = null;
                    SharedPreferences.Editor editor = null;
                    JSONObject jsonObject = null;
                    try {
                        prefs = getApplicationContext().getSharedPreferences("NotificationsStore", Context.MODE_PRIVATE);
                        editor = prefs.edit();
                        String jsonString = prefs.getString(NOTIFICATION_MAP_NAME, (new JSONObject()).toString());
                        jsonObject = new JSONObject(jsonString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Get type of activity in string
                    String ourType = Constants.getActivityString(getApplicationContext(), d.getType());
                    Log.e(TAG, "Our type: " + ourType + " with confidence: " + d.getConfidence());

                    // Check if user is moving
                    if(d.getConfidence() > 70 && !ourType.equals("Still")) {
                        // TODO: uhh do we need this here still?
                    }

                    // Check if the user has changed states
                    if(d.getConfidence() > 50 && !ourType.equals(currentState)) {
                        previousState = currentState;
                        currentState = ourType;

                        Log.e(TAG, "previousState: " + previousState);
                        Log.e(TAG, "currentState: " + currentState);

                        // Check if this change is part of a recipe
                        if(EditRecipesFragment.mRecipeList != null) {
                            for (Recipe r : EditRecipesFragment.mRecipeList) {
                                if (r.getIfList().contains(previousState) && r.getThenList().contains(currentState)) {
                                    String action = r.getDoList().get(0);

                                    Log.e(TAG, "Found matching recipe. Performing action: " + action);

                                    // Create null notification
                                    Notification not = null;

                                    // Check what this action was
                                    if (action.equals("Drop Pin")) {
                                        // If drop pin, get location and create notification
                                        Log.e(TAG, "Creating notification.");
                                        Location notifLoc = getLocation();
                                        not = new Notification(action, notifLoc);
                                    }

                                    try {
                                        if (not != null) {
                                            // Add notification to shared preferences
                                            jsonObject.put(Integer.toString(jsonObject.length()), not.toString());
                                            editor.putString(NOTIFICATION_MAP_NAME, jsonObject.toString());
                                            editor.commit();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
