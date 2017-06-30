package com.Tapp.Anonymeet.FireBaseChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Tapp.Anonymeet.R;


public class MessagesFragment extends Fragment implements MyListener {

    public MessagesFragment() {

    }

    RecyclerView recyclerView;
    static UsersAdapter usersAdapter;
    static Context ctx;
    static boolean isActive;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_messages, container, false);

        isActive = true;
        ctx = getActivity();
        if(getActivity().getIntent().getBooleanExtra("fromNoti", false)){
            Intent i = new Intent(ctx, ChatActivity.class);
            i.putExtra("usernameTo", getActivity().getIntent().getStringExtra("usernameTo"));
            startActivity(i);
        }
        preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        usersAdapter = new UsersAdapter(getActivity(), this);
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

        return view;
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        Intent myintent = new Intent(ctx, ChatActivity.class).putExtra("usernameTo", usersAdapter.contacts.get(position).name);
        startActivity(myintent);
    }

    public void syncContacts() {
        Log.d("MYLOG", "fragment");

        if (ctx != null && recyclerView != null) {
            Log.d("MYLOG", "fragment1");

            usersAdapter.syncContacts();
        }
    }
}
