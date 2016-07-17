package kr.co.tmoney.mobiledriverconsole;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.tmoney.mobiledriverconsole.model.vo.RouteVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopGroupVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.RouteDialog;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.VehicleDialog;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOffActivity extends AppCompatActivity implements RouteDialog.PassValueFromRouteDialogListener, VehicleDialog.PassValueFromVehicleDialogListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOffActivity.class);

    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mRouteTxt, mVehicleTxt, mLogoutTxt, mTripOnTxt;

    String[] mRouteIds;
    String[] mRouteNames;
    String[] mVehicles;

    StopVO[] mStops;

    StopGroupVO[] mStopGroups;

    String mFares;

    private String mRouteId; // ex> 554R

    private String mVehicleId; // ex> SV580005

    private Map mFrontVehicleInfo = new HashMap();

    Firebase mFirebase;

//    private SpannableStringBuilder mRouteMessage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_off_activity);
        // change status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorNavy));
        }else{
        }

        // setup Firebase on Android
        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(Constants.FIREBASE_HOME);
        // bring up all routes & vehicles list to save up time
        getRouteList();
        getVehicleList();

        // loading Splash
        startActivity(new Intent(this, SplashActivity.class));

        // build UI
        initialiseUI();

    }


    /**
     * build up UI and register click events per component
     */
    private void initialiseUI() {
        mRouteTxt = (TextView) findViewById(R.id.trip_off_route_txt);
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
                logger.debug("Route Event");
                showRouteDialog();
                break;
            case R.id.trip_off_vehicle_txt :
//                Log.d(LOG_TAG, "Vehicle Event");
                logger.debug("Vehicle Event");
                showVehicleDialog();
                break;
            case R.id.trip_off_logout_btn :
//                Log.d(LOG_TAG, "Logout Event");
                logger.debug("Logout Event");
                logout();
                break;
            case R.id.trip_off_tripon_btn :
//                Log.d(LOG_TAG, "TripOn Event");
                logger.debug("TripOn Event");
                turnOnTripOn();
                break;
        }
    }


    /**
     * Just about to leave this activity so set up data for next activities
     * 1. save vehicleId
     * 2. change tripOn to true
     * 3. startIntent
     */
    private void turnOnTripOn() {
        // save vehicle name into SharedPreferences
        put(Constants.VEHICLE_NAME, mVehicleId);

        // update DB to indicate starting
        setTripOn();

        // switch to TripOn
        Intent i = new Intent(getApplicationContext(), MDCMainActivity.class);
        startActivity(i);
    }

    /**
     * save all stops information into SharedPreference by Gson
     */
    private void saveStopsDetail() {
        put(Constants.STOPS_IN_ROUTE, mStops);
    }

    /**
     * save stop groups information into SharedPreference by Gson
     */
    private void saveStopGroupsDetail() {
        put(Constants.STOP_GROUPS_IN_ROUTE, mStopGroups);
    }

    /**
     * save all fares information into SharedPreference by Gson
     */
    private void saveFaresDetail() {
        put(Constants.FARES_IN_ROUTE, MDCUtils.getStopGroups(mFares));
    }


    /**
     *  logout
     */
    private void logout(){
//        searchFrontVehicle();
//        setTripOn();
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
        put(Constants.ROUTE_ID, mRouteId);
        // update selected route info in TextView
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        String legend = "Route : ";
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
    }


    /**
     * callback method on vehicleTxt
     * @param routeName
     */
    @Override
    public void sendVehicleName(String routeName) {
        // save stop details into SharedPreferences
        saveStopsDetail();
        // save stop groups into SharedPreferences
        saveStopGroupsDetail();
        // save fares into SharedPreferences
        saveFaresDetail();
        // assign vehicle name to mVehicleId
        mVehicleId = routeName;
        // update selected vehicle info in TextView

        SpannableStringBuilder spannable = new SpannableStringBuilder();
        String legend = "Vehicle : ";
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
        mVehicleTxt.setText(getString(R.string.trip_off_vehicle_txt_description));
    }

    /**
     * Dialog to select vehicles
     */
    private void showVehicleDialog() {
        if(mVehicles.length==0){
            // show warning message
            Toast.makeText(getApplicationContext(), "No available vehicle on the Route", Toast.LENGTH_SHORT).show();
            // update TextView
            mVehicleTxt.setText(getString(R.string.trip_off_vehicle_txt_description));
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
                    RouteVO route = shot.getValue(RouteVO.class);
                    routeIds.add(shot.getKey());
                    routeNames.add(route.getName());
                }
                if(routeIds.size()>0 && routeNames.size()>0){
                    mRouteIds = MDCUtils.convertListToStringArray(routeIds);
                    mRouteNames = MDCUtils.convertListToStringArray(routeNames);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                logger.error("Error happens while getting Route list : " + firebaseError.getMessage());
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
//                    if(!isTrip) for testing purpose
                    {
                        vehicles.add(shot.getKey());
                    }
                }
                if(vehicles.size()>0){
                    mVehicles = MDCUtils.convertListToStringArray(vehicles);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                logger.error("Error happens while getting Route list : " + firebaseError.getMessage());
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
    private void setTripOn() {

        String frontCar = (String) mFrontVehicleInfo.get(Constants.FRONT_VEHICLE_ID);
        String frontIndex = (String) mFrontVehicleInfo.get(Constants.FRONT_VEHICLE_INDEX);

        // update Vehicle info under 'vehicles'
        Firebase currentVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
        Map<String, Object> currentTripOn = new HashMap<String, Object>();
        currentTripOn.put(Constants.VEHICLE_TRIP_ON, true);
        currentTripOn.put(Constants.VEHICLE_CURRENT_ROUTE, mRouteId);
        currentTripOn.put(Constants.VEHICLE_FRONT, frontCar);
        currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
        currentVehicle.updateChildren(currentTripOn);

        // update front Vehicle's rearVehicle value if exists
        if(!frontCar.equalsIgnoreCase("")){
            Firebase frontVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + frontCar);
            Map<String, Object> frontTripOn = new HashMap<String, Object>();
            frontTripOn.put(Constants.VEHICLE_REAR, mVehicleId);
            frontTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
            frontVehicle.updateChildren(frontTripOn);
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
                logger.error("Error happens while getting Route list : " + firebaseError.getMessage());
            }
        });

    }

    /**
     * Bring stops info from firebase and save into Arraylist
     */
    private void getStopsDetail(){
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_ROUTE_LIST_PATH + "/" + mRouteId +"/routeStop");
        Query queryRef = ref.orderByChild("id");
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
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_FARE_LIST_PATH + "/" + mRouteId +"/fareStops");
        Query queryRef = ref.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<StopGroupVO> list = new ArrayList<StopGroupVO>();
                for(DataSnapshot shot : dataSnapshot.getChildren()){
                    list.add(new StopGroupVO(Integer.parseInt(shot.getKey()), shot.getValue()+""));
                }
                mStopGroups = new StopGroupVO[list.size()];
                mStopGroups = list.toArray(mStopGroups);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * Bring fares info - fares/{route_ID}/fareSets/CASH/ADULT/fare - from firebase and save into Arraylist
     */
    private void getFaresDetail(){
        Firebase ref = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_FARE_LIST_PATH + "/" + mRouteId +"/fareSets/CASH/ADULT");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFares = (String) dataSnapshot.child("fare").getValue();
//                Log.e(LOG_TAG, mFares);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /////////////////////////////
    //  SharedPreferences
    /////////////////////////////
    public void put(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, Object value) {
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String json = new Gson().toJson(value);
        editor.putString(key, json);
        editor.commit();
        logger.debug(json);
    }

}
