package com.creapple.tms.mobiledriverconsole.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creapple.tms.mobiledriverconsole.MDCMainActivity;
import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.dialog.PassengerDialog;
import com.creapple.tms.mobiledriverconsole.dialog.PrintConfirmationDialog;
import com.creapple.tms.mobiledriverconsole.dialog.StopDialog;
import com.creapple.tms.mobiledriverconsole.dialog.ZoneDialog;
import com.creapple.tms.mobiledriverconsole.model.vo.StopGroupVO;
import com.creapple.tms.mobiledriverconsole.model.vo.StopVO;
import com.creapple.tms.mobiledriverconsole.print.PrinterAdapter;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopDialog.PassValueFromStopDialogListener, ZoneDialog.PassValueFromZoneDialogListener, PassengerDialog.PassValueFromPassengerDialogListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt, mPassengerTxt, mPaymentTxt;

    public Context mContext;

    private StopVO[] mStops; // all stops info as array

    private StopGroupVO[] mGroups; // all stop groups info as array

    private String[] mNames; // stop names to pop up dialog

    private String[] mStopGroups; // group names to pop up dialog

    private String[] mAdultFares, mSeniorFares, mStudentFares; // all fare info as array

    private String mOriginStop; // original stop

    private String mDestinationZone; // destination zone

    private int mDestinationGroup; // destination group

