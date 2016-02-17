package com.rao.tba;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import java.util.List;

import com.rao.tba.RecipeFragment.OnListFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener {
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

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
                System.out.println("OnPageSelected " + position);
                List<Fragment> allFrags = getSupportFragmentManager().getFragments();
                System.out.println(allFrags.toString());
                if (position == 0) {
                    setTitle("Notifications");
                } else if (position == 1) {
                    setTitle("My Recipes");
                    RecipeFragment temp = (RecipeFragment) allFrags.get(1);
                    temp.printStuff();
                } else if (position == 2) {
                    setTitle("Edit Recipe");
                    EditRecipesFragment temp = (EditRecipesFragment) allFrags.get(2);
                    temp.hello();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
    public void onListFragmentInteraction(Recipe item) {
        System.out.println("hello there " + item.toString());
        Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
    }
}
