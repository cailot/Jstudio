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
public class ZoneDialog extends DialogFragment{

    // Bind the value between user selection in dialog and stop name on FareFragment
    public interface PassValueFromZoneDialogListener {
        void sendZoneName(int name, String type);
    }

    public ZoneDialog() {
    }

    @SuppressLint("ValidFragment")
    public ZoneDialog(int[] fares, String[] zones) {
        this.mAdultFare = fares;
        this.mZoneName = zones;
    }

    /**
     * This interface hooks up FareFragment and StopsDialog by calling
     * 1. calling setPassValueFromDialogListener() from FareFragment
     * 2. passing value via sendDestinationZoneName() from DestinationZoneDialog
     */
    PassValueFromZoneDialogListener mPassValueFromZoneDialogListener;

    int[] mAdultFare;// = {21, 23, 23, 25}
    String[] mZoneName;// = {"Zone 1", "Zone 2", "Zone 3", "Zone 4"};


    public void setPassValueFromZoneDialogListener(PassValueFromZoneDialogListener passValueFromZoneDialogListener){
        mPassValueFromZoneDialogListener = passValueFromZoneDialogListener;
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
        View view = inflater.inflate(R.layout.zones_dialog, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.zone_grid_view);
        ZoneCustomAdapter zonesCustomAdapter = new ZoneCustomAdapter(getActivity(), mAdultFare, mZoneName);
        gridView.setAdapter(zonesCustomAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
//                String stopInfo = names[pos] + " : " + zones[pos];
                // update TexView in FareFragment according to user's choice
                mPassValueFromZoneDialogListener.sendZoneName(mAdultFare ==null ? 0 : mAdultFare[pos], mZoneName ==null ? "" : mZoneName[pos]);
                dismiss();
            }
        });
        return view;
    }
}
