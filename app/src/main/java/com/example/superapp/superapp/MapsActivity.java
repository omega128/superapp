package com.example.superapp.superapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.Provider;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;
import android.util.Log;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private class Cry {
        private String myTitle;
        private double myLatitude,
                       myLongitude;

        public Cry (String title, double latitude, double longitude) {
            myTitle = title;
            myLatitude = latitude;
            myLongitude = longitude;
        }

        public String getTitle () { return myTitle; }
        public double getLatitude () { return myLatitude; }
        public double getLongitude () { return myLongitude; }

    }

    private Vector<Cry> cryList = new Vector<Cry>();

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

        // attempt to get the user's GPS coordinates and move the camera there if we can.
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                LatLng playerAt = new LatLng(
                        (location.getLatitude()),
                        (location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(playerAt));
            }
        } catch (SecurityException e) {
            Log.d("OHNOES", "Could not locate GPS coordinates.");
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://kcchambers.dreamhosters.com/scc/superapp/map.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OHNOES", "NET CONNECTION FAILED.");
            }

            @Override
            public void onResponse(Call call, final Response response) throws java.io.IOException {
                String responseData = response.body().string();
                Log.d("OHNOES", "NET CONNECTION SUCCEEDED.");
                Log.d("OHNOES", "JSON DATA: " + responseData);

                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONArray cries = json.getJSONArray("cries");

                    cryList.clear();

                    for (int i = 0; i < cries.length(); ++i) {
                        JSONObject cry = cries.getJSONObject(i);
                        String title = cry.getString("c_title");
                        Cry newCry = new Cry (cry.getString("c_title"),
                                              cry.getDouble("c_lat"),
                                              cry.getDouble("c_lon"));
                        cryList.add(newCry);
                    }

                    Log.d("OHNOES", "CRY VECTOR NOW SIZE " + cryList.size());

                } catch (JSONException e) {
                    Log.d("OHNOES", "COULD NOT PARSE DATA!!!");
                }
            }
        });

        Log.d("OHNOES", "CRY VECTOR NOW SIZE " + cryList.size());
        for (Cry cry : cryList) {
            LatLng coords = new LatLng(cry.getLatitude(),
                    cry.getLongitude());

            Log.d("OHNOES", "ADDED MARKER: " + cry.getTitle());
            mMap.addMarker(new MarkerOptions().position(coords).title(cry.getTitle()));
        }






        // Add a marker at the St John's Apartment, for testing.
        //LatLng stJohns = new LatLng(47.613998, -122.32253);
        //mMap.addMarker(new MarkerOptions().position(stJohns).title("Marker At St Johns"));
        Log.d("OHNOES", "ADDED A MARKER.");

    }
}
