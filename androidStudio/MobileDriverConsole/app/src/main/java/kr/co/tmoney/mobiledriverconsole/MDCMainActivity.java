package kr.co.tmoney.mobiledriverconsole;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.gson.Gson;

import kr.co.tmoney.mobiledriverconsole.geofencing.GeofenceService;
import kr.co.tmoney.mobiledriverconsole.model.vo.StopVO;
import kr.co.tmoney.mobiledriverconsole.ui.fragments.TabAdapter;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

public class MDCMainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MDCUtils.getLogTag(MDCMainActivity.class);

    private TabAdapter mTabAdapter;

    private ViewPager mViewPager;

//    private Fragment mTripOnFragment, mFareFragment;


    private MDCReceiver mMDCReceiver;

    private TabLayout mTabLayout;


    private StopVO[] mStops; // shared by FareFragment

    private double currentLat; // will be assigned from TripOnFragment

    private double currentLon; // will be assigned from TripOnFragment

    public String getVehicleId() {
        return mVehicleId;
    }

    private String mVehicleId; // vehicle id

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLon() {
        return currentLon;
    }

    public void setCurrentLon(double currentLon) {
        this.currentLon = currentLon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

//        mTripOnFragment = new TripOnFragment();
//        mFareFragment = new FareFragment();
        mTabAdapter = new TabAdapter(getSupportFragmentManager(), getApplicationContext());
//        mTabAdapter = new TabAdapter(getSupportFragmentManager(), getApplicationContext(), mTripOnFragment, mFareFragment);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        mVehicleId = getVehicleId(Constants.VEHICLE_NAME);
        mStops = getStopsInfo(Constants.STOPS_ID_IN_ROUTE);



        // Register Receiver
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_SERVICE);
        mMDCReceiver = new MDCReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMDCReceiver, intentFilter);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startIntentService();
                switchTabSelection();
            }
        });

        // Firebase set up
        Firebase.setAndroidContext(this);
    }

    /**
     * switch tab's selection
     */
    private void switchTabSelection() {
        int selected = mTabLayout.getSelectedTabPosition();
        Log.d(LOG_TAG, "Selected : " + selected);
        if(selected==Constants.TRIP_ON_FRAGMENT_TAB){
            mTabLayout.getTabAt(Constants.FARE_FRAGMENT_TAB).select();
        }else{
            mTabLayout.getTabAt(Constants.TRIP_ON_FRAGMENT_TAB).select();
        }
    }

    private void startIntentService() {
        Intent intent = new Intent(this, GeofenceService.class);
        startService(intent);
        Log.e(LOG_TAG, "startIntentService()");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMDCReceiver);
        Log.d(LOG_TAG, "onStop()");
    }

    /**
     * Intent receiver from IntentService
     */
    public class MDCReceiver extends BroadcastReceiver{

        public MDCReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(Constants.GEO_UPDATE_INTENT_SERVICE);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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

}
