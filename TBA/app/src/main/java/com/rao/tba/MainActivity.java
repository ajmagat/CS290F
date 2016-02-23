package com.rao.tba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements RecipeFragment.OnListFragmentInteractionListener {
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onPause() {
        super.onPause();

        List<Fragment> allFrags = getSupportFragmentManager().getFragments();

        if (allFrags.size() < 2) {
            return;
        }

        final RecipeFragment temp = (RecipeFragment) allFrags.get(1);
        temp.updateRecipeList();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            /**
             * @brief Method to figure out which fragment we are currently on
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                List<Fragment> allFrags = getSupportFragmentManager().getFragments();

                if (position == 0) {
                    setTitle("Notifications");
                } else if (position == 1) {
                    setTitle("My Recipes");
                    RecipeFragment temp = (RecipeFragment) allFrags.get(1);
                } else if (position == 2) {
                    setTitle("Edit Recipe");
                    EditRecipesFragment temp = (EditRecipesFragment) allFrags.get(2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //Add demo notification
            List<Fragment> allFrags = getSupportFragmentManager().getFragments();
            NotificationsFragment notiFrag= (NotificationsFragment) allFrags.get(0);
            TextView notiView = (TextView) notiFrag.getView().findViewById(R.id.bikeNotification);
            String timeStamp = new SimpleDateFormat("MM/dd HH:mm").format(Calendar.getInstance().getTime());
            notiView.setText("Dropped bike pin " + timeStamp);
        }
        return true;
    }

    public void showMap(View v) {

        Intent mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);

    }

    public void startAccelerometer(View view) {
        Intent accelIntent = new Intent(this, AccelerometerActivity.class);
        startActivity(accelIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief This is the callback for when a Recipe item is clicked at the RecipeFragment
     * @param item
     */
    public void onListFragmentInteraction(final Recipe item, final int pos, final RecipeListAdapter adapter, final List<Recipe> values) {
        final Recipe dialogItem = item;
        List<Fragment> allFrags = getSupportFragmentManager().getFragments();
        final EditRecipesFragment temp = (EditRecipesFragment) allFrags.get(2);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this); //Read Update


        alertDialog.setTitle(item.getName());
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.recipe_dialog, null);

        CheckBox onCheckBox = (CheckBox) dialoglayout.findViewById(R.id.recipe_checkbox);
        onCheckBox.setChecked(dialogItem.getOn());
        onCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dialogItem.setOn(isChecked);
            }
        });

        alertDialog.setView(dialoglayout);
        alertDialog.setPositiveButton("Edit Recipe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                temp.fillWithRecipe(dialogItem);
                mViewPager.setCurrentItem(2);
            }
        }).setNegativeButton("Cancel", null).setNeutralButton("Delete Recipe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("RecipeStore", Context.MODE_PRIVATE);
                    String jsonString = prefs.getString("RecipeMap", (new JSONObject()).toString());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    jsonObject.remove(dialogItem.getName());

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("RecipeMap").commit();
                    editor.putString("RecipeMap", jsonObject.toString());
                    editor.commit();

                    values.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    adapter.notifyItemRangeRemoved(pos, values.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        alertDialog.create().show();

  /*      System.out.println("hello there " + item.toString());
        Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
        List<Fragment> allFrags = getSupportFragmentManager().getFragments();
        EditRecipesFragment temp = (EditRecipesFragment) allFrags.get(2);
        temp.fillWithRecipe(item);
        mViewPager.setCurrentItem(2);*/
    }
}
