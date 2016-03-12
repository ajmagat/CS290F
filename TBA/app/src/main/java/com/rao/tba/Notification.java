package com.rao.tba;

import android.location.Location;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ajmagat on 3/9/16.
 */
public class Notification {
    private String mType;
    private Location mLocation;
    private Date mDate;

    public Notification(String type) {
        mType = type;
        mDate = new Date();
    }

    public Notification(String type, Location loc) {
        mType = type;
        mLocation = loc;
        mDate = new Date();
    }

    public Notification(String serializedNotification, String id) {
        List<String> partsList = new ArrayList<>(Arrays.asList(serializedNotification.split("!")));
        mType = partsList.get(0);
        DateFormat sdff = new SimpleDateFormat("MMdd_HHmm");
        try {
            mDate = sdff.parse(partsList.get(1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mLocation = null;
        if(mType.equals("Drop Pin")) {
            mLocation = new Location("");
            mLocation.setLatitude(Double.parseDouble(partsList.get(2)));
            mLocation.setLongitude(Double.parseDouble(partsList.get(3)));
        }
    }

    public Location getLocation() {
        return mLocation;
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
        System.out.println("In toString for notification");
        System.out.println(mType + "!" + timeStamp + "!" + mLocation.getLatitude() + "!" + mLocation.getLongitude());
        return mType + "!" + timeStamp + "!" + mLocation.getLatitude() + "!" + mLocation.getLongitude();
    }
}
