package kr.co.tmoney.mobiledriverconsole;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import kr.co.tmoney.mobiledriverconsole.geofencing.GeofenceService;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.fragments.TabAdapter;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

public class MDCMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = MDCUtils.getLogTag(MDCMainActivity.class);

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

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


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
        mStops = getStopsInfo(Constants.STOPS_ID_IN_ROUTE);

        buildGoogleApiClient();

        // Register Receiver
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_SERVICE);
        mGeoReceiver = new GeoReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mGeoReceiver, intentFilter);


        // Firebase set up
        Firebase.setAndroidContext(this);
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
            Log.d(LOG_TAG, "GoogleApiClient is now connected");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        stopIntentService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGeoReceiver);
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
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
            int action = intent.getIntExtra(Constants.GEOFENCE_INTENT_ACTION, Constants.NO_VALUE);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            switch (action){
                case Geofence.GEOFENCE_TRANSITION_ENTER :
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
        Log.d(LOG_TAG, "onConnected()");
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
            message = "구글 플레이 서비스에 연결되지 않았습니다.";
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
                        message = "Geofence에 추가되었습니다.";
                    } else {
                        switch (status.getStatusCode()) {
                            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                                message = "Geonfence에서 거부되었습니다.";
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                                message = "Geonfence를 너무 많이 등록하였습니다.";
                                break;
                            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                                message = "Geonfence를 너무 많은 펜딩인텐트를 등록하였습니다.";
                                break;
                            default:
                                message = "알수 없는 에러가 발생하였습니다.";
                        }
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "구글 플레이 서비스에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
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
                        message = "Geofence에서 제거되었습니다.";
                    } else {
                        message = "Geonfence에서 거부되었습니다." + status.getStatusCode();
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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


}
