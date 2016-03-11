package com.example.or.anonymeet.GPS;

import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;



public class MyLocationListener extends Service implements LocationListener {

    LocationManager locationManager;
    Geocoder geocoder;
    String mAddress;
    Location mLocation;

    public void onLocationChanged(Location location) {

        mLocation = location;

        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();

        try {
            mAddress = geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }

    public IBinder onBind(Intent intent) {
        return null;
    }


}
