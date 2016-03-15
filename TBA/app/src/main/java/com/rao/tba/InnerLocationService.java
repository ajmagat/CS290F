package com.rao.tba;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

public class InnerLocationService extends IntentService {
    public static Object sLocationLock = new Object();
    public static Location sLatestLocation;

    public InnerLocationService() {
        super("InnerLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("LOCATION STUFF", "in here");
        if (intent == null) {
            Log.e("LOCATION STUFF", "why you null");
        } else {
            //Log.e("LOCATION STUFF", intent.getExtras().toString());
            for (String key : intent.getExtras().keySet()) {
                Log.w("location", "key: " + key);
            }
        }

        if (! LocationResult.hasResult(intent)) {
            Log.e("LOCATION STUFF", "nothing in");

            synchronized (sLocationLock) {
                sLocationLock.notifyAll();
            }
            return;
        }

        Log.e("LOCATION STUFF", "trying to do stuff");
        LocationResult locationResult = LocationResult.extractResult(intent);
        sLatestLocation = locationResult.getLastLocation();
        synchronized (sLocationLock) {
            sLocationLock.notifyAll();
        }
    }
}
