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
    private String mName;
    private String mType;
    private Location mLocation;
    private Date mDate;

    public Notification(String name, String type) {
        mName = name;
        mType = type;
        mDate = new Date();
        mLocation = new Location("");
    }

    public Notification(String name, String type, Location loc) {
        mName = name;
        mType = type;
        mLocation = loc;
        mDate = new Date();
    }

    public Notification(String serializedNotification, String id, boolean heh) {
        List<String> partsList = new ArrayList<>(Arrays.asList(serializedNotification.split("!")));
        mType = partsList.get(0);
        DateFormat sdff = new SimpleDateFormat("MMdd_HHmm");
        try {
            mDate = sdff.parse(partsList.get(1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mName = partsList.get(2);
        mLocation = null;
        if(mType.equals("Drop Pin")) {
            mLocation = new Location("");
            mLocation.setLatitude(Double.parseDouble(partsList.get(3)));
            mLocation.setLongitude(Double.parseDouble(partsList.get(4)));
        }
    }

    /**
     * @brief Accessor for mType
     * @return mType
     */
    public String getType() {
        return mType;
    }

    public Location getLocation() {
        return mLocation;
    }
    /**
     * @brief Return string describing this notifications
     * @return descriptive string
     */
    public String describe() {
        String timeStamp = new SimpleDateFormat("MM/dd HH:mm").format(mDate);
        return timeStamp + " : " + mName + "\n" + mType;
    }

    public String toString() {
        String timeStamp = new SimpleDateFormat("MMdd_HHmm").format(mDate);
        System.out.println("In toString for notification");
        String returnString = mType + "!" + timeStamp + "!" + mName;
        if (mType.equals("Drop Pin")) {
            returnString += "!" + mLocation.getLatitude() + "!" + mLocation.getLongitude();
        }

        return returnString;
    }
}
