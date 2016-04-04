package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationListenerService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, Firebase.AuthStateListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 6000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 4000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Location mCurrentLocation;

    private String childName;
    private Firebase onlineUsers;

    GPSTracker gpsTracker;

    public LocationListenerService() {
        super("LocationListenerService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String email = getSharedPreferences("data", MODE_PRIVATE).getString("email", "");
        childName = email.substring(0, email.indexOf(".com"));

        onlineUsers = new Firebase("https://anonymeetapp.firebaseio.com/OnlineUsers");

        onlineUsers.addAuthStateListener(this);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

        gpsTracker = new GPSTracker();

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
        if (mGoogleApiClient.isConnected())
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

    public void buildNotification() {
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder n = null;

            n = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Long time no see!")
                    .setContentText("Pleas enable location service.")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) n.setColor(Color.parseColor("#ff5722"));

        TaskStackBuilder t = TaskStackBuilder.create(this);
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        t.addNextIntent(i);
        PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        n.setContentIntent(pendingIntent);
        nm.notify(0, n.build());
    }

    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        Log.d("TAG", "cause: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("TAG", result.getErrorMessage());
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
        if (authData == null) {
            stopLocationUpdates();
            stopSelf();
        }
    }

    public static Location getLocation() {
        return mCurrentLocation;
    }


    public class GPSTracker implements android.location.GpsStatus.Listener {

        LocationManager locationManager;

        public GPSTracker() {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            setupGPS();
        }

        private void setupGPS() {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.addGpsStatusListener(this);

        }

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    if (onlineUsers.getAuth() != null)
                    buildNotification();
                    break;
            }
        }
    }
}
