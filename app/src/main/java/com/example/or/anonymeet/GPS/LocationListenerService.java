package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationListenerService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GpsStatus.Listener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static Location mCurrentLocation;

    private String nickname;
    private static Firebase onlineUsers;

    public static boolean providerEnabled;

    LocationManager locationManager;

    static NotificationManager notificationManager;

    public static Context ctx;

    boolean visible;

    public LocationListenerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        visible = true;

        ctx = this;

        nickname = getSharedPreferences("data", MODE_PRIVATE).getString("nickname", "");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        onlineUsers = new Firebase("https://anonymeetapp.firebaseio.com/OnlineUsers");
        
        buildGoogleApiClient();

        mGoogleApiClient.connect();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        setupGPS();

        if (FindPeopleActivity.isRunning())
        cancelNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    private void setupGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.addGpsStatusListener(this);
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
        if (nickname != null)
            onlineUsers.child(nickname).runTransaction(new Transaction.Handler() {
                public Transaction.Result doTransaction(MutableData mutableData) {
                    mutableData.setValue(null);
                    return Transaction.success(mutableData);
                }

                public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
                }
            });
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (visible) {
            mCurrentLocation = location;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String gender = getSharedPreferences("data", MODE_PRIVATE).getString("gender", "");

            onlineUsers.child(nickname).child("latitude").setValue(latitude);
            onlineUsers.child(nickname).child("longitude").setValue(longitude);
            onlineUsers.child(nickname).child("gender").setValue(gender);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void buildNotification() {
            Notification.Builder n;
            n = new Notification.Builder(ctx)
                    .setContentTitle("Anonymeet")
                    .setContentText("Status: online")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setShowWhen(false)
                    .setVibrate(new long[]{Long.valueOf(0)})
                    .setSound(null)
                    .setOngoing(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                n.setColor(Color.parseColor("#ff5722"));

            TaskStackBuilder t = TaskStackBuilder.create(ctx);
            Intent i = new Intent(ctx, FindPeopleActivity.class);
            t.addNextIntent(i);
            PendingIntent pendingIntent = t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            n.setContentIntent(pendingIntent);
            //notificationManager.notify(0, n.build());
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

    public static Location getLocation() {
        return mCurrentLocation;
    }

    public static void cancelNotification() {
        notificationManager.cancel(0);
    }

    @Override
    public void onDestroy() {
        Log.d("TAG", "onDestroy");
        visible = false;
        LocationListenerService.cancelNotification();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        hideMe();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                providerEnabled = true;
                mGoogleApiClient.connect();
                if (FindPeopleActivity.isRunning())
                    FindPeopleActivity.hideMessage();

                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                providerEnabled = false;
                hideMe();
                if (nickname != null)
                    hideMe();
                if (FindPeopleActivity.isRunning())
                    FindPeopleActivity.showMessage();
                else {
                    stopSelf();
                }
                break;
        }
    }

    private void hideMe() {
        onlineUsers.child(nickname).runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
            }
        });
    }
}
