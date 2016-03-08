package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.or.anonymeet.ChatActivity;
import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;

//import anonymeet.R;

public class GPSActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView address_text;
    TextView distance_text;
    TextView data_text;
    MyLocationListener locationListener;
    LocationManager locationManager;
    Geocoder geocoder;
    boolean first;
    Location mLocation;
    Firebase firebaseRoot;
    EditText data_input;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        toolbar = (Toolbar) findViewById(R.id.toolBarGPS);
        setSupportActionBar(toolbar);

        first = true;

        address_text = (TextView) findViewById(R.id.address_text);
        distance_text = (TextView) findViewById(R.id.distance_text);
        data_text = (TextView) findViewById(R.id.data_text);
        data_input = (EditText) findViewById(R.id.data_input);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        geocoder = new Geocoder(this);

        firebaseRoot = new Firebase("https://anonymeet.firebaseio.com/Locations");

        firebaseRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                data_text.setText("Data: " + text);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void changeData(View view) {
        String text = data_input.getText().toString();
        data_input.setText("");
        firebaseRoot.setValue(text);
    }

    public void chatActivity(View view) {
        startActivity(new Intent(this, ChatActivity.class));
    }



    class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude();
            double latitude =  location.getLatitude();

            Address address = null;
            try {
                address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            address_text.setText("Address: " + address.getAddressLine(0));

            if (first) {
                mLocation = location;
                first = false;
            }
            else {
                float distance = location.distanceTo(mLocation);
                distance_text.setText("Distance: " + (int)distance + " meters");
            }

            //Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    }
}
