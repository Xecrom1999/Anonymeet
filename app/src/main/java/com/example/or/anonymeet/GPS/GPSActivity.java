package com.example.or.anonymeet.GPS;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.Collection;

public class GPSActivity extends AppCompatActivity implements  ValueEventListener, ListListener {

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
        finish();
    }


    public void goToMessagesActivity(View view) {
        startActivity(new Intent(this, MessagesActivity.class));
    }

    public void onDataChange(DataSnapshot dataSnapshot) {

        Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

        if (iter != null) {
            Collection<String> namesList = new ArrayList();
            Collection<Integer> distancesList = new ArrayList();

            for (DataSnapshot item : iter) {

                String email = getSharedPreferences("data", MODE_PRIVATE).getString("email", "");
                if (!item.getKey().toString().equals(email.substring(0, email.indexOf('.'))) && LocationListenerService.getLocation() != null && item.hasChild("latitude") && item.hasChild("longitude")) {
                    namesList.add(item.getKey().toString());

                    double latitude = Double.parseDouble(item.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(item.child("longitude").getValue().toString());

                    Location targetLocation = new Location("");
                    targetLocation.setLatitude(latitude);
                    targetLocation.setLongitude(longitude);

                    float distance = targetLocation.distanceTo(LocationListenerService.getLocation());
                    distancesList.add((int) distance);
                }
            }
            adapter.update(namesList, distancesList);
        }
    }

    public void onCancelled(FirebaseError firebaseError) {
    }

    public void startChat(String userName) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("usernameTo", userName);
        startActivity(intent);

    }

    public void noUsers(boolean noUsers) {
        peopleList.setVisibility(noUsers ? View.GONE:View.VISIBLE);
        findViewById(R.id.noUsers_text).setVisibility(noUsers ? View.VISIBLE:View.GONE);
    }
}
