package kr.co.tmoney.mobiledriverconsole;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;

import kr.co.tmoney.mobiledriverconsole.geofencing.GeofenceService;
import kr.co.tmoney.mobiledriverconsole.ui.fragments.FareFragment;
import kr.co.tmoney.mobiledriverconsole.ui.fragments.TabAdapter;
import kr.co.tmoney.mobiledriverconsole.ui.fragments.TripOnFragment;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

public class MDCMainActivity extends AppCompatActivity {// implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private static final String LOG_TAG = MDCUtils.getLogTag(MDCMainActivity.class);

    private TabAdapter mTabAdapter;

    private ViewPager mViewPager;

    private Fragment mTripOnFragment, mFareFragment;


    private MDCReceiver mMDCReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mTripOnFragment = new TripOnFragment();
        mFareFragment = new FareFragment();
        mTabAdapter = new TabAdapter(getSupportFragmentManager(), getApplicationContext(), mTripOnFragment, mFareFragment);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        // Register Receiver
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_SERVICE);
        mMDCReceiver = new MDCReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMDCReceiver, intentFilter);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntentService();

            }
        });

        // Firebase set up
        Firebase.setAndroidContext(this);


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

    public class MDCReceiver extends BroadcastReceiver{

        public MDCReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(Constants.GEO_UPDATE_INTENT_SERVICE);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

}
