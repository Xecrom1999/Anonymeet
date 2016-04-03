package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationListenerService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, Firebase.AuthStateListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 7000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Location mCurrentLocation;

    private String childName;

    private Firebase onlineUsers;

    public LocationListenerService() {
        super("LocationListenerService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String email = getSharedPreferences("data", MODE_PRIVATE).getString("email", "");
        childName = email.substring(0, email.indexOf('.'));

        onlineUsers = new Firebase("https://anonymeetapp.firebaseio.com/OnlineUsers");

        onlineUsers.addAuthStateListener(this);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

        return START_STICKY;
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
        GPSActivity.logout();
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        onlineUsers.child(childName).child("latitude").setValue(latitude);
        onlineUsers.child(childName).child("longitude").setValue(longitude);
    }

    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    public void onAuthStateChanged(AuthData authData) {
        if (authData == null)
        stopLocationUpdates();
    }

    public static Location getLocation() {
        return mCurrentLocation;
    }
}
