package com.Tapp.Anonymeet.FireBaseChat;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.Tapp.Anonymeet.R;

/**
 * Created by Or on 11/07/2017.
 */

public class ContactMenuDialogFragment extends android.app.DialogFragment {

    ListView listView;
    UsersAdapter adapter;
    int position;
    Context context;


    public ContactMenuDialogFragment(UsersAdapter a, int p, Context c) {
        super();
        adapter = a;
        position = p;
        context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.contact_menu_dialog_fragment, null, false);


        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        listView = (ListView) v.findViewById(R.id.menuListView);

        String[] values = {"Change contact's name", "Remove Contact"};

        listView.setAdapter(new ArrayAdapter<String>(context, R.layout.contact_menu_item, R.id.contact_menu_item_text, values));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    ChangeNameDialogFragment dialog = new ChangeNameDialogFragment();

                    dialog.show(getFragmentManager(), "change_name_dialog");
                    getDialog().dismiss();
                }
                else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete Contact");
                            builder.setMessage("Are you sure you want to delete your contact? You will lose your chat history too.");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.delete(position);
                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            Dialog dialog = builder.create();
                            dialog.show();


                    dismiss();

                }

                }
        });

        return v;
    }
}
