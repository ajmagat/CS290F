package com.rao.tba;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

public class InnerLocationService extends IntentService {
    public static Location sPreviousLocation = null;
    public static Location sCurrentLocation = null;

    public InnerLocationService() {
        super("InnerLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (! LocationResult.hasResult(intent)) {
            return;
        }
        Log.e("Map", "Results found for location");
        LocationResult locationResult = LocationResult.extractResult(intent);

        sPreviousLocation = sCurrentLocation;
        sCurrentLocation = locationResult.getLastLocation();
    }
}
