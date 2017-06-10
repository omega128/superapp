package com.example.superapp.superapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
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
import android.os.Handler;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private Handler updateMapTimer;

    /* Store Cry Records */
    private Vector<Cry> cryList = new Vector<Cry>();
    private class Cry {
        private String myTitle;
        private double myLatitude,
                myLongitude;


        public Cry(String title, double latitude, double longitude) {
            myTitle = title;
            myLatitude = latitude;
            myLongitude = longitude;
        }

        public String getTitle() { return myTitle; }
        public double getLatitude() { return myLatitude; }
        public double getLongitude() { return myLongitude; }
    }


    /* We need to track the user's GPS coordinates */
    private boolean haveGPS = false;
    private String myLocation;
    private double latitude,
            longitude;
    private class MyCurrentLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            location.getLatitude();
            location.getLongitude();

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            myLocation = "Lat: " + latitude + " Long: " + longitude;

            haveGPS = true;
            Log.d("OHNOES", myLocation);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }

    /* Periodically poll the website and update the map */
    private Runnable updateMapTimerThread = new Runnable() {
        public void run() {
            requestCriesFromServer();
            updateMap();
            updateMapTimer.postDelayed(updateMapTimerThread, 2000);
        }
    };

    /* request new cries from the server, clear the list and repopulate it */
    private void requestCriesFromServer() {
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
                        Cry newCry = new Cry(cry.getString("c_descr"),
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
    }

    private void updateMap() {
        if (mMap != null) {
            mMap.clear();
            for (Cry cry : cryList) {
                LatLng coords = new LatLng(cry.getLatitude(),
                        cry.getLongitude());

                Log.d("OHNOES", "ADDED MARKER: " + cry.getTitle());
                mMap.addMarker(new MarkerOptions().position(coords).title(cry.getTitle()));
            }
        }
    }


    @Override
    public void onRequestPermissionsResult (int requestCode,
                                     String[] permissions,
                                     int[] grantResults)

    {
        Log.d("OHNOES", "Grant results: " + grantResults[0]);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // start polling the server, so we can update the map
        updateMapTimer = new Handler(getApplicationContext().getMainLooper());
        updateMapTimer.postDelayed(updateMapTimerThread, 1);

        // check if the user has given permission to use their GPS coords.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("OHNOES", "USER HAS FORBIDDEN ACCESS TO GPS!");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
        {
            //set up a location listener
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            MyCurrentLocationListener locationListener = new MyCurrentLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) locationListener);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /* the user creates a new cry for help by long clicking the map */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                if (haveGPS) {
                    // todo: bring up New Cry dialog
                }
                else
                {
                    // todo: complain if user has no gps

                }
            }
        });

    }
}
