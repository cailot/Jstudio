package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.StopsDialog;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopsDialog.PassValueFromDialogListener{

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

    private Button mPaymentBtn;

    private NumberPicker mNumberPicker;

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt;

    String[] stops = {"City Hall", "Flinders Station", "St. Kilda", "Lunar Park"};

    boolean[] checkedItems = new boolean[stops.length];

    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fare_activity, null);
        mContext = container.getContext();
        initialiseUI(view);
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

    private void showOriginDialog() {

        FragmentManager fragmentManager = getFragmentManager();
        StopsDialog stopsDialog = new StopsDialog();
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromDialogListener(FareFragment.this, MDCConstants.FARE_ORIGIN_REQUEST);
        stopsDialog.show(fragmentManager, "Origin Stop");

    }

    private void showDestinationDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        StopsDialog stopsDialog = new StopsDialog();
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromDialogListener(FareFragment.this, MDCConstants.FARE_DESTINATION_REQUEST);
        stopsDialog.show(fragmentManager, "Destination Stop");
    }


    @Override
    public void sendStopName(String stop, int request) {
        // update stop info into TextView
        switch (request){
            case MDCConstants.FARE_ORIGIN_REQUEST :
                mOriginTxt.setText(stop);
                break;
            case MDCConstants.FARE_DESTINATION_REQUEST:
                mDestinationTxt.setText(stop);
                break;
        }
    }
}
