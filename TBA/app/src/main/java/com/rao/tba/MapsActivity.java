package com.rao.tba;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private double DEFAULT_LAT = 34.416655;
    private double DEFAULT_LONG = -119.845260;
    private GoogleMap mMap;
    private double mLat;
    private double mLong;
    private boolean mHaveCoords;

    public static List<LatLng> sPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mLat = b.getDouble("Latitude");
            mLong = b.getDouble("Longitude");
            mHaveCoords = true;
        } else {
            mHaveCoords = false;
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        if (mHaveCoords) {
            LatLng location = new LatLng(mLat, mLong);
            mMap.addMarker(new MarkerOptions().position(location).title("Your Bike"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        } else {
            LatLng location = new LatLng(DEFAULT_LAT, DEFAULT_LONG);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            MarkerOptions options = new MarkerOptions();
            CircleOptions circleOptions;
            for (LatLng l : sPoints ) {
                options.position(l);
                mMap.addMarker(options);

                circleOptions = new CircleOptions()
                        .center( l )
                        .radius( Constants.GEOFENCE_RADIUS )
                        .fillColor(0x40ff0000)
                        .strokeColor(Color.TRANSPARENT)
                        .strokeWidth(2);

                mMap.addCircle(circleOptions);
            }
        }


    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        mMap.addMarker(options);
        CircleOptions circleOptions = new CircleOptions()
                .center( latLng )
                .radius( Constants.GEOFENCE_RADIUS )
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        mMap.addCircle(circleOptions);

        sPoints.add(latLng);
    }
}
