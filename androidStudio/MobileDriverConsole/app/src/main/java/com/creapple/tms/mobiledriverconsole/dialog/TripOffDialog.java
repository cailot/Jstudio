package com.creapple.tms.mobiledriverconsole.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.fragments.SettingFragment;
import com.creapple.tms.mobiledriverconsole.print.PrinterAdapter;

import java.util.Map;

/**
 * Created by jinseo on 2016. 7. 17..
 */
//public class TripOffDialog extends DialogFragment {
public class TripOffDialog extends ImmersiveDialogFragment{

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;

    private SettingFragment mFragment;

    private PrinterAdapter mPrinterAdapter;

    private Map<String, String> mParams;

    public TripOffDialog() {
    }

    @SuppressLint("ValidFragment")
    public TripOffDialog(Fragment fragment, PrinterAdapter printerAdapter, Map<String, String> map) {
        mFragment = (SettingFragment) fragment;
        mPrinterAdapter = printerAdapter;
        mParams = map;
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
        View view = inflater.inflate(R.layout.trip_off_confirmation, container, false);

        mConfirmTxt = (TextView) view.findViewById(R.id.trip_off_confimation_txt);
        mConfirmBtn = (Button) view.findViewById(R.id.trip_off_confirmation_btn);
        mConfirmBtn.setText(getString(R.string.dialog_confirm));
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //print
            mPrinterAdapter.printTripOff(mParams);
            // tripOff Handle
            mFragment.tripOffHandle();
            // disappear
            dismiss();
            }
        });
        mCancelBtn = (Button) view.findViewById(R.id.trip_off_cancel_btn);
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
