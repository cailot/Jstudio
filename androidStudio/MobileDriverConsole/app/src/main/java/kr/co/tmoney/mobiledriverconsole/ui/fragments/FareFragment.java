package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopGroupVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
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

    private StopVO[] mStops;

    private String[] mNames;

    private StopGroupVO[] groups;

    private String[] mStopGroups;

    private String[] mFares;


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
        initialiseInfo();
        // set up bluetooth printer

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mOriginTxt.setText("Current Stop : " + MDCMainActivity.currentStopName);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
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
//                sendPrintCommand();
            }
        });

    }


    /**
     * Retrieve stops info from MDCMainActivity
     */
    private void initialiseInfo(){

        mStops = mMainActivity.getStops();

        groups = getStopGroupsInfo();

        mNames = new String[mStops.length];
        mStopGroups = new String[mStops.length];
        int i = 0;
        for(StopVO stop : mStops){
            mNames[i] = stop.getName();
            mStopGroups[i] = getGroup(stop.getFareStopTag());
            i++;
        }
        mFares = getFareInfo();
    }

    /**
     * Get group name by using fareStopTag in StopVO
     * @param index
     * @return
     */
    private String getGroup(int index){
        String group = "";
        for(int i=0; i<groups.length; i++){
            if(index==groups[i].getIndex())
            {
                group = groups[i].getName();
                break;
            }
        }
        return group;
    }

    /**
     * Get fareStopTag by using name in StopVO
     * @param name
     * @return
     */
    private int getStopTag(String name){
        int tag = 0;
        for(int i=0; i<mStops.length; i++){
            if(name.equalsIgnoreCase(mStops[i].getName()))
            {
                tag = mStops[i].getFareStopTag();
                break;
            }
        }
        return tag;
    }

    /**
     * Calculate fare based on origin & destination
     * @param origin
     * @param destination
     * @return
     */
    private int calculateFare(String origin, String destination){
        int price = 0;
        // 1. get origin group index
        int originGroup = getStopTag(origin);
        // 2. get destination group index
        int destinationGroup = getStopTag(destination);
        // 3. get fare info by using origin index
        String originLine = mFares[originGroup];
        // 4. make fare info to array
        String[] fares = StringUtils.split(originLine, ",");
        // 5. calculate difference from destination to origin, destination index should be greater than or equal to origin index
        int difference = destinationGroup - originGroup;
        // Exceptional case >>> such as driving backward for some reason, we will apply minimum fare for customer's convineince
        if(difference<0){
            difference = 0;
        }
        // 6. calculate final fare per person
        price = Integer.parseInt(fares[difference]);
        // 7. check how many passengers need the fare
        int num = Integer.parseInt(mPassengerCountTxt.getText().toString());
        // 8. return fare in total
        return price * num;
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
        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups, Constants.FARE_ORIGIN_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.ORIGIN_DIALOG_TAG);

    }

    /**
     * Pop up dialog for destination stop
     */
    private void showDestinationDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups, Constants.FARE_DESTINATION_REQUEST);
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
    

    // /**
    //  * Pop up dialog for bluetooth printer
    //  */
    // private void showBluetoothDialog() {
    //     BluetoothMatchingDeviceDialogFragment dialog = new BluetoothMatchingDeviceDialogFragment();
    //     dialog.show(getFragmentManager(), BluetoothMatchingDeviceDialogFragment.class.getName());
    // }

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

    /**
     * Retreive stop groups info under route
     * @return
     */
    public StopGroupVO[] getStopGroupsInfo() {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String json = pref.getString(Constants.STOP_GROUPS_IN_ROUTE, null);
        StopGroupVO[] stopGroups = new Gson().fromJson(json, StopGroupVO[].class);
        return stopGroups;
    }

    /**
     * Retreive fares info under route
     * @return
     */
    public String[] getFareInfo() {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String json = pref.getString(Constants.FARES_IN_ROUTE, null);
        String[] fares = new Gson().fromJson(json, String[].class);
        return fares;
    }
}
