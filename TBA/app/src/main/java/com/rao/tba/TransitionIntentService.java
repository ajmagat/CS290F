package com.rao.tba;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class TransitionIntentService extends IntentService implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final String NOTIFICATION_MAP_NAME = "NotificationMap";
    // Tag for logs
    protected static final String TAG = "TransitionIntentService";

    // Keep track of state across classes
    public static String previousState = "Unknown";
    public static String currentState = "Unknown";

    private static boolean sGPSIsOn = false;
    private static boolean sQuickGPSIsOn = false;

    // Keep track of location
    public static Location sPreviousLocation = null;
    public static Location sCurrentLocation = null;

    private static PendingIntent sLocationIntent;
    private static PendingIntent sPendingIntent;

    // Google API for location service
    private GoogleApiClient sGoogleApi;

    // Handler for looping until location is returned
    private Handler mLocationHandler;

    public static Lock mLocationLock = new ReentrantLock();

    // Use this to test a notification
    // Set to 0 to receive 1 notification
    // Set to anything else to not receive the test notification
    public static int TEST_INT = 1;

    public static boolean sInitialLocationSet = false;

    /**
     * @brief Default constructor
     */
    public TransitionIntentService() {
        super("TransitionIntentService");

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

    /**
     * @brief This function starts the GPS
     */
    private void startLocation(boolean quick) {
        // Check if we need to set up Google API
        if (! sGPSIsOn) {
            Log.e(TAG, "starting location?");
            if (sGoogleApi == null) {
                // Build API
                sGoogleApi = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                // Thank the 6 God for blockingConnect
                ConnectionResult what = sGoogleApi.blockingConnect();
            }

            if (sLocationIntent == null) {
                Intent intent = new Intent(this, InnerLocationService.class);
                sLocationIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            if (quick) {
                sQuickGPSIsOn = true;
            }

            long interval = quick ? 0 : 20000;
            LocationRequest locRequest = new LocationRequest();
            if (quick) {
                sQuickGPSIsOn = true;
                locRequest.setNumUpdates(2);
            }

            locRequest.setInterval(interval);
            locRequest.setFastestInterval(interval);
            locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(sGoogleApi, locRequest, sLocationIntent);
            sGPSIsOn = true;
        }
    }

    /**
     * @brief This function starts the GPS
     */
    private void stopLocation() {
        // Check if we need to set up Google API
        if (sGPSIsOn) {
            if (sGoogleApi == null) {
                // Build API
                sGoogleApi = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                // Thank the 6 God for blockingConnect
                ConnectionResult what = sGoogleApi.blockingConnect();
            }

            if (sLocationIntent == null) {
                Intent intent = new Intent(this, InnerLocationService.class);
                sLocationIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            if (! sQuickGPSIsOn ) {
                InnerLocationService.sPreviousLocation = null;
                InnerLocationService.sCurrentLocation = null;
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(sGoogleApi, sLocationIntent);
            sGPSIsOn = false;
            sQuickGPSIsOn = false;
        }
    }

    private Location getLocation() {
        // Check if we need to set up Google API
        if (sGoogleApi == null) {
            // Build API
            sGoogleApi = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            // Thank the 6 God for blockingConnect
            ConnectionResult block = sGoogleApi.blockingConnect();
        }

        Location last = LocationServices.FusedLocationApi.getLastLocation(sGoogleApi);

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

        if ( ! sInitialLocationSet || sCurrentLocation == null) {
            startLocation(true);
            sCurrentLocation = InnerLocationService.sCurrentLocation;
            if (sCurrentLocation != null) {
                stopLocation();
                sInitialLocationSet = true;
            }
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
                Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

                // Get type of activity in string form
                String detectedType = Constants.getActivityString(getApplicationContext(), d.getType());
                String originalDetectedType = detectedType;
                Log.w(TAG, "Detected type: " + detectedType + " with confidence: " + d.getConfidence());


                // Check if the detected activity is for movement
                if (!detectedType.equals("Still")) {

                    // Check for location
                    sPreviousLocation = InnerLocationService.sPreviousLocation;
                    sCurrentLocation = InnerLocationService.sCurrentLocation;
                    // Wait until there are at least two locations to check
                    if (sPreviousLocation == null || sCurrentLocation == null) {
                        Log.e(TAG, "Starting quick GPS");
                        startLocation(true);
                        return;
                    } else {
                        // Check if quick GPS is on, if so turn it off
                        Log.e(TAG, "Starting slower gps");
                        if (sQuickGPSIsOn) {
                            stopLocation();
                        }

                        // Turn on regular GPS
                        startLocation(false);
                    }


                    // Check if the distance between the current and last position is enough to signify movement
                    if (sPreviousLocation != null && sCurrentLocation != null) {
                        localIntent.putExtra("Difference", Float.toString(sCurrentLocation.distanceTo(sPreviousLocation)));

                        float distance = sCurrentLocation.distanceTo(sPreviousLocation);
                        if (distance < Constants.MINIMUM_CHANGE_DISTANCE) {
                            detectedType = "Still";
                        }
                    }
                }

                if (originalDetectedType.equals("Still") && sInitialLocationSet ) {
                    stopLocation();
                }

                if (sPreviousLocation != null) {
                    localIntent.putExtra("PLocation", sPreviousLocation.toString());
                }

                if (sCurrentLocation != null) {
                    localIntent.putExtra("CLocation", sCurrentLocation.toString());
                }

                previousState = currentState;
                currentState = detectedType;

                Log.w(TAG, "Current state is : " + currentState);

                localIntent.putExtra("Previous State", previousState);
                localIntent.putExtra("Current State", currentState);

                localIntent.putExtra("notif", false);


                // Check if the user has changed states
                if(!currentState.equals(previousState)) {
                    Log.e(TAG, "previousState: " + previousState);
                    Log.e(TAG, "currentState: " + currentState);

                    // Check if this change is part of a recipe
                    if(EditRecipesFragment.mRecipeList != null) {
                        for (Recipe r : EditRecipesFragment.mRecipeList) {
                            if (r.getIfList().contains(previousState) && r.getThenList().contains(currentState)) {
                                boolean inside = false;
                                if (r.getLocationList().size() > 0) {
                                    Log.e(TAG, "Checking geofences!!!!!!");
                                    for (LatLng l : r.getLocationList()) {
                                        if (sCurrentLocation == null) {
                                            Location t = new Location("");
                                            t.setLatitude(l.latitude);
                                            t.setLongitude(l.longitude);
                                            if (sCurrentLocation.distanceTo(t) <= Constants.GEOFENCE_RADIUS) {
                                                inside = true;
                                            }
                                        }
                                    }
                                } else if (r.getLocationList().size() == 1) {
                                    Log.e(TAG, "There are no geofences");
                                    inside = true;
                                }

                                if (inside) {
                                    localIntent.putExtra("notif", true);
                                    Notification temp = createNotification(r);
                                    localIntent.putExtra("New Notification", temp.toString());
                                }
                            }
                        }
                    }
                }

                if (sPreviousLocation != null) {
                    Log.w(TAG, "prev location " + sPreviousLocation.toString());
                }
                if (sCurrentLocation != null) {
                    Log.w(TAG, "continue location " + sCurrentLocation.toString());
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
            if (action.equals(Constants.DROP_PIN)) {
                // If drop pin, get location and create notification
                Log.e(TAG, "Creating notification");
                Location notifLoc = getLocation();

                not = new Notification(triggered.getName(), action, notifLoc);
            } else if (action.equals("Silence Phone")) {
                AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audio.setRingerMode(0);

                not = new Notification(triggered.getName(), action);
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

            Notification not = new Notification("asdf", Constants.DROP_PIN, notifLoc);

            // Add notification to shared preferences
            jsonObject.put(Integer.toString(jsonObject.length()), not.toString());
            editor.putString(NOTIFICATION_MAP_NAME, jsonObject.toString());
            editor.commit();
            Log.w(TAG, "Just added test to shared preferences");
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
            localIntent.putExtra("New Notification", not.toString());
            localIntent.putExtra("notif", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            Log.w(TAG, "Just broadcasted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}