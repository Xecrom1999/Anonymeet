package com.example.or.anonymeet.FireBaseChat;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class AddChatFragment extends android.app.Fragment {

    EditText user;
    ImageButton add;
    DB myDB;

    public AddChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_chat, container, false);
        myDB = new DB(getContext());
        user = (EditText)v.findViewById(R.id.userToAdd);
        add = (ImageButton)v.findViewById(R.id.addUser);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getText().toString().equals("")){
                    myDB.insert(user.getText().toString());
                }
                else{
                    Toast.makeText(getContext(), "You did not specified any user", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

}
