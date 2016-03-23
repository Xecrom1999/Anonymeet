package com.example.or.anonymeet.GPS;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 12/03/16.
 */
public class PeopleDialog extends DialogFragment {

    private Context ctx;
    private ArrayList<String> list;

    public PeopleDialog(Context ctx, ArrayList<String> list) {
        this.ctx = ctx;
        this.list = list;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle("People");
        builder.setItems((CharSequence[])list.toArray(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ctx, list.get(which), Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
