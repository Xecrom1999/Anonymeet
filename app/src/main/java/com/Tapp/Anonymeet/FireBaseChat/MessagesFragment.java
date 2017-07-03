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

import com.Tapp.Anonymeet.GPS.FindPeopleActivity;
import com.Tapp.Anonymeet.R;


public class MessagesFragment extends Fragment implements MyListener {

    public MessagesFragment() {

    }

    RecyclerView recyclerView;
    static UsersAdapter usersAdapter;
    static Context ctx;
    SharedPreferences preferences;

    @Override
    public void onResume() {
        super.onResume();
        syncContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_messages, container, false);


        ctx = getActivity();

        preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        usersAdapter = new UsersAdapter(getContext(), this);
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

        usersAdapter.syncContacts();

    }

    public void itemInsertedIn(int position) {
        Log.d("MYLOG", "fragment");

        usersAdapter.itemInsertedIn(position);

    }
}
