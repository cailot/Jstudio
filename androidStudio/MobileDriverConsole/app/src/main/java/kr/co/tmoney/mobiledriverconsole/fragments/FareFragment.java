package kr.co.tmoney.mobiledriverconsole.fragments;

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

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.dialog.PassengerDialog;
import kr.co.tmoney.mobiledriverconsole.dialog.PrintConfirmationDialog;
import kr.co.tmoney.mobiledriverconsole.dialog.StopDialog;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopGroupVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.print.PrinterAdapter;
import kr.co.tmoney.mobiledriverconsole.print.PrinterViewAction;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

//import org.apache.log4j.Logger;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment implements StopDialog.PassValueFromStopDialogListener, PassengerDialog.PassValueFromPassengerDialogListener, PrinterViewAction {

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mPriceTxt, mOriginTxt, mDestinationTxt, mPassengerCountTxt, mPaymentTxt;

    Context mContext;

    private StopVO[] mStops; // all stops info as array

    private StopGroupVO[] mGroups; // all stop groups info as array

    private String[] mNames; // stop names to pop up dialog

    private String[] mStopGroups; // group names to pop up dialog

    private String[] mFares; // all fare info as array

    private String mOriginStop; // original stop

    private String mDestinationStop; // destination stop

    private int mPrice; // price per each adult

    private int mPassengerCount; // passenger count

    private int mTotalFare;

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
        mPassengerCountTxt.setText(getResources().getString(R.string.fare_passenger_title));
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
        mPaymentTxt.setText(getResources().getString(R.string.fare_payment_title));
        mPassengerCount = 0;
        mTotalFare = 0;
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
        mPassengerCountTxt = (TextView) view.findViewById(R.id.fare_passenger_count_txt);
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
        map.put(Constants.PRINT_NUMBER_OF_PERSON, mPassengerCount+"");
        map.put(Constants.PRINT_FARE_PER_PERSON, mPrice+"");
        map.put(Constants.PRINT_TOTAL, mTotalFare+"");

        PrintConfirmationDialog printConfirmationDialog = new PrintConfirmationDialog(mContext, mPrinterAdapter, stringBuilder, map, mPassengerCount);
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
        mFares = getFareInfo();
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
    private int calculateFare(){
        mPrice = 0;
        // 1. get origin group index
        int originGroup = getStopTag(mOriginStop);
        // 2. get destination group index
        int destinationGroup = getStopTag(mDestinationStop);

        // this will not happen in real life but just to avoid NPE in testing data
        if(originGroup>=mFares.length){
            originGroup = mFares.length-1;
        }
        if(destinationGroup>=mFares.length){
            destinationGroup = mFares.length-1;
        }
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
        mPrice = Integer.parseInt(fares[difference].trim());

//        Log.e(LOG_TAG, "price ==> " + price);
        // 7. check how many passengers need the fare
        // 8. return fare in total
        mTotalFare = mPrice * mPassengerCount;
        return mTotalFare;
    }


    /**
     * Pop up dialog for origin stop
     */
    private void showOriginDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups, Constants.FARE_ORIGIN_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.ORIGIN_DIALOG_TAG);
        mPassengerCountTxt.setText(getResources().getString(R.string.fare_passenger_title));
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
    }


    /**
     * Pop up dialog for destination stop
     */
    private void showDestinationDialog() {
        StopDialog stopsDialog = new StopDialog(mNames, mStopGroups, Constants.FARE_DESTINATION_REQUEST);
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        stopsDialog.setPassValueFromStopDialogListener(FareFragment.this);
        stopsDialog.show(getFragmentManager(), Constants.DESTINATION_DIALOG_TAG);
        mPassengerCountTxt.setText(getResources().getString(R.string.fare_passenger_title));
        mPriceTxt.setText(getResources().getString(R.string.fare_price_title));
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
        mPassengerCount = Integer.parseInt(name);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String message = getString(R.string.fare_passenger_legend);// + name;
        SpannableString text = new SpannableString(message);
        text.setSpan(new RelativeSizeSpan(1f), 0, message.length(), 0);
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, message.length(), 0);
        text.setSpan(new StyleSpan(Typeface.BOLD), 0, message.length(), 0);
        stringBuilder.append(text);
        stringBuilder.append("\t");
        stringBuilder.append(name);
        mPassengerCountTxt.setText(stringBuilder);

        String price = " ฿ " + calculateFare()+"";
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
    public String[] getFareInfo() {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String json = pref.getString(Constants.FARES_IN_ROUTE, null);
        String[] fares = new Gson().fromJson(json, String[].class);
        return fares;
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
        spannable.append("จำนวน : ");
        spannable.append(getSpannableString(Integer.toString(mPassengerCount)));
        spannable.append("\t");
        spannable.append(" ราคาต่อคน : ");
        spannable.append(getSpannableString(Integer.toString(mPrice)));
        spannable.append("\t");
        spannable.append(" ราคารวม : ");
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
