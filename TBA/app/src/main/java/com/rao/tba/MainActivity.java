package com.rao.tba;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NotificationsFragment.OnListFragmentInteractionListener, RecipeFragment.OnListFragmentInteractionListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    protected static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private PendingIntent mPendingIntent;
    private LocationRequest mLocationRequest;
    private PendingIntent mLocationIntent;

    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Listen for changes of fragment
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
                } else if (position == 2) {
                    setTitle("Edit Recipe");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Check permissions

        // Create Google API Client
        buildGoogleApiClient();
    }

    /**
     * Need permission to get location.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * @brief Method to set up Google API Client and begin listening for activity
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

        if (mPendingIntent == null) {
            Intent intent = new Intent(this, TransitionIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mPendingIntent = pendingIntent;
        }

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(5000);
        }

        if (mLocationIntent == null) {
            Intent locationIntent = new Intent(this, TransitionIntentService.class);
            mLocationIntent = PendingIntent.getService(this, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
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
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("RAOStore", Context.MODE_PRIVATE);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        List<Fragment> allFrags = getSupportFragmentManager().getFragments();

        if (allFrags.size() < 2) {
            return;
        }

        final RecipeFragment temp = (RecipeFragment) allFrags.get(1);
        temp.updateRecipeList();

        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, Constants.DETECTION_INTERVAL_IN_MILLISECONDS, mPendingIntent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onResult(Status status) {

    }

    @Override
    public void onListFragmentInteraction(Notification item, int pos, NotificationListAdapter adapter, List<Notification> values) {
        // Check the type of notification
        if (item.getType().equals("Drop Pin")) {
            // Create an intent for the map
            Intent mapsIntent = new Intent(this, MapsActivity.class);

            // Create a bundle to hold any extra parameters
            Bundle b = new Bundle();

            // Store location information in bundle
            Location temp = item.getLocation();
            b.putDouble("Latitude", temp.getLatitude());
            b.putDouble("Longitude", temp.getLongitude());
            mapsIntent.putExtras(b);

            // Start map activity
            startActivity(mapsIntent);
        }
    }

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "ActivityDetectionBroadcastReceiver received broadcast");

            // Get notification information
            String notificationString = intent.getStringExtra("New Notification");

            // Add notification to list of notifications
            List<Notification> tempList = mSectionsPagerAdapter.getNotificationList();
            Notification newNotification = new Notification(notificationString, Integer.toString(tempList.size()));
            tempList.add(0, newNotification);

            // Signal to NotificationListAdapter that the list has changed
            ((NotificationsFragment) getSupportFragmentManager().getFragments().get(0)).getNotificationsAdapter().notifyDataSetChanged();
        }
    }
}
