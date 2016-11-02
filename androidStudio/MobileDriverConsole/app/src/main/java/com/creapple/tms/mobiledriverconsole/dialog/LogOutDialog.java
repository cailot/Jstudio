package com.creapple.tms.mobiledriverconsole.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class LogOutDialog extends DialogFragment {

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;

    private Activity mActivity;

    public LogOutDialog() {
    }

    @SuppressLint("ValidFragment")
    public LogOutDialog(Activity activity) {
        mActivity = activity;
    }

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
        View view = inflater.inflate(R.layout.log_out_confirmation, container, false);

        mConfirmTxt = (TextView) view.findViewById(R.id.log_out_confimation_txt);
        mConfirmBtn = (Button) view.findViewById(R.id.log_out_confirmation_btn);
        mConfirmBtn.setText(getString(R.string.dialog_confirm));
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase logout
                // update logout time
                String userUid = MDCUtils.getValue(mActivity.getApplicationContext(), Constants.USER_UID, "");
                String userPath = MDCUtils.getValue(mActivity.getApplicationContext(), Constants.USER_PATH, "");
                if(!userUid.equalsIgnoreCase("") && !userPath.equalsIgnoreCase("")) {
                    Firebase currentUser = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_USER_LIST_PATH + "/" + userUid);
                    // update logIn flag from true to false
                    currentUser.child(Constants.USER_LOGIN).setValue(false);

                    // update logout timestamp
                    Firebase currentPath = currentUser.child(userPath);
                    Map<String, Object> userData = new HashMap<String, Object>();
                    userData.put(Constants.AUTH_LOG_OUT_TIME, ServerValue.TIMESTAMP);
                    currentPath.updateChildren(userData);
                }

                // update front Vehicle's rearVehicle value as "" if exists
                String frontCar = MDCUtils.getValue(mActivity.getApplicationContext(), Constants.VEHICLE_FRONT, "");
                if(!frontCar.equalsIgnoreCase("")){
                    Firebase frontVehicle = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + frontCar);
                    Map<String, Object> frontTripOn = new HashMap<String, Object>();
                    frontTripOn.put(Constants.VEHICLE_REAR, "");
                    //frontTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
                    frontVehicle.updateChildren(frontTripOn);
                }

                // update rear Vehicle's frontVehicle value as "" if exists
                String rearCar = MDCUtils.getValue(mActivity.getApplicationContext(), Constants.VEHICLE_REAR, "");
                if(!rearCar.equalsIgnoreCase("")){
                    Firebase rearVehicle = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + rearCar);
                    Map<String, Object> rearTripOn = new HashMap<String, Object>();
                    rearTripOn.put(Constants.VEHICLE_FRONT, "");
                    //frontTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
                    rearVehicle.updateChildren(rearTripOn);
                }

                // logout
                FirebaseAuth.getInstance().signOut();

                // finish all activity
                mActivity.finishAffinity();

                // disappear
                dismiss();
            }
        });
        mCancelBtn = (Button) view.findViewById(R.id.log_out_cancel_btn);
        mCancelBtn.setText(getString(R.string.dialog_cancel));
        mCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
