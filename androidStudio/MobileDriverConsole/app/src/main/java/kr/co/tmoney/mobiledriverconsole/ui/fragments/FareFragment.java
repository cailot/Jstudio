package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.StopDialog;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopDialog.PassValueFromStopDialogListener{

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

    private Button mPaymentBtn;

    private NumberPicker mNumberPicker;

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt;

    String[] stops = {"City Hall", "Flinders Station", "St. Kilda", "Lunar Park"};

    boolean[] checkedItems = new boolean[stops.length];

    Context mContext;

    private String[] mNames;

    private String[] mTypes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fare_activity, null);
        mContext = container.getContext();
        initialiseUI(view);

        initialiseStopDetails();
        // initialise Firebase
//        Firebase root = new Firebase(MDCConstants.FIREBASE_HOME);
//        root.child(MDCConstants.FIREBASE_TEST_PATH);
//        root.child("users/mchen/name");



        return view;
    }

    private void initialiseUI(View view) {
        mPriceTxt = (TextView) view.findViewById(R.id.fare_price_txt);
        mOriginTxt = (TextView) view.findViewById(R.id.fare_origin_txt);
        mOriginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOriginDialog();
            }
        });
        mDestinationTxt = (TextView) view.findViewById(R.id.fare_destination_txt);
        mDestinationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDestinationDialog();
            }
        });
        mPaymentBtn = (Button) view.findViewById(R.id.fare_payment_btn);
        mPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showOriginDialog();
            }
        });
        mNumberPicker = (NumberPicker) view.findViewById(R.id.fare_number_picker);
        // set the minimum value of NumberPicker
        mNumberPicker.setMinValue(0);
        // set the maximum value of NumberPicker
        mNumberPicker.setMaxValue(10);
        // get whether the selector wheel wraps when reaching the min/max value
        mNumberPicker.setWrapSelectorWheel(true);
        // event listener for NumberPicker
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int recent) {
                mPriceTxt.setText(" ฿ " + recent* MDCConstants.ADULT_FARE);
            }
        });
    }

    private void initialiseStopDetails(){
        String names = getValue(MDCConstants.STOPS_NAME_IN_ROUTE, "");
        mNames = MDCUtils.convertStringToStringArray(names);
        String types = getValue(MDCConstants.STOPS_TYPE_IN_ROUTE, "");
        mTypes = MDCUtils.convertStringToStringArray(types);

    }

    private void showOriginDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mTypes, MDCConstants.FARE_ORIGIN_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), MDCConstants.ORIGIN_DIALOG_TAG);

    }

    private void showDestinationDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mTypes, MDCConstants.FARE_DESTINATION_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), MDCConstants.DESTINATION_DIALOG_TAG);
    }


    @Override
    public void sendStopName(String name, String type, int request) {
        // update stop info into TextView
        switch (request){
            case MDCConstants.FARE_ORIGIN_REQUEST :
                mOriginTxt.setText(name + " : " + type);
                break;
            case MDCConstants.FARE_DESTINATION_REQUEST:
                mDestinationTxt.setText(name + " : " + type);
                break;
        }
    }



    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }



}
