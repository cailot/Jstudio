package com.creapple.tms.mobiledriverconsole.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import com.creapple.tms.mobiledriverconsole.model.vo.StopGroupVO;
import com.creapple.tms.mobiledriverconsole.model.vo.StopVO;
import com.creapple.tms.mobiledriverconsole.print.PrinterAdapter;
import com.creapple.tms.mobiledriverconsole.print.PrinterViewAction;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
import com.google.gson.Gson;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.apache.log4j.Logger;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopDialog.PassValueFromStopDialogListener, PassengerDialog.PassValueFromPassengerDialogListener, PrinterViewAction {

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt, mAdultTxt, mSeniorTxt, mStudentTxt, mAdultPriceTxt, mSeniorPriceTxt, mStudentPriceTxt, mPaymentTxt;

    Context mContext;

    private StopVO[] mStops; // all stops info as array

    private StopGroupVO[] mGroups; // all stop groups info as array

    private String[] mNames; // stop names to pop up dialog

    private String[] mStopGroups; // group names to pop up dialog

    private String[] mAdultFares, mSeniorFares, mStudentFares; // all fare info as array

    private String mOriginStop; // original stop

    private String mDestinationStop; // destination stop

    private int mAdultPrice, mSeniorPrice, mStudentPrice; // price per each

    private int mAdultCount, mSeniorCount, mStudentCount; // passenger count

    private int mAdultTotalFare, mSeniorTotalFare, mStudentTotalFare, mTotalFare;

    private String mRouteId; // need it for printing

    private String mVehicleId; // need it for printing

    private MDCMainActivity mMainActivity; // MainActivity to get current stop when start

    private PrinterAdapter mPrinterAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fare_activity, null);
        mContext = container.getContext();
        mMainActivity = (MDCMainActivity)getActivity();

        mRouteId = MDCUtils.getValue(mContext, Constants.ROUTE_ID, getString(R.string.no_route_found));
        mVehicleId = MDCUtils.getValue(mContext, Constants.VEHICLE_NAME, getString(R.string.no_vehicle_found));

        // build UI
        initialiseUI(view);
        // load Stops info
        initialiseInfo();
        // set up bluetooth printer
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mPrinterAdapter = new PrinterAdapter(this, bluetoothAdapter);

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

        mAdultTxt.setText(getResources().getString(R.string.fare_adult_title));
        mSeniorTxt.setText(getResources().getString(R.string.fare_senior_title));
        mStudentTxt.setText(getResources().getString(R.string.fare_student_title));

        mAdultPriceTxt.setText("0");
        mSeniorPriceTxt.setText("0");
        mStudentPriceTxt.setText("0");
        mAdultPriceTxt.setClickable(false);
        mSeniorPriceTxt.setClickable(false);
        mStudentPriceTxt.setClickable(false);


        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
        mPaymentTxt.setText(getResources().getString(R.string.fare_payment_title));
        mPaymentTxt.setClickable(false);
        mAdultCount = 0;
        mSeniorCount = 0;
        mStudentCount = 0;
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
        mAdultTxt = (TextView) view.findViewById(R.id.fare_adult_txt);
        mSeniorTxt = (TextView) view.findViewById(R.id.fare_senior_txt);
        mStudentTxt = (TextView) view.findViewById(R.id.fare_student_txt);
//        mPassengerCountTxt = (TextView) view.findViewById(R.id.fare_passenger_count_txt);
//        mPassengerCountTxt.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                showPassengerCountDialog();
//            }
//        });

        mAdultPriceTxt = (TextView) view.findViewById(R.id.fare_adult_price_txt);
        mAdultPriceTxt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                showPassengerCountDialog(Constants.ADULT_FARE_REQUEST);
            }
        });
        mSeniorPriceTxt = (TextView) view.findViewById(R.id.fare_senior_price_txt);
        mSeniorPriceTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showPassengerCountDialog(Constants.SENIOR_FARE_REQUEST);
            }
        });
        mStudentPriceTxt = (TextView) view.findViewById(R.id.fare_student_price_txt);
        mStudentPriceTxt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showPassengerCountDialog(Constants.STUDENT_FARE_REQUEST);
            }
        });

