package kr.co.tmoney.mobiledriverconsole;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import kr.co.tmoney.mobiledriverconsole.ui.fragments.MDCTabAdapter;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

// Jin desk 2nd
public class MDCMainActivity extends AppCompatActivity {// implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private static final String LOG_TAG = MDCUtils.getLogTag(MDCMainActivity.class);

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MDCTabAdapter mMDCTabAdapter;

    //public GeoReceiver geoReceiver;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

//    private GoogleApiClient mGoogleApiClient;
//
//    private LocationRequest mLocationRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise GoogleApiClient
//        buildGoogleApiClient();
//        mGoogleApiClient.connect();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mMDCTabAdapter = new MDCTabAdapter(getSupportFragmentManager());
        mMDCTabAdapter = new MDCTabAdapter(getSupportFragmentManager(), getApplicationContext());

        // initialise Receiver to update Geo info and GeoFire
//        IntentFilter intentFilter = new IntentFilter(GeoReceiver.MDC_RECEIVER);
//        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
//        geoReceiver = new GeoReceiver();
//        registerReceiver(geoReceiver, intentFilter);



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMDCTabAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();
//        startIntents();
//        if(!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()){
////            mGoogleApiClient.connect();
//            Log.d(LOG_TAG, "Google Api Client is now connected");
//        }

        Log.d(LOG_TAG, "onStart()");
    }

    private void startIntents() {
//        Intent intent = new Intent(this, GeoFireService.class);
//        intent.putExtra("fromMain", "I am a Main");
//        startService(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister GeoReceiver
//        if(geoReceiver!=null) {
//            unregisterReceiver(geoReceiver);
//        }
//        if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
//            mGoogleApiClient.disconnect();
//        }
        Log.d(LOG_TAG, "onStop()");
    }

    /**
     *
     * This Receiver will update MainActivity
     * 1) Update distance of vehicels - GeoUpdateService
     * 2) Switch view according to GeoFence - GeoFireService
     *
     */
//    public class GeoReceiver extends BroadcastReceiver{
//
//        public static final String MDC_RECEIVER = MDCConstants.PACKAGE_NAME + ".ALL_DONE";
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String response = intent.getStringExtra(MDCConstants.GEOFIRE_INTENT_SERVICE);
//            Toast.makeText(context, response, Toast.LENGTH_LONG);
//        }
//    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


//    protected synchronized void buildGoogleApiClient(){
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }



//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Log.d(LOG_TAG, "GoogleApiClient onConnected");
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(MDCConstants.GOOGLE_MAP_POLLING_INTERVAL);
//
//        // Android SDK 23
//        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
//            Log.d(LOG_TAG, "Permission check needs later....");
//
//        }
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d(LOG_TAG, "GoogleApiClient onConnectionSuspended");
//        mGoogleApiClient.connect();
//    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(LOG_TAG, "GoogleApiClient onConnectionFailed");
//    }

//    public GoogleApiClient getGoogleApiClient(){
//        return mGoogleApiClient;
//    }
//    public LocationRequest getLocationRequest(){
//        return mLocationRequest;
//    }
}
