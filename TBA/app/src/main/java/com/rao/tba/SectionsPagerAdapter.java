package com.rao.tba;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rao.tba.EditRecipesFragment;
import com.rao.tba.NotificationsFragment;
import com.rao.tba.Recipe;
import com.rao.tba.RecipeFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragmentArray;
    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        fragmentArray = new Fragment[3];
        ArrayList<Recipe> recipeList = new ArrayList<Recipe>();

        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("RecipeStore", Context.MODE_PRIVATE);

        String jsonString = prefs.getString("RecipeMap", (new JSONObject()).toString());
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                String value = (String) jsonObject.get(key);
                recipeList.add(new Recipe(value, key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentArray[0] = NotificationsFragment.newInstance(1);
        fragmentArray[1] = RecipeFragment.newInstance(1, recipeList);
        fragmentArray[2] = EditRecipesFragment.newInstance(3, recipeList);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        System.out.println("Get item called");
        if ( position == 0 ) {
            return fragmentArray[0];
        } else if ( position == 1 ) {
            return fragmentArray[1];
        } else if( position == 2 ) {
            return fragmentArray[2];
        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return fragmentArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}