//        mStudentFare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
//                int count = Integer.parseInt(mStudentFare.getSelectedItem().toString());
//                mStudentCount = count;
//                Log.d(LOG_TAG, "Student Fare selected : " + count);
//                mTotalFare = calculateTotal();
//                updateFareText();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
////                mStudentCount = 0;
//            }
//        });

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
        map.put(Constants.PRINT_DATE, getTimestamp());
        map.put(Constants.PRINT_ROUTE, mRouteId);
        map.put(Constants.PRINT_BUS, mVehicleId);
        map.put(Constants.PRINT_FROM, mOriginStop);
        map.put(Constants.PRINT_TO, mDestinationStop);
        map.put(Constants.PRINT_ADULT_NUMBER_OF_PERSON, mAdultCount +"");
        map.put(Constants.PRINT_ADULT_FARE_PER_PERSON, mAdultPrice +"");
        map.put(Constants.PRINT_ADULT_TOTAL, mAdultTotalFare +"");
        map.put(Constants.PRINT_SENIOR_NUMBER_OF_PERSON, mSeniorCount + "");
        map.put(Constants.PRINT_SENIOR_FARE_PER_PERSON, mSeniorPrice + "");
        map.put(Constants.PRINT_SENIOR_TOTAL, mSeniorTotalFare + "");
        map.put(Constants.PRINT_STUDENT_NUMBER_OF_PERSON, mStudentCount + "");
        map.put(Constants.PRINT_STUDENT_FARE_PER_PERSON, mStudentPrice + "");
        map.put(Constants.PRINT_STUDENT_TOTAL, mStudentTotalFare + "");



        PrintConfirmationDialog printConfirmationDialog = new PrintConfirmationDialog(mContext, mPrinterAdapter, stringBuilder, map);
        printConfirmationDialog.setMainActivity(mMainActivity);
        printConfirmationDialog.show();
    }


    /**
     * Retrieve stops info from MDCMainActivity
     */
    private void initialiseInfo(){

        mStops = mMainActivity.getStops();

        mGroups = getStopGroupsInfo();

        mNames = new String[mStops.length];
        mStopGroups = new String[mStops.length];
        int i = 0;
        for(StopVO stop : mStops){
            mNames[i] = stop.getName();
            mStopGroups[i] = getGroup(stop.getFareStopTag());
            i++;
        }
        getFareInfo();
    }


    /**
     * Get group name by using fareStopTag in StopVO
     * @param index
     * @return
     */
    private String getGroup(int index){
        String group = "";
        for(int i=0; i<mGroups.length; i++){
            if(index==mGroups[i].getIndex())
            {
                group = mGroups[i].getName();
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
     * @return
     */


    private int calculateTotal(){
        int adult = calculateAdultFare();
        int senior = calculateSeniorFare();
        int student = calculateStudentFare();
        mPaymentTxt.setClickable(true);
        return adult + senior + student;
    }

    private int calculateAdultFare(){
        mAdultPrice = 0;

        if(mAdultCount==0){
            return 0;
        }else {
            // 1. get origin group index
            int originGroup = getStopTag(mOriginStop);
            // 2. get destination group index
            int destinationGroup = getStopTag(mDestinationStop);

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
            mAdultPrice = Integer.parseInt(fares[difference].trim());

            //        Log.e(LOG_TAG, "price ==> " + price);
            // 7. check how many passengers need the fare
            // 8. return fare in total
            mAdultTotalFare = mAdultPrice * mAdultCount;
            return mAdultTotalFare;
        }
    }

    private int calculateSeniorFare(){
        mSeniorPrice = 0;

        if(mSeniorCount == 0){
            return 0;
        }else {
            // 1. get origin group index
            int originGroup = getStopTag(mOriginStop);
            // 2. get destination group index
            int destinationGroup = getStopTag(mDestinationStop);

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
            mSeniorPrice = Integer.parseInt(fares[difference].trim());

            //        Log.e(LOG_TAG, "price ==> " + price);
            // 7. check how many passengers need the fare
            // 8. return fare in total
            mSeniorTotalFare = mSeniorPrice * mSeniorCount;
            return mSeniorTotalFare;
        }
    }

    private int calculateStudentFare(){
        mStudentPrice = 0;

        if(mStudentCount == 0){
            return 0;
        }else {
            // 1. get origin group index
            int originGroup = getStopTag(mOriginStop);
            // 2. get destination group index
            int destinationGroup = getStopTag(mDestinationStop);

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
            mStudentPrice = Integer.parseInt(fares[difference].trim());

            //        Log.e(LOG_TAG, "price ==> " + price);
            // 7. check how many passengers need the fare
            // 8. return fare in total
            mStudentTotalFare = mStudentPrice * mStudentCount;
            return mStudentTotalFare;
        }
    }


    /**
     * Pop up dialog for origin stop
     */
    private void showOriginDialog() {
        mAdultPriceTxt.setText("0");
        mSeniorPriceTxt.setText("0");
        mStudentPriceTxt.setText("0");
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));

        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups, Constants.FARE_ORIGIN_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.ORIGIN_DIALOG_TAG);

    }


    /**
     * Pop up dialog for destination stop
     */
    private void showDestinationDialog() {
        mAdultPriceTxt.setText("0");
        mSeniorPriceTxt.setText("0");
        mStudentPriceTxt.setText("0");
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));

        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups, Constants.FARE_DESTINATION_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.DESTINATION_DIALOG_TAG);
    }


    /**
     * Pop up dialog for passenger count
     */
    private void showPassengerCountDialog(int request) {
        PassengerDialog passengerDialog = new PassengerDialog(request);
        // link itself to be updated via 'PassValueFromPassengerDialogListener.sendPassengerCount()'
        passengerDialog.setPassValueFromPassengerDialogListener(FareFragment.this);
        passengerDialog.show(getFragmentManager(), Constants.PASSENGER_DIALOG_TAG);
    }
    

    /**
     * check whether update will happen either origin or destination
     * @param name
     * @param type
     * @param request
     */
    @Override
    public void sendStopName(String name, String type, int request) {

//        String message = "";
        SpannableStringBuilder  spannable = new SpannableStringBuilder();
        // update stop info into TextView
        switch (request){
            case Constants.FARE_ORIGIN_REQUEST :
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
                break;
            case Constants.FARE_DESTINATION_REQUEST:
                mDestinationStop = name;
                String legendD = getString(R.string.fare_destination_legend);
                SpannableString legendSD = new SpannableString(legendD);
                legendSD.setSpan(new ForegroundColorSpan(Color.BLACK), 0, legendD.length(), 0);
                legendSD.setSpan(new StyleSpan(Typeface.BOLD), 0, legendD.length(), 0);
                spannable.append(legendSD);
                spannable.append("\t");

                SpannableString nameSD = new SpannableString(name);
                nameSD.setSpan(new ForegroundColorSpan(Color.WHITE), 0, name.length(), 0);
                nameSD.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), 0);
                spannable.append(nameSD);
                spannable.append("\t\t");

                SpannableString groupSD = new SpannableString(type);
                groupSD.setSpan(new ForegroundColorSpan(Color.BLACK), 0, type.length(), 0);
                groupSD.setSpan(new StyleSpan(Typeface.BOLD), 0, type.length(), 0);
                spannable.append(groupSD);

                mDestinationTxt.setText(spannable);

                ///////////////////////////////////////////////
                // activate fare textview
                ///////////////////////////////////////////////
                mAdultPriceTxt.setClickable(true);
                mSeniorPriceTxt.setClickable(true);
                mStudentPriceTxt.setClickable(true);
                break;
        }
    }

    /**
     * check whether update will happen in fares
     * @param name
     * @param requestCode
     */
    @Override
    public void sendPassengerCount(String name, int requestCode) {
        switch (requestCode) {
            case Constants.ADULT_FARE_REQUEST:
                mAdultPriceTxt.setText(name);
                mAdultCount = Integer.parseInt(name);
                break;
            case Constants.SENIOR_FARE_REQUEST:
                mSeniorPriceTxt.setText(name);
                mSeniorCount = Integer.parseInt(name);
                break;
            case Constants.STUDENT_FARE_REQUEST:
                mStudentPriceTxt.setText(name);
                mStudentCount = Integer.parseInt(name);
                break;
        }
        mTotalFare = calculateTotal();
        updateFareText();
    }

    /**
     * update Fare Textview
     */
    public void updateFareText() {
        String price = " ฿ " + mTotalFare +"";
        SpannableString fare = new SpannableString(price);
        fare.setSpan(new RelativeSizeSpan(2f), 0, price.length(), 0);
        fare.setSpan(new StyleSpan(Typeface.BOLD), 0, price.length(), 0);
        mPriceTxt.setText(fare);
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
    public void getFareInfo() {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString(Constants.ADULT_FARES_IN_ROUTE, null);
        mAdultFares = gson.fromJson(json, String[].class);
        json = pref.getString(Constants.SENIOR_FARES_IN_ROUTE, null);
        mSeniorFares = gson.fromJson(json, String[].class);
        json = pref.getString(Constants.STUDENT_FARES_IN_ROUTE, null);
        mStudentFares = gson.fromJson(json, String[].class);

        Log.d(LOG_TAG, "Adult " + ArrayUtils.toString(mAdultFares));
        Log.d(LOG_TAG, "Senior " + ArrayUtils.toString(mSeniorFares));
        Log.d(LOG_TAG, "Student " + ArrayUtils.toString(mStudentFares));
    }


    @Override
    public void showConnected() {
        Log.d(LOG_TAG, "Printer connected");
    }


    @Override
    public void showFailed() {
        Log.d(LOG_TAG, "Printer connection fails");
    }


    /**
     * Check whether current tab is selected or not
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(this.isVisible()){
            String lang = MDCUtils.getValue(mContext, Constants.SELECTED_LANGUAGE, "en");

            if(isVisibleToUser){
                resetData();
                Log.d(LOG_TAG, "FareFragment is visible");
            }else{
                Log.d(LOG_TAG, "FareFragment is not visible");
            }
        }
    }


    /**
     * Decorate print message for Dialog
     * @return
     */
    public SpannableStringBuilder getPrintMessage(){
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        spannable.append("หมายเลขตั๋ว: ");
        spannable.append(getSpannableString(StringUtils.leftPad(Integer.toString(MDCMainActivity.fareTransactionId), 5, "0")));
        spannable.append("\t");
        spannable.append("วันเวลา: " );
        spannable.append(getSpannableString(getTimestamp()));
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
        spannable.append(getSpannableString(mDestinationStop));
        spannable.append("\n");
        // adult
        spannable.append("จำนวนผู้ใหญ่ : ");// number
        spannable.append(getSpannableString(Integer.toString(mAdultCount)));
        spannable.append("\t");
        spannable.append(" ผราคาของผู้ใหญ่ : ");// price
        spannable.append(getSpannableString(Integer.toString(mAdultPrice)));
        spannable.append("\t");
        spannable.append(" ค่าโดยสารรวมผู้ใหญ่ : ");// sum
        spannable.append(getSpannableString(Integer.toString(mAdultTotalFare)));
        spannable.append("\n");
        // senior
        spannable.append("จำนวนอาวุโส : ");
        spannable.append(getSpannableString(Integer.toString(mSeniorCount)));
        spannable.append("\t");
        spannable.append(" ราคาของอาวุโส : ");
        spannable.append(getSpannableString(Integer.toString(mSeniorPrice)));
        spannable.append("\t");
        spannable.append(" ค่าโดยสารอาวุโสทั้งหมด : ");
        spannable.append(getSpannableString(Integer.toString(mSeniorTotalFare)));
        spannable.append("\n");
        // student
        spannable.append("จำนวนนักเรียน : ");
        spannable.append(getSpannableString(Integer.toString(mStudentCount)));
        spannable.append("\t");
        spannable.append(" ค่าโดยสารนักเรียน : ");
        spannable.append(getSpannableString(Integer.toString(mStudentPrice)));
        spannable.append("\t");
        spannable.append(" นักเรียนราคารวม : ");
        spannable.append(getSpannableString(Integer.toString(mStudentTotalFare)));
        spannable.append("\n");
        // Total
        spannable.append(" ราคารวม : ");// total price
        spannable.append(getSpannableString(Integer.toString(mTotalFare)));
        spannable.append("\n");
        spannable.append("      ขอบคุณที่ใช้บริการ");

        return spannable;
    }


    public SpannableString getSpannableString(String str){
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new RelativeSizeSpan(1.0f), 0, str.length(), 0);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, str.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, str.length(), 0);
        return spannableString;
    }


    public String getTimestamp(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH.mm");
        String format = simpleDateFormat.format(date);
        return format;
    }


}
