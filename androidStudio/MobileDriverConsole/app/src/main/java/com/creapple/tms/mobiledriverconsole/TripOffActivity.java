package com.creapple.tms.mobiledriverconsole;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.creapple.tms.mobiledriverconsole.dialog.LogOutDialog;
import com.creapple.tms.mobiledriverconsole.dialog.RouteDialog;
import com.creapple.tms.mobiledriverconsole.dialog.VehicleDialog;
import com.creapple.tms.mobiledriverconsole.model.vo.StopGroupVO;
import com.creapple.tms.mobiledriverconsole.model.vo.StopVO;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOffActivity extends ProgressActivity implements RouteDialog.PassValueFromRouteDialogListener, VehicleDialog.PassValueFromVehicleDialogListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOffActivity.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mRouteTxt, mVehicleTxt, mLogoutTxt, mTripOnTxt;

    String[] mRouteIds;

    String[] mRouteNames;

    String[] mVehicles;

    StopVO[] mStops;

    StopGroupVO[] mStopGroups;

    String mAdultFares, mSeniorFares, mStudentFares;

    private String mRouteId; // ex> 554R

    private String mVehicleId; // ex> SV580005

    private Map mFrontVehicleInfo = new HashMap();

    private Firebase mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_off_activity);
        // change status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorNavy));
        }else{
        }

        // show dialog to prevent user's action
        showProgressDialog();

        // setup Firebase on Android
        mFirebase = new Firebase(Constants.FIREBASE_HOME);
        // bring up all routes & vehicles list to save up time
        getRouteList();
        getVehicleList();

        // permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // user already reject the permission so show the dialog again
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.GPS_PERMISSION_GRANT+1);

                } else {

                    // we can request the permission for the first time.
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.GPS_PERMISSION_GRANT+1);

                }
            } else {
                // already got the permission
                this.preparePermission();
            }
        }

        // build UI
        initialiseUI();

        new CheckRouteNVehicleTask().execute();
    }


    /**
     * GPS permission handles - Android SDK 23
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(LOG_TAG, "Request code : " + requestCode);

        switch (requestCode) {
            case Constants.GPS_PERMISSION_GRANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "TripOff : GPS permission granted ", Toast.LENGTH_SHORT).show();
                    preparePermission();

                } else {

                    Toast.makeText(this, "Need to grant the permssion", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    /**
     * Just want to get GPS permission before showing Map
     */
    private void preparePermission(){
//        Log.d(LOG_TAG, "Prepare Permission at TripOffActivity");
    }


    /**
     * build up UI and register click events per component
     */
    private void initialiseUI() {
        mRouteTxt = (TextView) findViewById(R.id.trip_off_route_txt);
        mRouteTxt.setEnabled(false);
        mRouteTxt.setTextColor(Color.BLACK);
        mRouteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mVehicleTxt = (TextView) findViewById(R.id.trip_off_vehicle_txt);
        mVehicleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mVehicleTxt.setEnabled(false);
        mVehicleTxt.setTextColor(Color.BLACK);

        mLogoutTxt = (TextView) findViewById(R.id.trip_off_logout_btn);
        mLogoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mTripOnTxt = (TextView) findViewById(R.id.trip_off_tripon_btn);
        mTripOnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mTripOnTxt.setEnabled(false);
        mTripOnTxt.setTextColor(Color.BLACK);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void tripOffEvents(View view){
        switch(view.getId()){
            case R.id.trip_off_route_txt :
//                Log.d(LOG_TAG, "Route Event");
                showRouteDialog();
                break;
            case R.id.trip_off_vehicle_txt :
//                Log.d(LOG_TAG, "Vehicle Event");
                showVehicleDialog();
                break;
            case R.id.trip_off_logout_btn :
//                Log.d(LOG_TAG, "Logout Event");
                logOut();
                break;
            case R.id.trip_off_tripon_btn :
                // prevent double click
                mTripOnTxt.setClickable(false);
                mTripOnTxt.setText(getString(R.string.trip_off_process));
//                mTripOnTxt.setTextColor(Color.BLACK);
                turnOnTripOn();
                break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.d(LOG_TAG, "onConfigurationChanged");
    }


    /**
     * Just about to leave this activity so set up data for next activities
     * 1. save vehicleId
     * 2. change tripOn to true
     * 3. startIntent
     */
    private void turnOnTripOn() {
        // save vehicle name into SharedPreferences
        MDCUtils.put(getApplicationContext(), Constants.VEHICLE_NAME, mVehicleId);

        // save stop details into SharedPreferences
        saveStopsDetail();
        // save stop groups into SharedPreferences
        saveStopGroupsDetail();
        // save fares into SharedPreferences
        saveFaresDetail();

        // update DB to indicate starting
        setTripOn();

        // switch to TripOn
        Intent i = new Intent(getApplicationContext(), MDCMainActivity.class);
        startActivity(i);
    }

//    private SpannableStringBuilder makeRouteInfo() {
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//        //String title = "Do you want to start driving ?";
//        String title="คุณต้องการ ที่จะเริ่มต้น การขับรถ ?";
//        SpannableString titleS = new SpannableString(title);
//        titleS.setSpan(new RelativeSizeSpan(1.0f), 0, title.length(), 0);
//        titleS.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), 0);
//        titleS.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), 0);
//        spannableStringBuilder.append(titleS);
//        spannableStringBuilder.append("\n\n");
//        spannableStringBuilder.append(getString(R.string.trip_off_route_legend));
//        SpannableString routeS = new SpannableString(mRouteId);
//        routeS.setSpan(new RelativeSizeSpan(1.5f), 0, mRouteId.length(), 0);
//        routeS.setSpan(new ForegroundColorSpan(Color.WHITE), 0, mRouteId.length(), 0);
//        routeS.setSpan(new StyleSpan(Typeface.BOLD), 0, mRouteId.length(), 0);
//        spannableStringBuilder.append(routeS);
//        spannableStringBuilder.append("\n\n");
//        spannableStringBuilder.append(getString(R.string.trip_off_vechicle_legend));
//        SpannableString vehicleS = new SpannableString(mVehicleId);
//        vehicleS.setSpan(new RelativeSizeSpan(1.5f), 0, mVehicleId.length(), 0);
//        vehicleS.setSpan(new ForegroundColorSpan(Color.WHITE), 0, mVehicleId.length(), 0);
//        vehicleS.setSpan(new StyleSpan(Typeface.BOLD), 0, mVehicleId.length(), 0);
//        spannableStringBuilder.append(vehicleS);
//
//        return spannableStringBuilder;
//
//    }


    /**
     * save all stops information into SharedPreference by Gson
     */
    public void saveStopsDetail() {
        MDCUtils.put(getApplicationContext(), Constants.STOPS_IN_ROUTE, mStops);
    }


    /**
     * save stop groups information into SharedPreference by Gson
     */
    public void saveStopGroupsDetail() {
        MDCUtils.put(getApplicationContext(), Constants.STOP_GROUPS_IN_ROUTE, mStopGroups);
    }


    /**
     * save all fares information into SharedPreference by Gson
     */
    public void saveFaresDetail() {
        MDCUtils.put(getApplicationContext(), Constants.ADULT_FARES_IN_ROUTE, MDCUtils.getStopGroups(mAdultFares));
        MDCUtils.put(getApplicationContext(), Constants.SENIOR_FARES_IN_ROUTE, MDCUtils.getStopGroups(mSeniorFares));
        MDCUtils.put(getApplicationContext(), Constants.STUDENT_FARES_IN_ROUTE, MDCUtils.getStopGroups(mStudentFares));
    }


    /**
     *  logout
     */
    private void logOut(){
//        Log.d(LOG_TAG, "logOut()");
        LogOutDialog logOutDialog = new LogOutDialog(this);
        logOutDialog.show(getFragmentManager(), Constants.LOGOUT_DIALOG_TAG);
//        finishAffinity();
    }


    /**
     * callback method on routeTxt
     * @param id
     * @param name
     */
    @Override
    public void sendRouteName(String id, String name) {
        // update route name according to user's choice
        // set routeId
        mRouteId = id;
        // save routeId into SharedPreferences
        MDCUtils.put(getApplicationContext(), Constants.ROUTE_ID, mRouteId);
        // update selected route info in TextView
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        String legend = getString(R.string.trip_off_route_legend);
        SpannableString legendS= new SpannableString(legend);
        legendS.setSpan(new ForegroundColorSpan(Color.BLACK), 0, legend.length(), 0);
        legendS.setSpan(new StyleSpan(Typeface.BOLD), 0, legend.length(), 0);
        spannable.append(legendS);
        spannable.append("\t");

        SpannableString idS = new SpannableString(id);
        idS.setSpan(new ForegroundColorSpan(Color.WHITE), 0, id.length(), 0);
        idS.setSpan(new StyleSpan(Typeface.BOLD), 0, id.length(), 0);
        spannable.append(idS);
        spannable.append("\t\t");

        SpannableString nameS = new SpannableString(name);
        nameS.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), 0);
        spannable.append(nameS);

        mRouteTxt.setText(spannable);

        // get the front vehicle info & index to prepare updating vehicle
        searchFrontVehicle();
        // get stops detail in route
        getStopsDetail();
        // get stop groups detail in route
        getStopGroupsDetail();
        // get fares detail in route
        getFaresDetail();

        // enable Trip On button after getting all stop & fare info
        new CheckStopNFareTask().execute();
        // enable Vehicle Txt
        mVehicleTxt.setEnabled(true);
        mVehicleTxt.setTextColor(Color.WHITE);

    }


    /**
     * callback method on vehicleTxt
     * @param routeName
     */
    @Override
    public void sendVehicleName(String routeName) {
        // assign vehicle name to mVehicleId
        mVehicleId = routeName;
        // update selected vehicle info in TextView

        SpannableStringBuilder spannable = new SpannableStringBuilder();
        String legend = getString(R.string.trip_off_vechicle_legend);
        SpannableString legendS= new SpannableString(legend);
        legendS.setSpan(new ForegroundColorSpan(Color.BLACK), 0, legend.length(), 0);
        legendS.setSpan(new StyleSpan(Typeface.BOLD), 0, legend.length(), 0);
        spannable.append(legendS);
        spannable.append("\t\t");

        SpannableString routeS = new SpannableString(routeName);
        routeS.setSpan(new ForegroundColorSpan(Color.WHITE), 0, routeName.length(), 0);
        routeS.setSpan(new StyleSpan(Typeface.BOLD), 0, routeName.length(), 0);
        spannable.append(routeS);

        mVehicleTxt.setText(spannable);

        // change color of Trip On button
        mTripOnTxt.setTextColor(Color.WHITE);
    }


    /**
     * Dialog to select routes
     */
    private void showRouteDialog() {

        RouteDialog routeDialog = new RouteDialog(mRouteIds, mRouteNames);
        // link itself to be updated via 'PassValueFromRouteDialogListener.sendStopName()'
        routeDialog.setPassValueFromRouteDialogListener(TripOffActivity.this);
        routeDialog.show(getFragmentManager(), Constants.ROUTE_DIALOG_TAG);
        // rollback original message to Vehicle TextView
        mVehicleTxt.setText(getString(R.string.trip_off_vehicle_title));
    }


    /**
     * Dialog to select vehicles
     */
    private void showVehicleDialog() {
        if(mVehicles.length==0){
            // show warning message
            Toast.makeText(getApplicationContext(), getString(R.string.no_vehicle_found), Toast.LENGTH_SHORT).show();
            // update TextView
            mVehicleTxt.setText(getString(R.string.trip_off_vehicle_title));
            return;
        }
        VehicleDialog vehicleDialog = new VehicleDialog(mVehicles);
        // link itself to be updated via 'PassValueFromVehicleDialogListener.sendVehicleName()'
        vehicleDialog.setPassValueFromVechicleDialogListener(TripOffActivity.this);
        vehicleDialog.show(getFragmentManager(), Constants.VEHICLE_DIALOG_TAG);
    }


    /**
     * Get route list from firebase
     */
    private void getRouteList() {
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_ROUTE_LIST_PATH);
        Query queryRef = ref.orderByChild("sortIndex");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List routeIds = new ArrayList();
                List routeNames = new ArrayList();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    String routeName = (String) shot.child(Constants.ROUTE_NAME).getValue();
                    routeIds.add(shot.getKey());
                    routeNames.add(routeName);

                }
                if(routeIds.size()>0 && routeNames.size()>0){
                    mRouteIds = MDCUtils.convertListToStringArray(routeIds);
                    mRouteNames = MDCUtils.convertListToStringArray(routeNames);
                }
