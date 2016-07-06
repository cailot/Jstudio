package kr.co.tmoney.mobiledriverconsole;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kr.co.tmoney.mobiledriverconsole.model.vo.RouteVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.RouteDialog;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.VehicleDialog;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOffActivity extends AppCompatActivity implements RouteDialog.PassValueFromRouteDialogListener, VehicleDialog.PassValueFromVehicleDialogListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOffActivity.class);

    private TextView mRouteTxt, mVehicleTxt;
    private Button mLogoutBtn, mTripOnBtn;

    String[] mRouteIds;
    String[] mRouteNames;
    String[] mVehicles;
//    List<String> mVehicles = new ArrayList<String>();;

    List<StopVO> mStops = new ArrayList<StopVO>();

    private String mRouteId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_off_activity);

        initialiseUI();

        // setup Firebase on Android
        Firebase.setAndroidContext(this);


    }

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
        mLogoutBtn = (Button) findViewById(R.id.trip_off_logout_btn);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mTripOnBtn = (Button) findViewById(R.id.trip_off_tripon_btn);
        mTripOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getRouteList();
        getVehicleList();


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
                Log.d(LOG_TAG, "Vehicle Event");
                showVehicleDialog();
                break;
            case R.id.trip_off_logout_btn :
                Log.d(LOG_TAG, "Logout Event");
                break;
            case R.id.trip_off_tripon_btn :
                Log.d(LOG_TAG, "TripOn Event");
                turnOnTripOn();
                break;
        }
    }



    private void turnOnTripOn() {
        // clear stops detail
//        mStops.clear();
//        // get stops detail in route
//        getStopsDetail();
//        // save stop details into SharedPreferences
//        saveStopsDetail();
        // save vehicle name into SharedPreferences
        put(MDCConstants.VEHICLE_NAME, mVehicleTxt.getText().toString());

        // switch to TripOn
        Intent i = new Intent(getApplicationContext(), MDCMainActivity.class);
        startActivity(i);
       // setContentView(R.layout.avtivity_next);
    }

    private void saveStopsDetail() {
        int size = mStops.size();
        String[] ids = new String[size];
        String[] names = new String[size];
        String[] types = new String[size];
        String[] lats = new String[size];
        String[] lons = new String[size];
        for(int i=0; i<size; i++){
            StopVO stop = (StopVO) mStops.get(i);
            ids[i] = stop.getId();
            names[i] = stop.getName();
            types[i] = stop.getType();
            lats[i] = stop.getLat()+"";
            lons[i] = stop.getLon()+"";
        }
        String idStr= MDCUtils.convertStringArrayToString(ids);
        String nameStr = MDCUtils.convertStringArrayToString(names);
        String typeStr= MDCUtils.convertStringArrayToString(types);
        String latStr = MDCUtils.convertStringArrayToString(lats);
        String lonStr= MDCUtils.convertStringArrayToString(lons);

        put(MDCConstants.STOPS_ID_IN_ROUTE, idStr);
        put(MDCConstants.STOPS_NAME_IN_ROUTE, nameStr);
        put(MDCConstants.STOPS_TYPE_IN_ROUTE, typeStr);
        put(MDCConstants.STOPS_LATITUDE_IN_ROUTE, latStr);
        put(MDCConstants.STOPS_LONGITUDE_IN_ROUTE, lonStr);
    }

    private void logout(){
        Firebase ref = new Firebase(MDCConstants.FIREBASE_HOME);
        ref.child("testKey").setValue("myValue");
    }


    @Override
    public void sendRouteName(String id, String name) {
        // update route name according to user's choice
        // set routeId
        mRouteId = id;
        // save routeId into SharedPreferences
        put(MDCConstants.ROUTE_ID, mRouteId);
        // update selected route info in TextView
        mRouteTxt.setText(id + " : " + name);
        // get stops detail in route
        getStopsDetail();
//        // save stop details into SharedPreferences
//        saveStopsDetail();

    }

    @Override
    public void sendVehicleName(String routeName) {
//        // get stops detail in route
//        getStopsDetail();
        // save stop details into SharedPreferences
        saveStopsDetail();
        // update selected vehicle info in TextView
        mVehicleTxt.setText(routeName);
    }

    private void showRouteDialog() {

        RouteDialog routeDialog = new RouteDialog(mRouteIds, mRouteNames);
        // link itself to be updated via 'PassValueFromRouteDialogListener.sendStopName()'
        routeDialog.setPassValueFromRouteDialogListener(TripOffActivity.this);
        routeDialog.show(getFragmentManager(), MDCConstants.ROUTE_DIALOG_TAG);
        // rollback original message to Vehicle TextView
        mVehicleTxt.setText(getString(R.string.trip_off_vehicle_txt_description));
    }

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
        vehicleDialog.show(getFragmentManager(), MDCConstants.VEHICLE_DIALOG_TAG);
    }

    private void getRouteList() {
        Firebase ref = new Firebase(MDCConstants.FIREBASE_HOME + MDCConstants.FIREBASE_ROUTE_LIST_PATH);
        Query queryRef = ref.orderByChild("sortIndex");
        // get only once when need to initialise
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List routeIds = new ArrayList();
                List routeNames = new ArrayList();
                for (DataSnapshot shot : snapshot.getChildren()) {
//                    Log.d(LOG_TAG, "====>  " + shot.getValue());
                    RouteVO route = shot.getValue(RouteVO.class);
                    routeIds.add(shot.getKey());
                    routeNames.add(route.getName());
//                    Log.d(LOG_TAG, shot.getKey() + " ==>");// + route.toString());
                }
                if(routeIds.size()>0 && routeNames.size()>0){
                    mRouteIds = MDCUtils.convertListToStringArray(routeIds);
                    mRouteNames = MDCUtils.convertListToStringArray(routeNames);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "Error happens while getting Route list : " + firebaseError.getMessage());
            }
        });
    }

    /**
     * Bring vehicle list
     */
    private void getVehicleList(){

        Firebase ref = new Firebase(MDCConstants.FIREBASE_HOME + MDCConstants.FIREBASE_VEHICLE_LIST_PATH);
        Query queryRef = ref.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List vehicles = new ArrayList();
                for (DataSnapshot shot : snapshot.getChildren()) {
//                    RouteVO route = shot.getValue(RouteVO.class);
//                    routeIds.add(shot.getKey());
//                    routeNames.add(route.getName());
                    vehicles.add(shot.getKey());
                    Log.d(LOG_TAG, shot.getKey() + " ==>");// + route.toString());
                }
                if(vehicles.size()>0){
                    mVehicles = MDCUtils.convertListToStringArray(vehicles);
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, "Error happens while getting Route list : " + firebaseError.getMessage());
            }
        });


//        queryRef.addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                mVehicles.add(snapshot.getKey());
////                Log.e(LOG_TAG, "Vehicle added ===> " + snapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//            }
//        });
    }


    private void getStopsDetail(){
        mStops.clear();
        Firebase ref = new Firebase(MDCConstants.FIREBASE_HOME + MDCConstants.FIREBASE_ROUTE_LIST_PATH + "/" + mRouteId +"/routeStop");
        Query queryRef = ref.orderByChild("id");
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot shot : dataSnapshot.getChildren()){
                    StopVO stopVO = shot.getValue(StopVO.class);
                    mStops.add(stopVO);
                    Log.e(LOG_TAG, stopVO.toString());
                }
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
        SharedPreferences sharedPreferences = getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        SharedPreferences pref = getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}
