package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.BluetoothMatchingDeviceDialogFragment;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.PassengerDialog;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.StopDialog;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopDialog.PassValueFromStopDialogListener, PassengerDialog.PassValueFromPassengerDialogListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt, mPassengerCountTxt, mPaymentTxt;

    Context mContext;

    private String[] mNames;

    private String[] mTypes;

    private StopVO[] mStops;


    private MDCMainActivity mMainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fare_activity, null);
        mContext = container.getContext();
        mMainActivity = (MDCMainActivity)getActivity();
        // build UI
        initialiseUI(view);
        // load Stops info
        initialiseStopDetails();
        // set up bluetooth printer
        enableBluetooth();

        return view;
    }

    /**
     * set up bluetooth
     */
    private void enableBluetooth() {
        BluetoothAdapter adpater = BluetoothAdapter.getDefaultAdapter();
        if (adpater == null) {
            // Device does not support Bluetooth
        }
        if (!adpater.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        }else{

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mOriginTxt.setText("Current Stop : " + MDCMainActivity.currentStopName);
    }

    /**
     * build up UI components
     * @param view
     */
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
        mPassengerCountTxt = (TextView) view.findViewById(R.id.fare_passenger_cout_txt);
        mPassengerCountTxt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                showPassengerCountDialog();
            }
        });
        mPaymentTxt = (TextView) view.findViewById(R.id.fare_payment_txt);
        mPaymentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBluetoothDialog();
            }
        });

    }


    /**
     * Retrieve stops info from MDCMainActivity
     */
    private void initialiseStopDetails(){

        mStops = mMainActivity.getStops();
        mNames = new String[mStops.length];
        mTypes = new String[mStops.length];
        int i = 0;
        for(StopVO stop : mStops){
            mNames[i] = stop.getName();
            mTypes[i] = stop.getType();
//            Log.d(LOG_TAG, stop.toString());
            i++;
        }
    }


//    /**
//     * Check whether current tab is selected or not
//     * @param isVisibleToUser
//     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(this.isVisible()){
//            if(isVisibleToUser){
//                getClosestStop();
//                Log.d(LOG_TAG, "FareFragment is visible");
//            }else{
//                Log.d(LOG_TAG, "FareFragment is not visible");
//            }
//        }
//    }


    /**
     * Pop up dialog for origin stop
     */
    private void showOriginDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mTypes, Constants.FARE_ORIGIN_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.ORIGIN_DIALOG_TAG);

    }

    /**
     * Pop up dialog for destination stop
     */
    private void showDestinationDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mTypes, Constants.FARE_DESTINATION_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.DESTINATION_DIALOG_TAG);
    }

    /**
     * Pop up dialog for passenger count
     */
    private void showPassengerCountDialog() {
        PassengerDialog passengerDialog = new PassengerDialog();
        // link itself to be updated via 'PassValueFromPassengerDialogListener.sendPassengerCount()'
        passengerDialog.setPassValueFromPassengerDialogListener(FareFragment.this);
        passengerDialog.show(getFragmentManager(), Constants.PASSENGER_DIALOG_TAG);
    }


    /**
     * Pop up dialog for bluetooth printer
     */
    private void showBluetoothDialog() {
        BluetoothMatchingDeviceDialogFragment dialog = new BluetoothMatchingDeviceDialogFragment();
        dialog.show(getFragmentManager(), BluetoothMatchingDeviceDialogFragment.class.getName());
    }

    /**
     * check whether update will happen either origin or destination
     * @param name
     * @param type
     * @param request
     */
    @Override
    public void sendStopName(String name, String type, int request) {
        // update stop info into TextView
        switch (request){
            case Constants.FARE_ORIGIN_REQUEST :
                mOriginTxt.setText(name + " : " + type);
                break;
            case Constants.FARE_DESTINATION_REQUEST:
                mDestinationTxt.setText(name + " : " + type);
                break;
        }
    }

    /**
     * 1. update user's selection on passenger count
     * 2. update Fare Textview
     * @param name
     */
    @Override
    public void sendPassengerCount(String name) {
        mPassengerCountTxt.setText(name);
        mPriceTxt.setText(" ฿ " + Integer.parseInt(StringUtils.defaultString(name, "0"))* Constants.ADULT_FARE);
    }
}
