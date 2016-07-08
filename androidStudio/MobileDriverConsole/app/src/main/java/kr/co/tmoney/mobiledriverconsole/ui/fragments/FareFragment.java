package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.StopDialog;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopDialog.PassValueFromStopDialogListener{

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

    private Button mPaymentBtn;

    private NumberPicker mNumberPicker;

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt;

    Context mContext;

    private String[] mNames;

    private String[] mTypes;

    private StopVO[] mStops;

    private StopVO mClosestStop;

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
        return view;
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
                mPriceTxt.setText(" ฿ " + recent* Constants.ADULT_FARE);
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


    /**
     * Check whether current tab is selected or not
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(this.isVisible()){
            if(isVisibleToUser){
                getClosestStop();
                Log.d(LOG_TAG, "FareFragment is visible");
            }else{
                Log.d(LOG_TAG, "FareFragment is not visible");
            }
        }
    }


    /**
     * Search and get closest stop info
     */
    private void getClosestStop() {
        // 1. get current GPS
        double lat = mMainActivity.getCurrentLat();
        double lon = mMainActivity.getCurrentLon();
        double[] gps = new double[mStops.length];
        // 2. calculate distance
        int i = 0;
        for(StopVO stop : mStops){
            //13.8577,100.626699
             double distance = MDCUtils.getDistanceMeters(lat, lon, stop.getLat(), stop.getLon());
            //double distance = MDCUtils.getDistanceMeters(13.8577,100.626699, stop.getLat(), stop.getLon());
            Log.e(LOG_TAG, stop.getId() + " : " + distance);
            gps[i] = distance;
            i++;
        }
        int index = MDCUtils.getMinDistanceIndex(gps);
        mClosestStop = mStops[index];
        // auto select closest stop in Origin
        mOriginTxt.setText(mClosestStop.getName() + " : " + mClosestStop.getType());
    }


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

}
