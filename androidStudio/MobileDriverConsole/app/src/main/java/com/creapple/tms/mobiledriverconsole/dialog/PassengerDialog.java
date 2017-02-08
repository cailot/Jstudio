package com.creapple.tms.mobiledriverconsole.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.dialog.adapter.PassengerCustomAdapter;

/**
 * Created by jinseo on 2016. 6. 30..
 */
//public class PassengerDialog extends DialogFragment{
public class PassengerDialog extends ImmersiveDialogFragment4{

    private Activity mActivity;

    // Bind the value between user selection in dialog and stop name on FareFragment
    public interface PassValueFromPassengerDialogListener{
        void sendPassengerCount(int name);
    }

    public PassengerDialog(){

    }

    @SuppressLint("ValidFragment")
    public PassengerDialog(Activity activity) {
//        this.requestCode = requestCode;
        mActivity = activity;
        mCount = new String[3];
        mCount[0] = mActivity.getResources().getString(R.string.fare_adult_title);
        mCount[1] = mActivity.getResources().getString(R.string.fare_student_title);
        mCount[2] = mActivity.getResources().getString(R.string.fare_senior_title);
    }

    /**
     * This interface hooks up FareFragment and PassengerDialog by calling
     * 1. calling setPassValueFromPassengerDialogListener() from FareFragment
     * 2. passing value via sendPassengerCount() from PassengerDialog
     */
    PassValueFromPassengerDialogListener mPassValueFromPassengerDialogListener;

//    int requestCode;

    String[] mCount;


    public void setPassValueFromPassengerDialogListener(PassValueFromPassengerDialogListener passValueFromPassengerDialogListener){
        mPassValueFromPassengerDialogListener = passValueFromPassengerDialogListener;
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
        View view = inflater.inflate(R.layout.passenger_dialog, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.passenger_count_grid_view);
        PassengerCustomAdapter passengerCustomAdapter = new PassengerCustomAdapter(getActivity(), mCount);
        gridView.setAdapter(passengerCustomAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
//                String stopInfo = names[pos] + " : " + zones[pos];
                // update TexView in FareFragment according to user's choice
//                mPassValueFromPassengerDialogListener.sendPassengerCount(mCount[pos]);
                mPassValueFromPassengerDialogListener.sendPassengerCount(pos);
                dismiss();
            }
        });
        return view;
    }
}
