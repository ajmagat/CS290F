package com.rao.tba;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ajmagat on 3/9/16.
 */
public class Notification {
    private String mType;
    private Location mLocation;
    private Date mDate;

    public Notification(String type, Location loc) {
        mType = type;
        mLocation = loc;
        mDate = new Date();
    }

    public Notification(String serializedNotification, String id) {
        List<String> partsList = new ArrayList<>(Arrays.asList(serializedNotification.split("!")));
        mType = partsList.get(0);

    }

    /**
     * @brief Return string describing this notifications
     * @return descriptive string
     */
    public String describe() {
        return mType;
    }

    public String toString() {
        String timeStamp = new SimpleDateFormat("MMdd_HHmm").format(mDate);
        return mType + "!" + timeStamp + "!" + mLocation.toString();
    }
}
