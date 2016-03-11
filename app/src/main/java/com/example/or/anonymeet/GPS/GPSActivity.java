package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.or.anonymeet.ChatActivity;
import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;

//import anonymeet.R;

public class GPSActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView address_text;
    TextView name_text;
    TextView data_text;
    MyLocationListener locationListener;
    LocationManager locationManager;
    Geocoder geocoder;
    Location mLocation;
    Firebase firebaseRoot;
    EditText data_input;
    String myName;
    SharedPreferences myData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        toolbar = (Toolbar) findViewById(R.id.toolBarGPS);
        setSupportActionBar(toolbar);

        address_text = (TextView) findViewById(R.id.address_text);
        name_text = (TextView) findViewById(R.id.name_text);
        data_text = (TextView) findViewById(R.id.data_text);
        data_input = (EditText) findViewById(R.id.data_input);

        myData = getSharedPreferences("myData", Context.MODE_PRIVATE);
        myName = myData.getString("myName", "NAME");

        if (myName.equals("NAME") || myName.equals("") || myName == null) changeName();
        else name_text.setText("My name: " + myName);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        geocoder = new Geocoder(this);

        firebaseRoot = new Firebase("https://anonymeet.firebaseio.com");

        firebaseRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String text = "" + dataSnapshot.getValue(String.class);
                    data_text.setText("Data: " + text);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void changeData(View view) {
        String text = data_input.getText().toString();
        data_input.setText("");
        //firebaseRoot.setValue(text);
        HashMap map = new HashMap<String, String>();
        map.put("Text", text);
        firebaseRoot.updateChildren(map);
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
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onProviderDisabled(String provider) {

        }
    }

    public void changeName() {
        View view = getLayoutInflater().inflate(R.layout.name_dialog_layout, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        final EditText editText = (EditText) view.findViewById(R.id.name_input2);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == EditorInfo.IME_ACTION_DONE) {

                    String name = editText.getText().toString();

                    if (!name.matches("")){
                        SharedPreferences sp = getSharedPreferences("myData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("myName", name);
                        editor.commit();
                        name_text.setText("My name: " + name);
                    }

                    return true;
                }

                return false;
            }
        });

        dialog.setView(view);
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = editText.getText().toString();

                if (!name.matches("")){
                    SharedPreferences sp = getSharedPreferences("myData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("myName", name);
                    editor.commit();
                    name_text.setText("My name: " + name);
                    myName = name;
                }

            }
        });

        dialog.setCancelable(false);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (myName.equals("NAME") || myName.equals("") || myName == null) changeName();
                else name_text.setText("My name: " + myName);
            }
        });

        Dialog d = dialog.create();
        d.show();
    }
}
