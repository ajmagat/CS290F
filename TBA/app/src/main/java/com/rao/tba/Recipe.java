package com.rao.tba;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by romankazarin on 2/15/16.
 */
public class Recipe {
    private ArrayList<String> mIfList;
    private ArrayList<String> mThenList;
    private ArrayList<String> mDoList;
    private ArrayList<LatLng> mLocationList;
    private String mName;
    private boolean mOn;

    /**
     * @brief Constructor for Recipe given lists for if, then, and do
     * @param ifList
     * @param thenList
     * @param doList
     * @param locList
     * @param recipeName
     */
    public Recipe(ArrayList<String> ifList, ArrayList<String> thenList, ArrayList<String> doList, ArrayList<LatLng> locList, String recipeName) {
        mIfList = ifList;
        mThenList = thenList;
        mDoList = doList;
        mName = recipeName;
        mLocationList = locList;
        mOn = true;
    }

    /**
     * @brief Constructor for Recipe given serialized version of Recipe
     * @param serializedRecipe
     * @param name
     */
    public Recipe(String serializedRecipe, String name) {
        List<String> partsList = new ArrayList<>(Arrays.asList(serializedRecipe.split("!")));
        mOn = Boolean.valueOf(partsList.get(0));
        mIfList = new ArrayList<>(Arrays.asList(partsList.get(1).split("#")));
        mThenList = new ArrayList<>(Arrays.asList(partsList.get(2).split("#")));
        mDoList = new ArrayList<>(Arrays.asList(partsList.get(3).split("#")));

        if (partsList.size() == 5) {
            mLocationList = new ArrayList<>();
            List<String> tempPoints = new ArrayList<>(Arrays.asList(partsList.get(4).split("#")));

            for (String s : tempPoints) {
                Log.e("why", s);
                List<String> latAndLang = new ArrayList<>(Arrays.asList(s.split("q")));
                Log.e("why", latAndLang.toString());
                Log.e("why", s.split("$")[0]);
                double lat = Double.parseDouble(latAndLang.get(0));
                double lon = Double.parseDouble(latAndLang.get(1));
                mLocationList.add(new LatLng(lat, lon));
            }
        } else {
            mLocationList = new ArrayList<>();
        }

        mName = name;
    }

    /**
     * @brief Accessor method for recipe name
     * @return mName
     */
    public String getName() {
        return mName;
    }

    /**
     * @brief Mutator method for mOn
     * @param on
     */
    public void setOn(boolean on) {
        mOn = on;
    }

    /**
     * @brief Accessor method for mOn
     * @return mOn
     */
    public boolean getOn() {
        return mOn;
    }
    /**
     * @brief Accessor method for if list
     * @return mIfList
     */
    public ArrayList<String> getIfList() {
        return mIfList;
    }

    /**
     * @brief Accesor method for then list
     * @return mThenList
     */
    public ArrayList<String> getThenList() {
        return mThenList;
    }

    /**
     * @brief Accessor method for do list
     * @return mDoList
     */
    public ArrayList<String> getDoList() {
        return mDoList;
    }

    /**
     * @brief Accessor method for location list
     * @return mLocationList
     */
    public ArrayList<LatLng> getLocationList() {
        return mLocationList;
    }

    /**
     * @brief Get serializable version of Recipe
     * @return returnString
     */
    public String toString() {
        String returnString = Boolean.toString(mOn) + "!";

        if ( mIfList.size() > 0 ) {
            for ( int i = 0; i < mIfList.size() - 1; i++ ) {
                returnString += mIfList.get(i) + "#";
            }
            returnString += mIfList.get(mIfList.size() - 1);
        }

        returnString += "!";

        if ( mThenList.size() > 0 ) {
            for ( int i = 0; i < mThenList.size() - 1; i++ ) {
                returnString += mThenList.get(i) + "#";
            }
            returnString += mThenList.get(mThenList.size() - 1);
        }

        returnString += "!";


        if ( mDoList.size() > 0 ) {
            for ( int i = 0; i < mDoList.size() - 1; i++ ) {
                returnString += mDoList.get(i) + "#";
            }

            returnString += mDoList.get(mDoList.size() - 1);
        }

        returnString += "!";

        if ( mLocationList.size() > 0 ) {
            LatLng temp;
            for ( int i = 0; i < mLocationList.size() - 1; i++ ) {
                temp = mLocationList.get(i);
                returnString += Double.toString(temp.latitude) + "q" + Double.toString(temp.longitude) + "#";
            }

            temp = mLocationList.get(mLocationList.size() - 1);
            returnString += Double.toString(temp.latitude) + "q" + Double.toString(temp.longitude);
        }

        return returnString;
    }

    /**
     * @brief Get human readable version of Recipe
     * @return returnString
     */
    public String toReadableString(){
        String returnString = "";
        if ( mIfList.size() > 0 ) {
            returnString += "IF: " + mIfList.get(0) + "... ";
        }

        if ( mThenList.size() > 0 ) {
            returnString += "THEN: " + mThenList.get(0) + "..., ";
        }

        if ( mDoList.size() > 0 ) {
            returnString += "DO: " + mDoList.get(0) + "...";
        }
        return returnString;
    }
}
