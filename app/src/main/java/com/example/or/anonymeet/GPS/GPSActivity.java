package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.or.anonymeet.FireBaseChat.ChatActivity;
import com.example.or.anonymeet.FireBaseChat.MessagesActivity;
import com.example.or.anonymeet.FireBaseChat.MyService;
import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GPSActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ValueEventListener, ListListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private Firebase onlineUsers;
    private String userName;
    private Toolbar toolbar;
    LocationManager lm;
    static boolean active;
    RecyclerView peopleList;
    PeopleListAdapter adapter;
    static String childName;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_activity);
        checkForPermission();
        Intent i = new Intent(this, MyService.class);
        startService(i);

        toolbar = (Toolbar) findViewById(R.id.toolBar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Find People");

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        onlineUsers = new Firebase("https://anonymeetapp.firebaseio.com/OnlineUsers");
        userName = getSharedPreferences("data", MODE_PRIVATE).getString("email", "");

        childName = userName.substring(0, userName.indexOf('.'));

        //buildGoogleApiClient();

        onlineUsers.addValueEventListener(this);

        locationProvider();

        initializeList();

        startLocationService();
    }

    private void startLocationService() {
        intent = new Intent(getApplicationContext(), LocationListenerService.class);
        startService(intent);
    }

    private void initializeList() {
        peopleList = (RecyclerView) findViewById(R.id.peopleList);
        adapter = new PeopleListAdapter(this);
        peopleList.setLayoutManager(new LinearLayoutManager(this));
        peopleList.setHasFixedSize(true);
        peopleList.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
                return;
            }
        }
    }

    public static boolean isActive(){
        return active;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Snackbar.make(peopleList, "Location Permission Denied", Snackbar.LENGTH_SHORT).show();
            }
    }

    public void locationProvider() {

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setCancelable(false);

            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout_item) onLogout();

        return super.onOptionsItemSelected(item);
    }

    private void onLogout() {
        onlineUsers.unauth();

        stopService(intent);

        onlineUsers.child(childName).runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
            }
        });
        startActivity(new Intent(this, LoginActivity.class));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        active = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }

        super.onPause();
    }*/

    @Override
    protected void onStop() {
        //mGoogleApiClient.disconnect();
        /*onlineUsers.child(childName).runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
            }
        });*/
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        active = false;
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Address address = null;
        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
        } catch (IOException e1) {
        }

        if (address != null)
            onlineUsers.child(childName).child("address").setValue(address.getAddressLine(0));

        onlineUsers.child(childName).child("latitude").setValue(latitude);
        onlineUsers.child(childName).child("longitude").setValue(longitude);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    public void goToMessagesActivity(View view) {
        startActivity(new Intent(this, MessagesActivity.class));
    }

    public void onDataChange(DataSnapshot dataSnapshot) {

        Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

        if (iter != null) {
            Collection<String> namesList = new ArrayList<String>();
            Collection<String> addressesList = new ArrayList<String>();

            for (DataSnapshot item : iter) {

                if (item.child("address").getValue() != null) {
                    namesList.add(item.getKey().toString());
                    addressesList.add(item.child("address").getValue().toString());
                }

            }
            adapter.update(namesList, addressesList);
        }
    }

    public void onCancelled(FirebaseError firebaseError) {
    }

    public void startChat(String userName) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("usernameTo", userName);
        startActivity(intent);

    }


    public static String getChildName() {
        return childName;
    }
}
