package com.creapple.tms.mobiledriverconsole;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.creapple.tms.mobiledriverconsole.fragments.TabAdapter;
import com.creapple.tms.mobiledriverconsole.geofencing.GeofenceService;
import com.creapple.tms.mobiledriverconsole.model.MDCViewPager;
import com.creapple.tms.mobiledriverconsole.model.vo.StopVO;
import com.creapple.tms.mobiledriverconsole.model.vo.TransactionVO;
import com.creapple.tms.mobiledriverconsole.print.PrinterAdapter;
import com.creapple.tms.mobiledriverconsole.print.PrinterViewAction;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//public class MDCMainActivity extends FullScreeenActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PrinterViewAction{
public class MDCMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PrinterViewAction{

    private static final String LOG_TAG = MDCUtils.getLogTag(MDCMainActivity.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    private TabAdapter mTabAdapter;

    public MDCViewPager mViewPager;

    private GeoReceiver mGeoReceiver;

    private TabLayout mTabLayout;


    private StopVO[] mStops; // shared by FareFragment

    private String mRouteId; // route id

    private String mVehicleId; // vehicle id


    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Geofence> mGeofenceList;

    IntentFilter mIntentFilter;

    public static String currentStopName = "โรงพยาบาลสินแพทย์";

    public static String nextStopName = "ด่านทับช้าง";

//    public static int mPassengerCount; // passenger count during specific interval

    public static int mPassengerCountSum; // total passenger count

//    public static int mFareCash; // fare during specific interval

    public static int mFareCashSum; // total fare

    public static int fareTransactionId = 1;


    ///////////////////////////////////////////////////////////////////
    //
    // Trip Trasaction Information upload
    //
    //////////////////////////////////////////////////////////////////

    /**
     * If this turns on, it means add speed to arraylist
     */
    public static boolean mSpeedCheck;

    /**
     * It stores speed information to calculate interval average
     */
    public static ArrayList<Double> mAverageSpeed = new ArrayList<Double>();

    /**
     * Timestamp when entering geofence
     */
    public Long mEnteredTime;

    /**
     * Timstamp when exiting geofence
     */
    public Long mExitedTime;

    private Firebase mFirebase;

    private String mTripPath;
    
    /**
     * PrinterAdapter shared by FareFragment & SettingFragment
     */
    private PrinterAdapter mPrinterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mTabAdapter = new TabAdapter(getSupportFragmentManager(), getApplicationContext());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (MDCViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabAdapter);

        // make sure 3 tabs retained rather than re-creation
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

//        mVehicleId = getVehicleId(Constants.VEHICLE_NAME);

        mRouteId = MDCUtils.getValue(getApplicationContext(), Constants.ROUTE_ID, getString(R.string.no_route_found));
        mVehicleId = MDCUtils.getValue(getApplicationContext(), Constants.VEHICLE_NAME, getString(R.string.no_vehicle_found));


        mStops = getStopsInfo(Constants.STOPS_IN_ROUTE);

        buildGoogleApiClient();

        mFirebase = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_TRIP_LIST_PATH);// + "/" + mRouteId);

