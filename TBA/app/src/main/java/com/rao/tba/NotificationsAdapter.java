package com.rao.tba;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by ajmagat on 2/23/16.
 */
public class NotificationsAdapter extends ArrayAdapter<DetectedActivity> {

    public NotificationsAdapter(Context context, ArrayList<DetectedActivity> detectedActivities) {
        super(context, 0, detectedActivities);
    }

    protected void updateActivities(ArrayList<DetectedActivity> detectedActivities) {

    }
}
