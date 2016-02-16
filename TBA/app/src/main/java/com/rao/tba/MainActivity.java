package com.rao.tba;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             //   System.out.println("OnPageScrolled " + position);
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("OnPageSelected " + position);

                if (position == 0) {
                    setTitle("Notifications");
                } else if (position == 1) {
                    setTitle("My Recipes");
                } else if (position == 2) {
                    setTitle("Edit Recipe");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               // System.out.println("OnPageScrollStateChanged " + state);
            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//                Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(mapsIntent);
//            }
//        });

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

    public static class NotificationsFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public NotificationsFragment() {
        }

        public static NotificationsFragment newInstance(int sectionNumber) {
            NotificationsFragment fragment = new NotificationsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            System.out.println("CREATING Notifications");
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class ViewRecipesFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public ViewRecipesFragment() {
        }

        public static ViewRecipesFragment newInstance(int sectionNumber) {
            ViewRecipesFragment fragment = new ViewRecipesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            System.out.println("CREATING ViewRecipes");
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class EditRecipesFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        private Spinner spinner1, spinner2, spinner3;
        private Button btnSubmit;

        public EditRecipesFragment() {
        }

        public static EditRecipesFragment newInstance(int sectionNumber) {
            EditRecipesFragment fragment = new EditRecipesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            System.out.println("CREATING EditRecipes");
            View rootView = inflater.inflate(R.layout.fragment_edit_recipe, container, false);
            addListenerOnButton(rootView);
            return rootView;
        }

        public void addListenerOnButton(View rootView) {


            spinner1 = (Spinner) rootView.findViewById(R.id.spinner1);
            spinner2 = (Spinner) rootView.findViewById(R.id.spinner2);
            spinner3 = (Spinner) rootView.findViewById(R.id.spinner3);
            btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

            btnSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    System.out.println("OnClickListener : " +
                            "\nSpinner 1 : " + String.valueOf(spinner1.getSelectedItem()) +
                            "\nSpinner 2 : " + String.valueOf(spinner2.getSelectedItem()));
                }
            });
        }
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            System.out.println("Get item called");
            if ( position == 0 ) {
                return NotificationsFragment.newInstance(position + 1);
            } else if ( position == 1 ) {
                return ViewRecipesFragment.newInstance(position + 1);
            } else if( position == 2 ) {
                return EditRecipesFragment.newInstance(position + 1);
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
}
