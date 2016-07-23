package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.or.anonymeet.FireBaseChat.ChatActivity;
import com.example.or.anonymeet.FireBaseChat.HelperDB;
import com.example.or.anonymeet.FireBaseChat.MessagesActivity;
import com.example.or.anonymeet.FireBaseChat.MessagesDB;
import com.example.or.anonymeet.FireBaseChat.MyService;
import com.example.or.anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.Collection;

public class FindPeopleActivity extends AppCompatActivity implements ListListener, CompoundButton.OnCheckedChangeListener {

    private static Firebase onlineUsers;
    private static Firebase users;
    private Toolbar toolbar;
    LocationManager lm;
    static RecyclerView peopleList;
    static PeopleListAdapter adapter;
    Intent locIntent;
    Intent notiIntent;
    HelperDB db;
    static boolean isRunning;
    static TextView message_text;
    static final String noUsers_message = "No online users near by.";
    static final String locationDisabled_message = "Touch to enable location services.";
    static final String switchOff_message = "You're invisible to others";
    static Switch visible_switch;

    static String nickname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_people_activity);

        locIntent = new Intent(getApplicationContext(), LocationListenerService.class);
        db = new HelperDB(this);
        nickname = getSharedPreferences("data", MODE_PRIVATE).getString("nickname", "");

        if (getIntent().getBooleanExtra("fromNoti", false)) {
            Intent i = new Intent(this, MessagesActivity.class);
            i.putExtra("fromNoti", true);
            i.putExtra("usernameTo", getIntent().getStringExtra("usernameFrom"));
            startActivity(i);
        }
        if(getIntent().getBooleanExtra("fromNotiFew", false)){
            Intent i = new Intent(this, MessagesActivity.class);
            startActivity(i);
        }

        checkForPermission();

        message_text = (TextView) findViewById(R.id.noUsers_text);

        toolbar = (Toolbar) findViewById(R.id.toolBar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Find People");

        visible_switch = (Switch) findViewById(R.id.visible_switch);
        visible_switch.setOnCheckedChangeListener(this);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        onlineUsers = new Firebase("https://anonymeetapp.firebaseio.com/OnlineUsers");
        users = new Firebase("https://anonymeetapp.firebaseio.com/Users");

        initializeList();

        startServices();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Snackbar.make(peopleList, "Location Permission Denied", Snackbar.LENGTH_SHORT).show();
            }
    }

    private void startServices() {

        notiIntent = new Intent(this, MyService.class);
        startService(notiIntent);

        if (visible_switch.isChecked()) startLocationService();
    }

    private void startLocationService() {
        startService(locIntent);
    }

    private void initializeList() {
        peopleList = (RecyclerView) findViewById(R.id.peopleList);
        adapter = new PeopleListAdapter(this);
        peopleList.setLayoutManager(new LinearLayoutManager(this));
        peopleList.setHasFixedSize(true);
        peopleList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout_item) logoutMessage();

        return super.onOptionsItemSelected(item);
    }

    private void logoutMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("You will lose all of your data.");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onLogout();
            }
        });
        builder.setNegativeButton("Cancel", null);

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void onLogout() {
        stopService(notiIntent);
        stopService(locIntent);
        db.deleteAll();

        onlineUsers.child(nickname).runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
            }
        });

        getSharedPreferences("data", MODE_PRIVATE).edit().clear().commit();

        startActivity(new Intent(this, LoginActivity.class));

        finish();
    }

    public void goToMessagesActivity(View view) {
        startActivity(new Intent(this, MessagesActivity.class));
    }

    public static void updateList() {
        onlineUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) return;
                Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

                Collection<String> namesList = new ArrayList();
                Collection<Integer> distancesList = new ArrayList();
                Collection<String> gendersList = new ArrayList();

                for (DataSnapshot item : iter) {

                    if (!nickname.equals(item.getKey().toString()) && LocationListenerService.getLocation() != null && item.hasChild("latitude") && item.hasChild("longitude") && item.hasChild("gender")) {
                        namesList.add(item.getKey().toString());

                        double latitude = Double.parseDouble(item.child("latitude").getValue().toString());
                        double longitude = Double.parseDouble(item.child("longitude").getValue().toString());
                        Location targetLocation = new Location("");
                        targetLocation.setLatitude(latitude);
                        targetLocation.setLongitude(longitude);

                        float distance = targetLocation.distanceTo(LocationListenerService.getLocation());
                        distancesList.add((int) distance);

                        gendersList.add(item.child("gender").getValue().toString());
                    }
                }
                adapter.update(namesList, distancesList, gendersList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void startChat(String userName, String gender) {
        MessagesDB myDB = new MessagesDB(this);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("usernameTo", userName);
        intent.putExtra("userWasExisted", db.userExists(userName));
        db.insertUser(userName, gender);
        startActivity(intent);
    }

    private boolean checkInternetConnection() {
        //TODO: show text when there's no connection to the Internet;
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())
            return true;

        return false;
    }

    public static void updateMessage() {
        boolean visible = visible_switch.isChecked();
        boolean providerEnabled = LocationListenerService.providerEnabled;
        boolean hasUsers = PeopleListAdapter.hasUsers;

        peopleList.setVisibility(View.GONE);
        message_text.setVisibility(View.VISIBLE);

        if (!visible) setMessage(switchOff_message);
        else if (!providerEnabled) setMessage(locationDisabled_message);
        else if (!hasUsers) setMessage(noUsers_message);

        else {
            updateList();
            setMessage("Loading...");
            peopleList.setVisibility(View.VISIBLE);
            message_text.setVisibility(View.GONE);
        }
    }

    public static void setMessage(String message) {
        message_text.setText(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        try {
            LocationListenerService.cancelNotification();
        } catch (NullPointerException e) {
        }
        updateMessage();
        visible_switch.setChecked(getSharedPreferences("data", MODE_PRIVATE).getBoolean("visible", false));
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        if (visible_switch.isChecked() && LocationListenerService.providerEnabled)
        LocationListenerService.buildNotification();

        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("visible", visible_switch.isChecked()).commit();
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public void enableLocationServices(View view) {

        if (!message_text.getText().toString().equals(locationDisabled_message)) return;

        if (Build.VERSION.SDK_INT >= 22) locationChecker(LocationListenerService.getApi(), this);

        else if (!LocationListenerService.providerEnabled) {
            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);


            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            GpsStatus.Listener listener = new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    if (event == GpsStatus.GPS_EVENT_STARTED) finishActivity(0);
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            
          manager.addGpsStatusListener(new GpsStatus.Listener() {
              @Override
              public void onGpsStatusChanged(int event) {
                  if (event == GpsStatus.GPS_EVENT_STARTED) finishActivity(0);
              }
          });
            manager.removeGpsStatusListener(listener);
        }
    }

    public void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                                     @Override
                                     public void onResult(LocationSettingsResult result) {
                                         final Status status = result.getStatus();
                                         final LocationSettingsStates state = result.getLocationSettingsStates();
                                         switch (status.getStatusCode()) {
                                             case LocationSettingsStatusCodes.SUCCESS:
                                                 break;
                                             case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                                 try {
                                                     status.startResolutionForResult(
                                                             activity, 1000);
                                                 } catch (IntentSender.SendIntentException e) {
                                                 }
                                                 break;
                                             case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                                 break;
                                         }
                                     }
                                 }
        );
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startService(locIntent);
            setMessage("Loading...");
        }
        else {
            stopService(locIntent);
            updateMessage();
        }
    }

    public static void clearAdapter() {
        adapter.clearAll();
    }
}