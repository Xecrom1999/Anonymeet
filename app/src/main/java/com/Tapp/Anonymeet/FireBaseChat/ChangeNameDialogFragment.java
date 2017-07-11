package com.Tapp.Anonymeet.FireBaseChat;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.Tapp.Anonymeet.R;

/**
 * Created by Or on 11/07/2017.
 */

public class ChangeNameDialogFragment extends DialogFragment {

    public ChangeNameDialogFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.change_name_dialog_fragment, null, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return v;
    }
}