//                Log.d(LOG_TAG, routeIds.toString());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.error_route_list) + firebaseError.getMessage());
            }
        });
    }


    /**
     * Bring vehicle list
     */
    private void getVehicleList(){
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_VEHICLE_LIST_PATH);
        Query queryRef = ref.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List vehicles = new ArrayList();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    // bring only available vehicles
                    boolean isTrip = (boolean) shot.child(Constants.VEHICLE_TRIP_ON).getValue();
                    if(!isTrip)
                    {
                        vehicles.add(shot.getKey());
                    }
                }
                if(vehicles.size()>0){
                    mVehicles = MDCUtils.convertListToStringArray(vehicles);
                }
//                Log.d(LOG_TAG, vehicles.toString());

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.error_route_list) + firebaseError.getMessage());
            }
        });
    }


    /*
     * As soon as user clicks tripOn button it triggers
     * Set several values under selected vehicle
     * 1. update Vehicle info under 'vehicles'
     * 2. update front Vehicle's rearVehicle value
     * 3. register vehicle name under 'routes'/vehicles
    */
    public void setTripOn() {

        String frontCar = (String) mFrontVehicleInfo.get(Constants.FRONT_VEHICLE_ID);
        String frontIndex = (String) mFrontVehicleInfo.get(Constants.FRONT_VEHICLE_INDEX);

        // update Vehicle info under 'vehicles'
        Firebase currentVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
        Map<String, Object> currentTripOn = new HashMap<String, Object>();
        currentTripOn.put(Constants.VEHICLE_TRIP_ON, true);
        currentTripOn.put(Constants.VEHICLE_CURRENT_ROUTE, mRouteId);
        currentTripOn.put(Constants.VEHICLE_FRONT, frontCar);
        currentTripOn.put(Constants.VEHICLE_REAR, "");
        currentTripOn.put(Constants.VEHICLE_PASSENGER_SUM, 0);
        currentTripOn.put(Constants.VEHICLE_FARE_SUM, 0);
        currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
        currentVehicle.updateChildren(currentTripOn);

        // update front Vehicle's rearVehicle value if exists
        if(!frontCar.equalsIgnoreCase("")){
            Firebase frontVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + frontCar);
            Map<String, Object> frontTripOn = new HashMap<String, Object>();
            frontTripOn.put(Constants.VEHICLE_REAR, mVehicleId);
            frontTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
            frontVehicle.updateChildren(frontTripOn);
            // save into Preference in order to delete 'rear' on front vehicle when log off
            MDCUtils.put(getApplicationContext(), Constants.VEHICLE_FRONT, frontCar);
        }

        // register vehicle name under 'routes'/vehicles
        String trip = MDCUtils.getTipNode(mVehicleId);
        Firebase routeVehicle = mFirebase.child(Constants.FIREBASE_ROUTE_LIST_PATH + "/" + mRouteId + "/vehicles/" + mVehicleId);
        Map<String, Object> routeUpdate = new HashMap<String, Object>();
        routeUpdate.put(Constants.VEHICLE_TRIP, trip);
        if(!frontCar.equalsIgnoreCase("")){
            routeUpdate.put(Constants.VEHICLE_INDEX, (Integer.parseInt(frontIndex)+1));
        }else{
            routeUpdate.put(Constants.VEHICLE_INDEX, 0);
        }
        routeVehicle.updateChildren(routeUpdate);

        // update user login by adding route & vehicle
        String userUid = MDCUtils.getValue(this, Constants.USER_UID, "");
        String userPath = MDCUtils.getValue(this, Constants.USER_PATH, "");
        if(!userUid.equalsIgnoreCase("") && !userPath.equalsIgnoreCase("")) {
            Firebase currentUser = mFirebase.child(Constants.FIREBASE_USER_LIST_PATH + "/" + userUid + "/" + userPath);
            Map<String, Object> userData = new HashMap<String, Object>();
            userData.put(Constants.AUTH_ROUTE, mRouteId);
            userData.put(Constants.AUTH_VEHICLE, mVehicleId);
            currentUser.updateChildren(userData);
        }

        // turn on 'Trip On'
        MDCUtils.put(getApplicationContext(), Constants.VEHICLE_TRIP_ON, true);

        // reset global variables in case of the previous instance still remains
        resetGlabalVariables();
    }


    /**
     * Reset the count for several static variables in MDCMainActivity
     */
    private void resetGlabalVariables(){
//        MDCMainActivity.mPassengerCount = 0;
        MDCMainActivity.mPassengerCountSum = 0;
//        MDCMainActivity.mFareCash = 0;
        MDCMainActivity.mFareCashSum = 0;
        MDCMainActivity.fareTransactionId = 1;
    }

    /**
     * Search front vehicle info by looking maximum 'index' under /routes/{routeId}/vheicles/{vehicle}/index
     */
    private void searchFrontVehicle(){
        mFrontVehicleInfo.clear();
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_ROUTE_LIST_PATH);
        Firebase childRef = ref.child(mRouteId + "/vehicles");
        Query queryRef = childRef.orderByChild("index");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String front = "";
                String index = "0";
                if(snapshot.getValue()==null){
//                    Log.d(LOG_TAG, "You are the first");
                }else {
                    // get highest index
                    for(DataSnapshot shot : snapshot.getChildren()){
                        front = shot.getKey();
                        index = shot.child("index").getValue().toString();
//                        Log.d(LOG_TAG, "child...." + shot.getKey() + " ===> "+ shot.child("index").getValue().toString());
                    }
//                    Log.d(LOG_TAG, front + " ==> " + index);
                }
                mFrontVehicleInfo.put(Constants.FRONT_VEHICLE_ID, front);
                mFrontVehicleInfo.put(Constants.FRONT_VEHICLE_INDEX, index);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.error_route_list) + firebaseError.getMessage());
            }
        });

    }


    /**
     * Bring stops info from firebase and save into Arraylist
     */
    private void getStopsDetail(){
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_ROUTE_LIST_PATH + "/" + mRouteId +"/routeStop");
        Query queryRef = ref.orderByChild(Constants.FARESTOP_SORT_INDEX);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<StopVO> list = new ArrayList<StopVO>();
                for(DataSnapshot shot : dataSnapshot.getChildren()){
                    StopVO stopVO = shot.getValue(StopVO.class);
                    // only store 'stop'
                    if(Constants.STOPS_TYPE.equalsIgnoreCase(stopVO.getType())) {
                        list.add(stopVO);
                    }
                }
                mStops = new StopVO[list.size()];
                mStops = list.toArray(mStops);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * Bring stop groups info - fareStops - from firebase and save into Arraylist
     */
    private void getStopGroupsDetail(){
//        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_FARE_LIST_PATH + "/" + mRouteId +"/fareStops");
//        Query queryRef = ref.orderByKey();
//        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<StopGroupVO> list = new ArrayList<StopGroupVO>();
//                for(DataSnapshot shot : dataSnapshot.getChildren()){
//                    list.add(new StopGroupVO(Integer.parseInt(shot.getKey()), shot.getValue()+""));
//                }
//                mStopGroups = new StopGroupVO[list.size()];
//                mStopGroups = list.toArray(mStopGroups);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_FARE_LIST_PATH + "/" + mRouteId +"/fareStops");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<StopGroupVO> list = new ArrayList<StopGroupVO>();
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                for(Map.Entry<String, Object> entry : td.entrySet()){
                    Map single = (Map) entry.getValue();
                    int index = Integer.parseInt(single.get(Constants.FARESTOP_SORT_INDEX)+"");
                    String name = (String) single.get(Constants.FARESTOP_NAME);
                    list.add(new StopGroupVO(index, name));
                }

                Log.d(LOG_TAG, "Before : " + Arrays.toString(list.toArray()));
                // Need to sort by sortIndex
                Collections.sort(list, new Comparator<StopGroupVO>() {
                    @Override
                    public int compare(StopGroupVO stop1, StopGroupVO stop2) {
                        // notice the cast to (Integer) to invoke compareTo
                        return ((Integer)stop1.getIndex()).compareTo(stop2.getIndex());
                    }
                });
                Log.d(LOG_TAG, "After : " + Arrays.toString(list.toArray()));

                mStopGroups = new StopGroupVO[list.size()];
                mStopGroups = list.toArray(mStopGroups);
//                Log.d(LOG_TAG, Arrays.toString(list.toArray()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * Bring fares info - fares/{route_ID}/fareSets - from firebase and save into Arraylist
     */
    private void getFaresDetail(){

        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_FARE_LIST_PATH + "/" + mRouteId + "/fareSets");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                for(Map.Entry<String, Object> entry : td.entrySet()){
                    Map single = (Map) entry.getValue();
                    String tag = (String) single.get(Constants.FARESET_PASSENGER_TAG);
                    String fare = (String) single.get(Constants.FARESET_FARESET);
                    if(StringUtils.equalsIgnoreCase(tag, Constants.FARESET_ADULT)){
                        mAdultFares = fare;
                    }else if(StringUtils.equalsIgnoreCase(tag, Constants.FARESET_SENIOR)){
                        mSeniorFares = fare;
                    }else if(StringUtils.equalsIgnoreCase(tag, Constants.FARESET_STUDENT)){
                        mStudentFares = fare;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * This task make sure Route text becomes available after populating Route & Vehicle data
     */
    public class CheckRouteNVehicleTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            boolean isDone = false;
            // check whether information is updated and ready to go
            while(!isDone){
                if(mRouteIds!=null && mRouteIds.length > 0 && mRouteNames!=null && mRouteNames.length > 0 && mVehicles!=null && mVehicles.length > 0){
                    isDone = true;
                }
                try {
                    Thread.sleep(Constants.THREAD_SLEEP);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRouteTxt.setEnabled(true);
            mRouteTxt.setTextColor(Color.WHITE);
            hideProgressDialog();
        }
    }


    /**
     * This task make sure Route text becomes available after populating Route & Vehicle data
     */
    public class CheckStopNFareTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            boolean isDone = false;
            // check whether information is updated and ready to go
            while(!isDone){
                if(mStops!=null && mStops.length > 0 && mStopGroups!=null && mStopGroups.length > 0 && mAdultFares !=null && mAdultFares.length() > 0){
                    isDone = true;
                }
                try {
                    Thread.sleep(Constants.THREAD_SLEEP);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTripOnTxt.setEnabled(true);
//            mTripOnTxt.setTextColor(Color.WHITE);
        }
    }
}