        // Register Receiver
        mIntentFilter = new IntentFilter(Constants.BROADCAST_SERVICE);
        mGeoReceiver = new GeoReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mGeoReceiver, mIntentFilter);

        // Dummy data for presentation purpose
        MDCMainActivity.currentStopName = mStops[0].getName();
        MDCMainActivity.nextStopName = mStops[1].getName();

        // register trip info
        registerTripInfo();
        
        // set up bluetooth printer
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mPrinterAdapter = new PrinterAdapter(this, bluetoothAdapter);

    }


    /**
     * switch tab's selection as per user's choice
     */
    public void switchTabSelection(int choice) {
        switch (choice){
            case Constants.FARE_FRAGMENT_TAB :
                mTabLayout.getTabAt(Constants.FARE_FRAGMENT_TAB).select();
                break;
            case Constants.TRIP_ON_FRAGMENT_TAB :
                mTabLayout.getTabAt(Constants.TRIP_ON_FRAGMENT_TAB).select();
                break;
            case Constants.SETTING_FRAGMENT_TAB :
                mTabLayout.getTabAt(Constants.SETTING_FRAGMENT_TAB).select();
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnecting()||!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
//            Log.d(LOG_TAG, "GoogleApiClient is now connected");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        // stop connection to BluetoothPrinter
        mPrinterAdapter.stopConnection();
    }


    @Override
    protected void onDestroy() {
        stopIntentService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeoReceiver);
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
//        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    public void showConnected() {
        // just implement PrinterViewAction
    }

    @Override
    public void showFailed() {
        // just implement PrinterViewAction
    }


    /**
     * Intent receiver from IntentService
     */
    public class GeoReceiver extends BroadcastReceiver{

        public GeoReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = StringUtils.defaultString(intent.getStringExtra(Constants.GEOFENCE_INTENT_MESSAGE));
            String stop = StringUtils.defaultString(intent.getStringExtra(Constants.GEOFENCE_INTENT_STOP));
            int action = intent.getIntExtra(Constants.GEOFENCE_INTENT_ACTION, Constants.NO_VALUE);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            switch (action){
                case Geofence.GEOFENCE_TRANSITION_ENTER :
                    // set timestamp for entering geofence
                    mEnteredTime = System.currentTimeMillis();
                    // update current/Next stop
                    updateStopNames(stop);
                    // switch tab to Fare
                    switchTabSelection(Constants.FARE_FRAGMENT_TAB);
                    // turn off flag for checking speed
                    turnOffSpeedCheck();
                    // call auditTx
                    //
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT :
                    // set timestamp for exiting geofence
                    mExitedTime = System.currentTimeMillis();
                    // switch tab to TripOn
                    switchTabSelection(Constants.TRIP_ON_FRAGMENT_TAB);
                    // reset list for average speed calculation
                    resetAverageSpeed();
                    // turn on flag for checking speed
                    turnOnSpeedCheck();
                    // call auditTx
                    //

                    break;
                default :
                    Toast.makeText(context, getString(R.string.unknown_geofence_transition), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Retreive stops info under route
     * @param key
     * @return
     */
    public StopVO[] getStopsInfo(String key) {
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String json = pref.getString(key, null);
        StopVO[] stops = new Gson().fromJson(json, StopVO[].class);
        // make sure sorting by sortIndex for sequential Geofencing
        Arrays.sort(stops);
        return stops;
    }


    /**
     * Retreive vehicle id
     * @param key
     * @return
     */
    public String getVehicleId(String key) {
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String id = pref.getString(key, getString(R.string.no_vehicle_found));
        return id;
    }


    public StopVO[] getStops() {
        return mStops;
    }



    ///////////////////////////////     GEOFENCING      ///////////////////////////////////////////////

    /**
     * As soon as connection establishes, start GeofenceService
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Log.d(LOG_TAG, "onConnected()");
        startIntentService();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * create GoogleApiClient
     */
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * start GeofenceService
     */
    private void startIntentService() {
        String message;
        if (!mGoogleApiClient.isConnected()) {
            message = "Google Api Clilent is not connected";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            PendingResult<Status> g = LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            );
            g.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    String message="";
                    if (status.isSuccess()) {
                        message = "Geofences Added";
                    } else {
                        switch (status.getStatusCode()) {
                            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                                message = getString(R.string.geofence_not_available);
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                                message = getString(R.string.geofence_too_many_geofences);
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                                message = getString(R.string.geofence_too_many_pending_intents);
                                break;
                            default:
                                message = getString(R.string.unknown_geofence_transition);
                        }
                    }
                }
            }); // Result processed in onResult().
        } catch (SecurityException en) {
            Log.e(LOG_TAG, en.getMessage());
        }
    }


    /**
     * Stop GeofenceSerivce
     */
    private void stopIntentService() {
        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, "Google Api Client is not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    String message;
                    if (status.isSuccess()) {
                        message = getString(R.string.geofence_removed);
                    } else {
                        message = getString(R.string.geofence_denied) + status.getStatusCode();
                    }
                }
            });
        } catch (SecurityException securityException) {
            Log.e(LOG_TAG, securityException.getMessage());
        }
    }


    /**
     *
     * Create dummy Geofence objects wrapped by GeofencingRequest
     * @return
     */
    private GeofencingRequest getGeofencingRequest(){

        mGeofenceList = new ArrayList<Geofence>();

        //////////// uncomment when ready /////////////////
        mGeofenceList = addGefenceToList();
        ///////////////////////////////////////////////////

//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("Company")
//                .setCircularRegion(
//                        -37.809003, 144.970886,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());
//
//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("State library")
//                .setCircularRegion(
//                        -37.810403, 144.964330,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());
//
//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("Burke mall")
//                .setCircularRegion(
//                        -37.813454, 144.965676,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());
//
//
//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("Flinders Station")
//                .setCircularRegion(
//                        -37.818224, 144.967852,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());
//
//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("South Bank")
//                .setCircularRegion(
//                        -37.823425, 144.970170,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());
//
//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("Domain Interchange")
//                .setCircularRegion(
//                        -37.832627, 144.972108,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());


        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();

    }


    /**
     * Add all stop info to Geofening List
     */
    private ArrayList<Geofence> addGefenceToList(){
        ArrayList<Geofence> list = new ArrayList<Geofence>();
        for(StopVO stop : mStops){
            list.add(new Geofence.Builder()
                    .setRequestId(stop.getName())
                    .setCircularRegion(
                            stop.getLat(), stop.getLon(),
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setNotificationResponsiveness(Constants.GEOFENCE_NOTIFICATION_RESPONSIVENESS)
                    .build()
            );
        }
        return list;
    }


    /**
     * Create PendingIntent for GeofenceService
     * @return
     */
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Update current / next stop name when Geofencing event happens (ENTERED)
     */
    private void updateStopNames(String stopName) {
        currentStopName = stopName;
        int index = 0;
        if(mStops!=null || mStops.length>0){
            for(int i=0; i<mStops.length; i++){
                if(mStops[i].getName().equalsIgnoreCase(stopName)){
                    index = i;
                }
            }
        }
        if((index+1) >= mStops.length){ // current stop is last one
            nextStopName = "";
        }else {
            nextStopName = mStops[index + 1].getName();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.e(LOG_TAG, "onConfigurationChanged");
    }


    /**
     * Disable all tab's click and movement
     */
    public void disableTabs(){
        LinearLayout tabStrip = (LinearLayout) mTabLayout.getChildAt(0);
        for(int i=0; i < tabStrip.getChildCount(); i++){
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
        mViewPager.setSwappable(false);
    }


    /**
     * Update Tab name as per language setting
     */
    public void updateTabNames(){
        for(int i=0; i<mTabAdapter.getCount(); i++){
            TabLayout.Tab tab =mTabLayout.getTabAt(i);
            tab.setText(mTabAdapter.getPageTitle(i).toString());
        }
    }


    /**
     * Release the resource
     * 1. Unregister Geofencing
     * 2. GoogleApiClient
     */
    public void closeResources(){
        stopIntentService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeoReceiver);
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Trip Information upload
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Add speed info to calucalte average speed later
     * @param speed
     */
    public static void addAverageSpeed(double speed){
        mAverageSpeed.add(speed);
    }

    /**
     * reset average speed
     */
    public void resetAverageSpeed(){
        mAverageSpeed.clear();
    }

    /**
     * Turn speed check on. It can be triggered when geofencing exits
     */
    public void turnOnSpeedCheck(){
        mSpeedCheck = true;
    }

    /**
     * Turn speed check off. It can be triggered when geofencing enters
     */
    public void turnOffSpeedCheck(){
        mSpeedCheck = false;
    }

    /**
     * Check whether speed check needs or not
     * @return
     */
    public static boolean isSpeedCheckTurnOn(){
        return mSpeedCheck;
    }




    /**
     * Get fareStopTag by using name in StopVO
     * @param name
     * @return
     */
    public String getStopId(String name){
        String id = "";
        for(int i=0; i<mStops.length; i++){
            if(name.equalsIgnoreCase(mStops[i].getName()))
            {
                id = mStops[i].getId();
                break;
            }
        }
        return id;
    }

    /**
     * Insert transaction record into Firebase
     *
     *
     *
     *
     * 1. 구간평균 속도 - 각 Trip 아래 stoplogs 각 정류장 정보에 추가 avgSpeed
     *   이전 정류장과 현재 정류장 간 평균 속도를 저장 ex) 10단위 속도합을 측정 횟수로 나눔
     *  ==> Enter
     *
     * 2. 정류장 정차시간 - 각 Trip 아래 stoplogs  정보에 추가 stopInterval
     * Geofence 집입 후 이탈이전 속도가 0인 시간의 합
     *  ==> Exit
     *
     * 3. 순통행 시간 - 각 Trip 아래 stoplogs 정보에 추가 tripInterval
     * 직전 정류장 Geofence 이탈 시간부터 이번 정류장 Geofence 이탈 시간의 차이
     *  ==> Enter
     *
     * 4. 정류장 별 현금 거래인원 - 각 Trip 아래 stoplogs 정보에 추가 cashCount
     *  ==>
     *
     * 5. 정류장 별 현금 거래금액 - 각 Trip 아래 stoplogs 정보에 추가 cashAmount
     *  ==>
     *
     * 6. Trip 별 거래금액 합 - 각 Trip아래 정보 추가 cashAmountSum
     *  ==> static value
     *
     * 7. Trip 별 거래인원 합 - 각 Trip아래 정보 추가 cashCountSum
     *  ==> static value
     *
     *
     *
     *
     */
    public void registerTripInfo() {

        mTripPath = MDCUtils.getTipNode(mVehicleId);
        Firebase tripVehicle = mFirebase.child(mTripPath);

        // save tripPath for other activities
        MDCUtils.put(getApplicationContext(), Constants.TRIP_PATH, mTripPath);

//        Log.d(LOG_TAG, "Trip Path : " + mTripPath);

        Map<String, Object> currentTripOn = new HashMap<String, Object>();
        currentTripOn.put(Constants.TRIP_CURRENT_STOP_NAME, currentStopName);
        currentTripOn.put(Constants.TRIP_CURRENT_STOP_ID, getStopId(currentStopName));
        String driverId = MDCUtils.getValue(this, Constants.USER_EMAIL, "");
        currentTripOn.put(Constants.TRIP_DRIVER_ID, driverId);
        currentTripOn.put(Constants.TRIP_FARE_SUM, 0);
        currentTripOn.put(Constants.TRIP_PASSENGER_SUM, 0);
        currentTripOn.put(Constants.TRIP_ROUTE, mRouteId);
        currentTripOn.put(Constants.TRIP_START_TIME, ServerValue.TIMESTAMP);
        currentTripOn.put(Constants.TRIP_UPDATED, ServerValue.TIMESTAMP);
        currentTripOn.put(Constants.TRIP_VEHICLE_ID, mVehicleId);
        tripVehicle.updateChildren(currentTripOn);
//        Log.d(LOG_TAG, "auditTx");

    }

    /**
     * Add transaction log under Trips
     * @param transactionVO
     */
    public void logTransaction(TransactionVO transactionVO){
        Firebase txLog = mFirebase.child(mTripPath).child("transactions");
        txLog.push().setValue(transactionVO);
    }


    /**
     * Update total fare and passenger count under Trips
     */
    public void updateTotalFare(){
        Firebase txUpdate = mFirebase.child(mTripPath);
        Map<String, Object> fares = new HashMap<String, Object>();
        fares.put(Constants.TRIP_FARE_SUM, mFareCashSum);
        fares.put(Constants.TRIP_PASSENGER_SUM, mPassengerCountSum);
        fares.put(Constants.TRIP_UPDATED, ServerValue.TIMESTAMP);
        txUpdate.updateChildren(fares);
    }
    
    /**
     * Return the instance of PrinterAdatper
     * @return
     */
    public PrinterAdapter getPrinterAdapter(){
        if(mPrinterAdapter==null){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mPrinterAdapter = new PrinterAdapter(this, bluetoothAdapter);
        }
        return mPrinterAdapter;
    }

//
//    /**
//     * Increase passenger count
//     * @param cnt
//     */
//    public void increasePassengerCount(int cnt){
//        mPassengerCount += cnt;
//        mPassengerCountSum += cnt;
//    }
//
//    /**
//     * Increase fare
//     * @param cnt
//     */
//    public void increaseFare(int cnt){
//        mFareCash += cnt;
//        mFareCashSum += cnt;
//    }
//
//    /**
//     * Retreive total passenger count
//     * @return
//     */
//    public int getTotalPassengerCount(){
//        return mPassengerCountSum;
//    }
//
//    /**
//     * Retreive total fare
//     * @return
//     */
//    public int getTotalFare(){
//        return mFareCashSum;
//    }
}
