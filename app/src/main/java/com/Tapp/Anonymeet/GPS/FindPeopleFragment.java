package com.Tapp.Anonymeet.GPS;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.Tapp.Anonymeet.FireBaseChat.ChatActivity;
import com.Tapp.Anonymeet.FireBaseChat.HelperDB;
import com.Tapp.Anonymeet.FireBaseChat.MessagesActivity;
import com.Tapp.Anonymeet.FireBaseChat.MyService;
import com.Tapp.Anonymeet.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class FindPeopleFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, ListListener {

    private static Firebase onlineUsers;
    private static Firebase users;
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
    static Context ctx;
    static String nickname;
    View view;
    static String username;

    public FindPeopleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.find_people_fragment, container, false);
        this.view = view;

        this.ctx = getContext();

        message_text = (TextView) view.findViewById(R.id.noUsers_text);

        visible_switch = (Switch) view.findViewById(R.id.visible_switch);
        visible_switch.setOnCheckedChangeListener(this);

        locIntent = new Intent(getContext(), LocationListenerService.class);
        db = new HelperDB(ctx);
        nickname = ctx.getSharedPreferences("data", MODE_PRIVATE).getString("nickname", "");

        if (getActivity().getIntent().getBooleanExtra("fromNoti", false)) {
            Intent i = new Intent(ctx, MessagesActivity.class);
            i.putExtra("fromNoti", true);
            i.putExtra("usernameTo", getActivity().getIntent().getStringExtra("usernameFrom"));
            startActivity(i);
        }
        if(getActivity().getIntent().getBooleanExtra("fromNotiFew", false)){
            Intent i = new Intent(ctx, MessagesActivity.class);
            startActivity(i);
        }

        checkForPermission();

        message_text = (TextView) view.findViewById(R.id.noUsers_text);

        visible_switch = (Switch) view.findViewById(R.id.visible_switch);
        visible_switch.setOnCheckedChangeListener(this);

        lm = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);

        onlineUsers = new Firebase("https://anonymeetapp.firebaseio.com/OnlineUsers");
        users = new Firebase("https://anonymeetapp.firebaseio.com/Users");

        initializeList();

        startServices();

        updateMessage();

        visible_switch.setChecked(ctx.getSharedPreferences("data", MODE_PRIVATE).getBoolean("visible", true));

        username = ctx.getSharedPreferences("data", MODE_PRIVATE).getString("nickname", "");

        return view;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasPermission = ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
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

        notiIntent = new Intent(ctx, MyService.class);
        ctx.startService(notiIntent);

        if (visible_switch.isChecked()) startLocationService();
    }

    private void startLocationService() {
        ctx.startService(locIntent);
    }

    private void initializeList() {
        peopleList = (RecyclerView) view.findViewById(R.id.peopleList);
        adapter = new PeopleListAdapter(this);
        peopleList.setLayoutManager(new LinearLayoutManager(ctx));
        peopleList.setHasFixedSize(true);
        peopleList.setAdapter(adapter);
    }

    public static void updateList() {
        onlineUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChildren()) return;
                if (!dataSnapshot.hasChild(username)) clearAdapter();

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
        Intent intent = new Intent(ctx, ChatActivity.class);
        intent.putExtra("usernameTo", userName);
        intent.putExtra("userWasExisted", db.userExists(userName));
        db.insertUser(userName, gender, 0);
        startActivity(intent);
        Toast.makeText(ctx, "אללה וואכבר!", Toast.LENGTH_SHORT).show();
    }

    private boolean checkInternetConnection() {
        //TODO: show text when there's no connection to the Internet;
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

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
            setMessage("Loading...");
            peopleList.setVisibility(View.VISIBLE);
            message_text.setVisibility(View.GONE);
        }
    }

    public static void setMessage(String message) {
        message_text.setText(message);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ctx.startService(locIntent);
        }
        else {
            ctx.stopService(locIntent);
        }
        updateMessage();
    }

    public static void clearAdapter() {
        adapter.clearAll();
    }

    public static void exit() {
        isRunning = false;
        if (visible_switch.isChecked() && LocationListenerService.providerEnabled)
            LocationListenerService.buildNotification();

        ctx.getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("visible", visible_switch.isChecked()).commit();
    }

    public void onLogout() {
        ctx.stopService(notiIntent);
        ctx.stopService(locIntent);
        db.deleteAll();

        onlineUsers.child(nickname).runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
            }
        });

        ctx.getSharedPreferences("data", MODE_PRIVATE).edit().clear().commit();

        startActivity(new Intent(ctx, LoginActivity.class));

        getActivity().finish();
    }
}
