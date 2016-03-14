package com.rao.tba;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
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
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class TransitionIntentService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    private static final String NOTIFICATION_MAP_NAME = "NotificationMap";
    // Tag for logs
    protected static final String TAG = "TransitionIntentService";

    // Keep track of state across classes
    private static String previousState = "Unknown";
    private static String currentState = "Unknown";

    private static int p = 0;
    // Keep track of location
    private static Location sPreviousLocation = null;
    private static Location sCurrentLocation = null;

    private static PendingIntent sLocationIntent;
    // Google API for location service
    private GoogleApiClient mGoogleApi;

    // Handler for looping until location is returned
    private Handler mLocationHandler;

    public Object mLocationLock;
    private static final String ACTION_LOCATION_UPDATED = "location_updated";
    private static final String ACTION_REQUEST_LOCATION = "request_location";

    // Use this to test a notification
    // Set to 0 to receive 1 notification
    // Set to anything else to not receive the test notification
    public static int TEST_INT = 1;

    /**
     * @brief Default constructor
     */
    public TransitionIntentService() {
        super("TransitionIntentService");
        mLocationLock = new Object();

    }

    /**
     * @param location
     * @brief Callback from Google FusedLocationAPI for when location is determined via GPS
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged " + location.toString());

        if (mLocationHandler.getLooper().getThread().isAlive()) {
            mLocationHandler.getLooper().quitSafely();
        }
    }

    /**
     * @param connectionHint
     * @brief Callback from Google API for when we have successfully connected
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        Log.i(TAG, "GoogleAPI successfully connected");
     /*   LocationRequest locRequest = new LocationRequest();
        locRequest.setInterval(0);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi, locRequest, sLocationIntent);*/
    }

    /**
     * @param result
     * @brief Callback from Google API for when connection has failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * @param cause
     * @brief Callback from Google API for when connection is suspended
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
    }

    private void onLocationUpdated(Intent intent) {
        Log.v(TAG, ACTION_LOCATION_UPDATED);

        // Extra new location
        Location location =
                intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        Log.i(TAG, "swag");
    }

    private Location getLocation2() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        ConnectionResult connectionResult = googleApiClient.blockingConnect(10, TimeUnit.SECONDS);
        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {

            Intent locationUpdatedIntent = new Intent(this, TransitionIntentService.class);
            locationUpdatedIntent.setAction(ACTION_LOCATION_UPDATED);

            // Send last known location out first if available
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                Intent lastLocationIntent = new Intent(locationUpdatedIntent);
                lastLocationIntent.putExtra(
                        FusedLocationProviderApi.KEY_LOCATION_CHANGED, location);
                startService(lastLocationIntent);
            }

            // Request new location
            LocationRequest mLocationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, mLocationRequest,
                    PendingIntent.getService(this, 0, locationUpdatedIntent, 0));

            googleApiClient.disconnect();
        } else {
            Log.e(TAG, String.format("Failed to connect to GoogleApiClient (error code = %d)",
                    connectionResult.getErrorCode()));
        }
        return null;
    }

    /**
     * @brief Used to get location using FusedLocationApi
     * @return current location
     */
 /*   private Location getLocation() {
        Log.i(TAG, "Trying to get location");
        // Check if we need to set up Google API
        if (mGoogleApi == null) {
            // Build API
            mGoogleApi = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            if (sLocationIntent == null) {
                Intent intent = new Intent(this, InnerLocationService.class);
                sLocationIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            // Thank the 6 God for blockingConnect
            ConnectionResult what = mGoogleApi.blockingConnect();
        }

        synchronized (InnerLocationService.sLocationLock) {
            Log.e(TAG, "waiting for an empty location");
            try {
                InnerLocationService.sLocationLock.wait();
                Log.e(TAG, "finished the first wait");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//;        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApi, sLocationIntent);
        return InnerLocationService.sLatestLocation;
        // Prepare looper
        // Looper will be used to wait for location
/*        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        Log.i(TAG, "1");
        // Create new handler
        mLocationHandler = new Handler();
        Log.i(TAG, "2");
        // Create new location request
        LocationRequest locRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locRequest.setInterval(5000);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        Log.i(TAG, "3");
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

        //Looper.loop();
        try {
            synchronized (mLocationLock) {
                Log.i(TAG, "Waiting for location");
                mLocationLock.wait();
                Log.i(TAG, "Done location");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApi);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApi, this);
        Log.i(TAG, "5");
        return last;
}*/
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
        locRequest.setInterval(0);
        locRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        Log.e(TAG, "About to wait for update");
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
                Log.w(TAG, "Detected type: " + detectedType + " with confidence: " + d.getConfidence());

                // Check if the detected activity is for movement
                if (!detectedType.equals("Still")) {
                    // Check for location
                    Log.w(TAG, "Checking gps");
                    sPreviousLocation = sCurrentLocation;
                    sCurrentLocation = getLocation();
                    Log.e(TAG, "Location is currently " + sCurrentLocation);
                    if (sCurrentLocation == null) {
                        Log.e(TAG, "shit was null");
                    } else {
                        Log.e(TAG, "SQUAD UP");
                    }

                    Log.w(TAG, "Got location");
                    // Check if the distance between the current and last position is enough to signify movement
                    if (sPreviousLocation != null && sCurrentLocation != null) {
                        float distance = sCurrentLocation.distanceTo(sPreviousLocation);
                        if (distance < Constants.MINIMUM_CHANGE_DISTANCE) {
                            detectedType = "Still";
                        }
                    }
                }
                Log.e(TAG, "detected type after gps check: " + detectedType);
                Log.w(TAG, "current state is : " + currentState);
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
