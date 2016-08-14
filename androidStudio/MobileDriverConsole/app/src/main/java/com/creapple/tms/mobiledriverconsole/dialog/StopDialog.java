package com.creapple.tms.mobiledriverconsole.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.creapple.tms.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class StopDialog extends DialogFragment{

    // Bind the value between user selection in dialog and stop name on FareFragment
    public interface PassValueFromStopDialogListener{
        void sendStopName(String name, String type, int requestCode);
    }

    public StopDialog() {
    }

    @SuppressLint("ValidFragment")
    public StopDialog(String[] names, String[] types, int requestCode) {
        this.mNames = names;
        this.mTypes = types;
        this.mRequestCode = requestCode;
    }

    /**
     * This interface hooks up FareFragment and StopsDialog by calling
     * 1. calling setPassValueFromDialogListener() from FareFragment
     * 2. passing value via sendStopName() from StopsDialog
     */
    PassValueFromStopDialogListener mPassValueFromStopDialogListener;

    int mRequestCode;

    String[] mNames;// = {"อู่บางพลี", "ทางเข้าสนามบิน", "าง้าสนามน", "ทาโค้ง 1", "โค้ง 2", "แยกสนามบิน", "โค้ง 3", "อู่บางพลี", "ทางเข้าสนามบิน", "าง้าสนามน", "ทาโค้ง 1", "โค้ง 2", "แยกสนามบิน", "โค้ง 3"};
    String[] mTypes;// = {"Zone 1", "Zone 2", "Zone 1", "Zone 1", "Zone 1", "Zone 2", "Zone 2", "Zone 1", "Zone 2", "Zone 1", "Zone 1", "Zone 1", "Zone 2", "Zone 2"};


    public void setPassValueFromStopDialogListener(PassValueFromStopDialogListener passValueFromStopDialogListener){
        mPassValueFromStopDialogListener = passValueFromStopDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stops_dialog, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.stop_grid_view);
        StopCustomAdapter stopsCustomAdapter = new StopCustomAdapter(getActivity(), mNames, mTypes);
        gridView.setAdapter(stopsCustomAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
//                String stopInfo = names[pos] + " : " + zones[pos];
                // update TexView in FareFragment according to user's choice
                mPassValueFromStopDialogListener.sendStopName(mNames==null ? "" : mNames[pos], mTypes==null ? "" : mTypes[pos], mRequestCode);
                dismiss();
            }
        });
        return view;
    }
}
