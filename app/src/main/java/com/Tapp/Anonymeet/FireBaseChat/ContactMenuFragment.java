package com.Tapp.Anonymeet.FireBaseChat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Tapp.Anonymeet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactMenuFragment extends android.app.Fragment {


    public ContactMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.contact_menu_fragment, null, false);



        return v;
    }

}