//    private int mAdultPrice, mSeniorPrice, mStudentPrice; // price per each

    private int mAdultCount, mSeniorCount, mStudentCount; // count for each

    private int mAdultTotalFare, mSeniorTotalFare, mStudentTotalFare, mTotalFare;

    private String mRouteId; // need it for printing

    private String mVehicleId; // need it for printing

    private MDCMainActivity mMainActivity; // MainActivity to get current stop when start

    private PrinterAdapter mPrinterAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fare_activity, null);

        mContext = container.getContext();
        mMainActivity = (MDCMainActivity) getActivity();

        mRouteId = MDCUtils.getValue(mContext, Constants.ROUTE_ID, getString(R.string.no_route_found));
        mVehicleId = MDCUtils.getValue(mContext, Constants.VEHICLE_NAME, getString(R.string.no_vehicle_found));

        // build UI
        initialiseUI(view);
        // load Stops info
        initialiseInfo();
        // set up bluetooth printer
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        mPrinterAdapter = new PrinterAdapter(this, bluetoothAdapter);
        mPrinterAdapter = mMainActivity.getPrinterAdapter();

        return view;
    }


    /**
     * clean & reset all field
     */
    private void resetData() {
        mOriginStop = MDCMainActivity.currentStopName.trim();
        int index = getStopTag(mOriginStop);

        SpannableStringBuilder spannable = new SpannableStringBuilder();

        String legendO = getString(R.string.fare_origin_legend);
        SpannableString legendSO = new SpannableString(legendO);
        legendSO.setSpan(new ForegroundColorSpan(Color.BLACK), 0, legendO.length(), 0);
        legendSO.setSpan(new StyleSpan(Typeface.BOLD), 0, legendO.length(), 0);
        spannable.append(legendSO);
        spannable.append("\t");

        SpannableString nameSO = new SpannableString(mOriginStop);
        nameSO.setSpan(new ForegroundColorSpan(Color.WHITE), 0, mOriginStop.length(), 0);
        nameSO.setSpan(new StyleSpan(Typeface.BOLD), 0, mOriginStop.length(), 0);
        spannable.append(nameSO);
        spannable.append("\t\t");

        String type = mStopGroups[index];
        SpannableString groupSO = new SpannableString(type);
        groupSO.setSpan(new ForegroundColorSpan(Color.BLACK), 0, type.length(), 0);
        groupSO.setSpan(new StyleSpan(Typeface.BOLD), 0, type.length(), 0);
        spannable.append(groupSO);

        mOriginTxt.setText(spannable);

        mDestinationTxt.setText(getResources().getString(R.string.fare_destination_title));

        mPassengerTxt.setText(getResources().getString(R.string.fare_passenger_title));
        mPassengerTxt.setClickable(false);


        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
        mPaymentTxt.setText(getResources().getString(R.string.fare_payment_title));
        mPaymentTxt.setClickable(false);
        mAdultTotalFare = 0;
        mSeniorTotalFare = 0;
        mStudentTotalFare = 0;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * build up UI components
     *
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
        mPassengerTxt = (TextView) view.findViewById(R.id.fare_passenger_txt);
        mPassengerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPassengerCountDialog();
            }
        });
        mPaymentTxt = (TextView) view.findViewById(R.id.fare_payment_txt);
        mPaymentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPrintCommand();
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        mPrinterAdapter.stopConnection();
    }


    /**
     * Send print command to bluetooth printer
     */
    private void sendPrintCommand() {
        SpannableStringBuilder stringBuilder = getPrintMessage();
        Map map = new HashMap<String, String>();
        map.put(Constants.PRINT_TICKET_NUMBER, StringUtils.leftPad(Integer.toString(MDCMainActivity.fareTransactionId), 5, "0"));
        map.put(Constants.PRINT_DATE, MDCUtils.getTimestamp());
        map.put(Constants.PRINT_ROUTE, mRouteId);
        map.put(Constants.PRINT_BUS, mVehicleId);
        map.put(Constants.PRINT_FROM, mOriginStop);
        map.put(Constants.PRINT_TO, mDestinationZone);
        map.put(Constants.PRINT_ADULT_NUMBER_OF_PERSON,  mAdultCount + "");
        map.put(Constants.PRINT_ADULT_TOTAL, mAdultTotalFare + "");
        map.put(Constants.PRINT_SENIOR_NUMBER_OF_PERSON,  mSeniorCount + "");
        map.put(Constants.PRINT_SENIOR_TOTAL, mSeniorTotalFare + "");
        map.put(Constants.PRINT_STUDENT_NUMBER_OF_PERSON, mStudentCount + "");
        map.put(Constants.PRINT_STUDENT_TOTAL, mStudentTotalFare + "");

        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = map.get(key).toString();

            Log.d(LOG_TAG, key + " " + value);
        }

        PrintConfirmationDialog printConfirmationDialog = new PrintConfirmationDialog(mContext, mPrinterAdapter, stringBuilder, map);
        printConfirmationDialog.setMainActivity(mMainActivity);
        //printConfirmationDialog.show();
        printConfirmationDialog.show(mMainActivity.getFragmentManager(), Constants.PRINT_DIALOG_TAG);
    }


    /**
     * Retrieve stops info from MDCMainActivity
     */
    private void initialiseInfo() {

        mStops = mMainActivity.getStops();

        mGroups = getStopGroupsInfo();

        mNames = new String[mStops.length];
        mStopGroups = new String[mStops.length];
        int i = 0;
        for (StopVO stop : mStops) {
            mNames[i] = stop.getName();
            mStopGroups[i] = getGroup(stop.getFareStopTag());
            i++;
        }
        getFareInfo();
    }


    /**
     * Get group name by using fareStopTag in StopVO
     *
     * @param index
     * @return
     */
    private String getGroup(int index) {
        String group = "";
        for (int i = 0; i < mGroups.length; i++) {
            if (index == mGroups[i].getIndex()) {
                group = mGroups[i].getName();
                break;
            }
        }
        return group;
    }


    /**
     * Get fareStopTag by using name in StopVO
     *
     * @param name
     * @return
     */
    private int getStopTag(String name) {
        int tag = 0;
        for (int i = 0; i < mStops.length; i++) {
            if (name.equalsIgnoreCase(mStops[i].getName())) {
                tag = mStops[i].getFareStopTag();
                break;
            }
        }
        return tag;
    }


    /**
     * Get stopGroupIndex for destination stop group
     * @param name
     * @return
     */
    private int getStopGroupIndex(String name){
        int index = 0;
        for(StopGroupVO stopGroup : mGroups){
            if(StringUtils.equalsIgnoreCase(stopGroup.getName(), name)){
                index = stopGroup.getIndex();
            }
        }
        return index;
    }

    /**
     * It returns possible list of destination group with fare.
     * This information will be popped up when clicks destination group.
     *
     * @return
     */
    private List<String> listAdultFare() {

        int originGroup = getStopTag(mOriginStop);

        List<String> fares = new ArrayList<String>();
        for (StopGroupVO fare : mGroups) {
            int index = fare.getIndex();
            if(index >= originGroup){
                String name = fare.getName();
                int adultFare = calculateAdultFare(index);
                fares.add(adultFare + Constants.FARE_SEPARATOR + name);
            }
        }
        return fares;
    }

    private int calculateAdultFare(int destinationGroup) {
//        mAdultPrice = 0;

        // 1. get origin group index
        int originGroup = getStopTag(mOriginStop);
        // 2. get destination group index

        // this will not happen in real life but just to avoid NPE in testing data
        if (originGroup >= mAdultFares.length) {
            originGroup = mAdultFares.length - 1;
        }
        if (destinationGroup >= mAdultFares.length) {
            destinationGroup = mAdultFares.length - 1;
        }
        // 3. get fare info by using origin index
        String originLine = mAdultFares[originGroup];
        // 4. make fare info to array
        String[] fares = StringUtils.split(originLine, ",");
        // 5. calculate difference from destination to origin, destination index should be greater than or equal to origin index
        int difference = destinationGroup - originGroup;
        // Exceptional case >>> such as driving backward for some reason, we will apply minimum fare for customer's convineince
        if (difference < 0) {
            difference = 0;
        }
        // 6. calculate final fare per person
//        mAdultPrice = Integer.parseInt(fares[difference].trim());
        mAdultTotalFare = Integer.parseInt(fares[difference].trim());


        //        Log.e(LOG_TAG, "price ==> " + price);
        // 7. check how many passengers need the fare
        // 8. return fare in total
//        mAdultTotalFare = mAdultPrice * 1;
        return mAdultTotalFare;
    }

    private int calculateAdultFare() {
//        mAdultPrice = 0;

        // 1. get origin group index
        int originGroup = getStopTag(mOriginStop);
        // 2. get destination group index
        int destinationGroup = mDestinationGroup;

        // this will not happen in real life but just to avoid NPE in testing data
        if (originGroup >= mAdultFares.length) {
            originGroup = mAdultFares.length - 1;
        }
        if (destinationGroup >= mAdultFares.length) {
            destinationGroup = mAdultFares.length - 1;
        }
        // 3. get fare info by using origin index
        String originLine = mAdultFares[originGroup];
        // 4. make fare info to array
        String[] fares = StringUtils.split(originLine, ",");
        // 5. calculate difference from destination to origin, destination index should be greater than or equal to origin index
        int difference = destinationGroup - originGroup;
        // Exceptional case >>> such as driving backward for some reason, we will apply minimum fare for customer's convineince
        if (difference < 0) {
            difference = 0;
        }
        // 6. calculate final fare per person
        mAdultTotalFare = Integer.parseInt(fares[difference].trim());

        // 7. check how many passengers need the fare
        // 8. return fare in total
//        mAdultTotalFare = mAdultPrice * 1;
        return mAdultTotalFare;
//        }
    }

    private int calculateSeniorFare() {
//        mSeniorPrice = 0;
        // 1. get origin group index
        int originGroup = getStopTag(mOriginStop);
        // 2. get destination group index
//        int destinationGroup = getStopTag(mDestinationStop);
        int destinationGroup = mDestinationGroup;
        // this will not happen in real life but just to avoid NPE in testing data
        if (originGroup >= mSeniorFares.length) {
            originGroup = mSeniorFares.length - 1;
        }
        if (destinationGroup >= mSeniorFares.length) {
            destinationGroup = mSeniorFares.length - 1;
        }
        // 3. get fare info by using origin index
        String originLine = mSeniorFares[originGroup];
        // 4. make fare info to array
        String[] fares = StringUtils.split(originLine, ",");
        // 5. calculate difference from destination to origin, destination index should be greater than or equal to origin index
        int difference = destinationGroup - originGroup;
        // Exceptional case >>> such as driving backward for some reason, we will apply minimum fare for customer's convineince
        if (difference < 0) {
            difference = 0;
        }
        // 6. calculate final fare per person
        mSeniorTotalFare = Integer.parseInt(fares[difference].trim());

        // 7. check how many passengers need the fare
        // 8. return fare in total
//        mSeniorTotalFare = mSeniorPrice * 1;
        return mSeniorTotalFare;
//        }
    }

    private int calculateStudentFare() {
//        mStudentPrice = 0;

        // 1. get origin group index
        int originGroup = getStopTag(mOriginStop);
        // 2. get destination group index
//        int destinationGroup = getStopTag(mDestinationStop);
        int destinationGroup = mDestinationGroup;
        // this will not happen in real life but just to avoid NPE in testing data
        if (originGroup >= mStudentFares.length) {
            originGroup = mStudentFares.length - 1;
        }
        if (destinationGroup >= mStudentFares.length) {
            destinationGroup = mStudentFares.length - 1;
        }
        // 3. get fare info by using origin index
        String originLine = mStudentFares[originGroup];
        // 4. make fare info to array
        String[] fares = StringUtils.split(originLine, ",");
        // 5. calculate difference from destination to origin, destination index should be greater than or equal to origin index
        int difference = destinationGroup - originGroup;
        // Exceptional case >>> such as driving backward for some reason, we will apply minimum fare for customer's convineince
        if (difference < 0) {
            difference = 0;
        }
        // 6. calculate final fare per person
        mStudentTotalFare = Integer.parseInt(fares[difference].trim());

        // 7. check how many passengers need the fare
        // 8. return fare in total
//        mStudentTotalFare = mStudentPrice * 1;
        return mStudentTotalFare;
//        }
    }


    /**
     * Pop up dialog for origin stop
     */
    private void showOriginDialog() {
        mPassengerTxt.setText(getResources().getString(R.string.fare_adult_title));
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.ORIGIN_DIALOG_TAG);

    }


    /**
     * Pop up dialog for destination stop
     */
    private void showDestinationDialog() {
        mPassengerTxt.setText(getResources().getString(R.string.fare_adult_title));
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
        List<String> fareInfo = listAdultFare();
        int[] adultFares = new int[fareInfo.size()];
        String[] fareZones = new String[fareInfo.size()];
        for(int i=0; i<fareInfo.size(); i++){
            String[] value = StringUtils.splitByWholeSeparator((String)fareInfo.get(i), Constants.FARE_SEPARATOR);
            adultFares[i] = Integer.parseInt(value[0]);
            fareZones[i] = value[1];
//            Log.d(LOG_TAG, adultFares[i] + "\t" + fareZones[i]);
        }
        ZoneDialog zoneDialog = new ZoneDialog(adultFares, fareZones);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        zoneDialog.setPassValueFromZoneDialogListener(FareFragment.this);
        zoneDialog.show(getFragmentManager(), Constants.DESTINATION_DIALOG_TAG);
    }


    /**
     * Pop up dialog for passenger count
     */
    private void showPassengerCountDialog() {
        PassengerDialog passengerDialog = new PassengerDialog(mMainActivity);
        // link itself to be updated via 'PassValueFromPassengerDialogListener.sendPassengerCount()'
        passengerDialog.setPassValueFromPassengerDialogListener(FareFragment.this);
        passengerDialog.show(getFragmentManager(), Constants.PASSENGER_DIALOG_TAG);
    }


    /**
     * check whether update will happen either origin or destination
     *
     * @param name
     * @param type
     */
    @Override
    public void sendStopName(String name, String type) {

        SpannableStringBuilder spannable = new SpannableStringBuilder();
        // update stop info into TextView
//        switch (request){
//            case Constants.FARE_ORIGIN_REQUEST :
        mOriginStop = name;

        String legendO = getString(R.string.fare_origin_legend);
        SpannableString legendSO = new SpannableString(legendO);
        legendSO.setSpan(new ForegroundColorSpan(Color.BLACK), 0, legendO.length(), 0);
        legendSO.setSpan(new StyleSpan(Typeface.BOLD), 0, legendO.length(), 0);
        spannable.append(legendSO);
        spannable.append("\t");

        SpannableString nameSO = new SpannableString(name);
        nameSO.setSpan(new ForegroundColorSpan(Color.WHITE), 0, name.length(), 0);
        nameSO.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), 0);
        spannable.append(nameSO);
        spannable.append("\t\t");

        SpannableString groupSO = new SpannableString(type);
        groupSO.setSpan(new ForegroundColorSpan(Color.BLACK), 0, type.length(), 0);
        groupSO.setSpan(new StyleSpan(Typeface.BOLD), 0, type.length(), 0);
        spannable.append(groupSO);

        mOriginTxt.setText(spannable);
        mDestinationTxt.setText(getResources().getString(R.string.fare_destination_title));
        mPassengerTxt.setClickable(false);
        mPaymentTxt.setClickable(false);
    }

    @Override
    public void sendZoneName(int fare, String zone) {
        mDestinationGroup = getStopGroupIndex(zone);
        mDestinationZone = zone;
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        String legendD = getString(R.string.fare_destination_legend);
        SpannableString legendSD = new SpannableString(legendD);
        legendSD.setSpan(new ForegroundColorSpan(Color.BLACK), 0, legendD.length(), 0);
        legendSD.setSpan(new StyleSpan(Typeface.BOLD), 0, legendD.length(), 0);
        spannable.append(legendSD);
        spannable.append("\t");

        SpannableString nameSD = new SpannableString(zone);
        nameSD.setSpan(new ForegroundColorSpan(Color.WHITE), 0, zone.length(), 0);
        nameSD.setSpan(new StyleSpan(Typeface.BOLD), 0, zone.length(), 0);
        spannable.append(nameSD);

//        mAdultPrice = fare;
        mAdultTotalFare = fare;
//        mSeniorPrice = 0;
        mSeniorTotalFare = 0;
//        mStudentPrice = 0;
        mStudentTotalFare = 0;
        mAdultCount = 1;
        mSeniorCount = 0;
        mStudentCount = 0;

        mDestinationTxt.setText(spannable);
        updateFareText(fare);
        mPassengerTxt.setClickable(true);
        mPaymentTxt.setClickable(true);
    }

    /**
     * check whether update will happen in fares
     *
     * @param requestCode
     */
    @Override
    public void sendPassengerCount(int requestCode) {
        String name = "";
        int fare = 0;
        switch (requestCode) {
            case Constants.ADULT_FARE_REQUEST:
                name = getString(R.string.fare_adult_title);
//                mSeniorPrice = 0;
                mSeniorTotalFare = 0;
//                mStudentPrice = 0;
                mStudentTotalFare = 0;
                mAdultCount = 1;
                mSeniorCount = 0;
                mStudentCount = 0;
                fare = calculateAdultFare();
                break;
            case Constants.SENIOR_FARE_REQUEST:
                name = getString(R.string.fare_senior_title);
//                mAdultPrice = 0;
                mAdultTotalFare = 0;
//                mStudentPrice = 0;
                mStudentTotalFare = 0;
                mAdultCount = 0;
                mSeniorCount = 1;
                mStudentCount = 0;
                fare = calculateSeniorFare();
                break;
            case Constants.STUDENT_FARE_REQUEST:
                name = getString(R.string.fare_student_title);
//                mAdultPrice = 0;
                mAdultTotalFare = 0;
//                mSeniorPrice = 0;
                mSeniorTotalFare = 0;
                mAdultCount = 0;
                mSeniorCount = 0;
                mStudentCount = 1;
                fare = calculateStudentFare();
                break;
        }
        mPassengerTxt.setText(name);
        mTotalFare = fare;
        mPaymentTxt.setClickable(true);
//        Log.d(LOG_TAG, "Returned ---> " + name);
        updateFareText();
    }

    /**
     * update Fare Textview
     */
    public void updateFareText() {
        String price = " ฿ " + mTotalFare + "";
        SpannableString fare = new SpannableString(price);
        fare.setSpan(new RelativeSizeSpan(2f), 0, price.length(), 0);
        fare.setSpan(new StyleSpan(Typeface.BOLD), 0, price.length(), 0);
        mPriceTxt.setText(fare);
    }

    private void updateFareText(int cost) {
        String price = " ฿ " + cost + "";
        SpannableString fare = new SpannableString(price);
        fare.setSpan(new RelativeSizeSpan(2f), 0, price.length(), 0);
        fare.setSpan(new StyleSpan(Typeface.BOLD), 0, price.length(), 0);
        mPriceTxt.setText(fare);
    }


    /**
     * Retreive stop groups info under route
     *
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
     *
     * @return
     */
    public void getFareInfo() {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(Constants.ADULT_FARES_IN_ROUTE, null);
        mAdultFares = gson.fromJson(json, String[].class);
        json = pref.getString(Constants.SENIOR_FARES_IN_ROUTE, null);
        mSeniorFares = gson.fromJson(json, String[].class);
        json = pref.getString(Constants.STUDENT_FARES_IN_ROUTE, null);
        mStudentFares = gson.fromJson(json, String[].class);

//        Log.d(LOG_TAG, "Adult " + ArrayUtils.toString(mAdultFares));
//        Log.d(LOG_TAG, "Senior " + ArrayUtils.toString(mSeniorFares));
//        Log.d(LOG_TAG, "Student " + ArrayUtils.toString(mStudentFares));
    }


    /**
     * Check whether current tab is selected or not
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            String lang = MDCUtils.getValue(mContext, Constants.SELECTED_LANGUAGE, "en");

            if (isVisibleToUser) {
                resetData();
//                Log.d(LOG_TAG, "FareFragment is visible");
            } else {
//                Log.d(LOG_TAG, "FareFragment is not visible");
            }
        }
    }


    /**
     * Decorate print message for Dialog
     *
     * @return
     */
    public SpannableStringBuilder getPrintMessage() {
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        spannable.append("หมายเลขตั๋ว: ");
        spannable.append(getSpannableString(StringUtils.leftPad(Integer.toString(MDCMainActivity.fareTransactionId), 5, "0")));
        spannable.append("\t");
        spannable.append("วันเวลา: ");
        spannable.append(getSpannableString(MDCUtils.getTimestamp()));
        spannable.append("\n");
        spannable.append("สาย : ");
        spannable.append(getSpannableString(mRouteId));
        spannable.append("\t");
        spannable.append("   หมายเลขรถ : ");
        spannable.append(getSpannableString(mVehicleId));
        spannable.append("\n");
        spannable.append("ต้นทาง : ");
        spannable.append(getSpannableString(mOriginStop));
        spannable.append("\t");
        spannable.append("   ปลายทาง : ");
        spannable.append(getSpannableString(mDestinationZone));
        spannable.append("\n");
        if(mAdultCount > 0) {
            spannable.append(" ค่าโดยสารรวมผู้ใหญ่ : ");// sum
            spannable.append(getSpannableString(Integer.toString(mAdultTotalFare)));
            spannable.append("\n");
        }
        if(mSeniorCount > 0) {
            spannable.append(" ค่าโดยสารอาวุโสทั้งหมด : ");
            spannable.append(getSpannableString(Integer.toString(mSeniorTotalFare)));
            spannable.append("\n");
        }
        if(mStudentCount > 0) {
            spannable.append(" นักเรียนราคารวม : ");
            spannable.append(getSpannableString(Integer.toString(mStudentTotalFare)));
            spannable.append("\n");
        }
        spannable.append("      ขอบคุณที่ใช้บริการ");

        return spannable;
    }


    public SpannableString getSpannableString(String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new RelativeSizeSpan(1.0f), 0, str.length(), 0);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, str.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, str.length(), 0);
        return spannableString;
    }

}
