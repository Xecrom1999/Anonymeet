package com.example.or.anonymeet.FireBaseChat;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.or.anonymeet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddChatFragment extends Fragment {

    EditText user;
    ImageButton add;
    DB myDB;
    android.support.v4.app.FragmentTransaction ft;
    Fragment ownF;
    RecyclerAdapter recyclerAdapter;

    public AddChatFragment() {
        // Required empty public constructor
    }
    public AddChatFragment(RecyclerAdapter r) {
        recyclerAdapter = r;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_chat, container, false);
        ownF = this;
        myDB = new DB(getActivity());
        user = (EditText)v.findViewById(R.id.userToAdd);
        add = (ImageButton)v.findViewById(R.id.addUser);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getText().toString().equals("")){
                    myDB.insert(user.getText().toString());
                    recyclerAdapter.syncContacts();
                    ft = getFragmentManager().beginTransaction();
                    ft.remove(ownF);
                    ft.commit();

                }
                else{
                    Toast.makeText(getActivity(), "You did not specified any user", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        recyclerAdapter.notifyItemInserted(recyclerAdapter.contacts.size()-1);
        super.onDestroy();
    }
}
