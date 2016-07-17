package kr.co.tmoney.mobiledriverconsole;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
import org.apache.log4j.Logger;

import java.util.ArrayList;

import kr.co.tmoney.mobiledriverconsole.geofencing.GeofenceService;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.fragments.TabAdapter;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

public class MDCMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = MDCUtils.getLogTag(MDCMainActivity.class);

    private Logger logger = Logger.getLogger(LOG_TAG);

    private TabAdapter mTabAdapter;

    private ViewPager mViewPager;

    private GeoReceiver mGeoReceiver;

    private TabLayout mTabLayout;


    private StopVO[] mStops; // shared by FareFragment

    public String getVehicleId() {
        return mVehicleId;
    }

    private String mVehicleId; // vehicle id


    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Geofence> mGeofenceList;

    IntentFilter mIntentFilter;

    public static String currentStopName = "โรงพยาบาลสินแพทย์";

    public static String nextStopName = "ด่านทับช้าง";

    public static int passengerCount;

    public static int fareTransactionId = 1;


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
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mVehicleId = getVehicleId(Constants.VEHICLE_NAME);
        mStops = getStopsInfo(Constants.STOPS_IN_ROUTE);

        buildGoogleApiClient();

        // Register Receiver
        mIntentFilter = new IntentFilter(Constants.BROADCAST_SERVICE);
        mGeoReceiver = new GeoReceiver();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mGeoReceiver, intentFilter);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                // permission granted
                LocalBroadcastManager.getInstance(this).registerReceiver(mGeoReceiver, mIntentFilter);

            }else {
                // need permission to proceed
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Toast.makeText(this, "GPS permission is needed to proceed service", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.GPS_PERMISSION_GRANT);
            }
        }

        // Firebase set up
        Firebase.setAndroidContext(this);

        // Dummy data for presentation purpose
        MDCMainActivity.currentStopName = mStops[3].getName();
        MDCMainActivity.nextStopName = mStops[4].getName();

    }



    /**
     * GPS permission handles - Android SDK 23
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "Request code : " + requestCode);

        if (requestCode == Constants.GPS_PERMISSION_GRANT) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mGeoReceiver, mIntentFilter);

                logger.debug("User grants GPS permission");
            } else {
                logger.error("User should grant a permission to proceed");
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnecting()||!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
            logger.debug("GoogleApiClient is now connected");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.debug("onStop()");
    }

    @Override
    protected void onDestroy() {
        stopIntentService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeoReceiver);
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
        logger.debug("onDestroy()");
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
                    // update current/Next stop
                    updateStopNames(stop);
                    switchTabSelection(Constants.FARE_FRAGMENT_TAB);
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT :
                    switchTabSelection(Constants.TRIP_ON_FRAGMENT_TAB);
                    break;
                default :
                    Toast.makeText(context, "Unknow action received", Toast.LENGTH_SHORT).show();
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
        return stops;
    }

    /**
     * Retreive vehicle id
     * @param key
     * @return
     */
    public String getVehicleId(String key) {
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String id = pref.getString(key, "No available vehicle");
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
        logger.debug("onConnected()");
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
                                message = "Geonfence not available";
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                                message = "Too many Geonfences registered";
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                                message = "Too many PendingIntents for Geonfence";
                                break;
                            default:
                                message = "Unknowkn Error";
                        }
                    }
//                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }); // Result processed in onResult().
        } catch (SecurityException en) {
            logger.debug( en.getMessage());
        }
    }

    /**
     * Stop GeofenceSerivce
     */
    private void stopIntentService() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google Api Client is not connected", Toast.LENGTH_SHORT).show();
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
                        message = "Geofence removed";
                    } else {
                        message = "Geonfence denied : " + status.getStatusCode();
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException securityException) {
            logger.debug( securityException.getMessage());
        }
    }


    /**
     *
     * Create dummy Geofence objects wrapped by GeofencingRequest
     * @return
     */
    private GeofencingRequest getGeofencingRequest(){

        mGeofenceList = new ArrayList<Geofence>();

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Company")
                .setCircularRegion(
                        -37.809003, 144.970886,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("State library")
                .setCircularRegion(
                        -37.810403, 144.964330,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Burke mall")
                .setCircularRegion(
                        -37.813454, 144.965676,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());


        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Flinders Station")
                .setCircularRegion(
                        -37.818224, 144.967852,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("South Bank")
                .setCircularRegion(
                        -37.823425, 144.970170,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Domain Interchange")
                .setCircularRegion(
                        -37.832627, 144.972108,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());

//        mGeofenceList.add(new Geofence.Builder()
//                .setRequestId("Leopold St.")
//                .setCircularRegion(
//                        -37.841150, 144.977329,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setNotificationResponsiveness(1000)
//                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER); Is it enough ???????
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER |
                GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();

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


}
