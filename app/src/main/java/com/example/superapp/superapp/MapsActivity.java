package com.example.superapp.superapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Provider;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // attempt to get the user's GPS coordinates and move the camera there if we do.
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider); /* TODO: FIX THIS */
/*
            if (location != null) {
                LatLng playerAt = new LatLng(
                        (location.getLatitude()),
                        (location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(playerAt));
            }*/
        } catch (SecurityException e) {
            // do something drastic here! ...or not.
        }

        // Add a marker at the St John's Apartment, for testing.
        LatLng stJohns = new LatLng(47.613998, -122.32253);
        mMap.addMarker(new MarkerOptions().position(stJohns).title("Marker At St Johns"));

        //LatLng sydney = new LatLng(-34, 151);


        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));



    }
}
