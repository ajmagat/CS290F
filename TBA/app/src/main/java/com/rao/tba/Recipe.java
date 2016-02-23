package com.rao.tba;

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

    private String mName;

    /**
     * @brief Constructor for Recipe given lists for if, then, and do
     * @param ifList
     * @param thenList
     * @param doList
     * @param recipeName
     */
    public Recipe(ArrayList<String> ifList, ArrayList<String> thenList, ArrayList<String> doList, String recipeName) {
        mIfList = ifList;
        mThenList = thenList;
        mDoList = doList;
        mName = recipeName;
    }

    /**
     * @brief Constructor for Recipe given serialized version of Recipe
     * @param serializedRecipe
     * @param name
     */
    public Recipe(String serializedRecipe, String name) {
        List<String> partsList = new ArrayList<String>(Arrays.asList(serializedRecipe.split("!")));
        mIfList = new ArrayList<>(Arrays.asList(partsList.get(0).split("#")));
        mThenList = new ArrayList<>(Arrays.asList(partsList.get(1).split("#")));
        mDoList = new ArrayList<>(Arrays.asList(partsList.get(2).split("#")));

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
     * @brief Get serializable version of Recipe
     * @return returnString
     */
    public String toString() {
        String returnString = "";

        if ( mIfList.size() > 0 ) {
            for ( int i = 0; i < mIfList.size() - 1; i++ ) {
                returnString += mIfList.get(i) + "#";
            }
            returnString += mIfList.get(mIfList.size() - 1) + "!";
        }

        if ( mThenList.size() > 0 ) {
            for ( int i = 0; i < mThenList.size() - 1; i++ ) {
                returnString += mThenList.get(i) + "#";
            }
            returnString += mThenList.get(mThenList.size() - 1) + "!";
        }


        if ( mDoList.size() > 0 ) {
            for ( int i = 0; i < mDoList.size() - 1; i++ ) {
                returnString += mDoList.get(i) + "#";
            }

            returnString += mDoList.get(mDoList.size() - 1);
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
