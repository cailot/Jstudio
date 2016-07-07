package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.geofencing.GeofenceService;
import kr.co.tmoney.mobiledriverconsole.model.vo.TripVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.VehicleVO;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOnFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

// implements OnMapReadyCallback, LocationListener, ResultCallback<Status> {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOnFragment.class);

    Context mContext;

    private MapView mMapView;

    private GoogleMap mGoogleMap;

    private Marker mMarker;

    private MarkerOptions mMarkerOptions;

    private TextView mTripOnTxt; // vehicle info

    private ImageView mFrontVehicleImg, mRearVehicleImg;

    private TextView mFrontVehicleTxt, mRearVehicleTxt;



    // From MDCMainActivity

    protected GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    /**
     * Geofencing Data
     */
    protected ArrayList<Geofence> mGeofences;

    private String mVehicleId;

    private String mRouteId;

    private VehicleVO mVehicle;

    private TripVO mTrip;

    private Firebase mFirebase;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_on_activity, null);
        mContext = container.getContext();

        mVehicleId = getValue(Constants.VEHICLE_NAME, "No available vehicle");
        // get VehicleInfo from Firebase

        mTripOnTxt = (TextView) view.findViewById(R.id.trip_on_txt);
        mTripOnTxt.setText(mVehicleId);
        mTripOnTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                registerGeofences();
            }
        });

        mFrontVehicleImg = (ImageView) view.findViewById(R.id.trip_on_front_img);
        mRearVehicleImg = (ImageView) view.findViewById(R.id.trip_on_rear_img);
        mFrontVehicleTxt = (TextView) view.findViewById(R.id.trip_on_front_info);
        mRearVehicleTxt = (TextView) view.findViewById(R.id.trip_on_rear_info);


        mMapView=(MapView)view.findViewById(R.id.trip_on_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        // set up googleApi
        buildGoogleApiClient();

        // inflate dummy Geofence list
        populateGeofenceList();

        // trigger IntentService
       // registerGeofences();

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initialiseMap();

    }

    private void initialiseMap() {
        LatLng melbourne = new LatLng(-37.835909, 144.981128);
        mMarkerOptions = new MarkerOptions();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(melbourne, Constants.GOOGLE_MAP_ZOOM_LEVEL));
        // temporary showing geofences
        showGeofences();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "GoogleApiClient onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.GOOGLE_MAP_POLLING_INTERVAL);

        // Android SDK 23
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
            Log.d(LOG_TAG, "Permission check needs later....");

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        // activate Geofences
//        activateGeofences();

        // initialise TripVO
//        mVehicle = new VehicleVO();
        mTrip = new TripVO();
        mFirebase = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_TRIP_LIST_PATH);
        mRouteId = getValue(Constants.ROUTE_ID, "");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "GoogleApiClient onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "GoogleApiClient onConnectionFailed");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
            Log.d(LOG_TAG, "GoogleApiClient is now connected");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMarker!=null){
            mMarker.remove();
        }
        //String msg = mVehicleId + "\n" + "Lat : " + location.getLatitude()+"\nLon : " + location.getLongitude();
        String msg = mVehicleId + "\n";
        if(location.hasSpeed()){
//            msg += "Speed : " + (location.getSpeed()*3600/1000 +"km/h") + "\n";
        }
        msg += "Speed : " + 0.0 +"km/h" + "\n";
        msg += "Passenger : " + "0"; // will subscribe passenger count in future....

        mTripOnTxt.setText(msg);
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mMarkerOptions.position(current)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker));

        mMarker = mGoogleMap.addMarker(mMarkerOptions);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, Constants.GOOGLE_MAP_ZOOM_LEVEL));
//        Log.d(LOG_TAG, msg);

        // insert transaction into trips
        auditTransaction(location.getLatitude(), location.getLongitude());

        // display dummy data for front & rear vehicle
        updateVehicles();
    }

    private void auditTransaction(double lat, double lon) {
        String key = MDCUtils.getTipNode(mVehicleId);
        mTrip.setCurrentStopId("1");
        mTrip.setDriverId("jinhyung.seo");
        mTrip.setRouteKey(mRouteId);
        Firebase vehicleRef = mFirebase.child(key);
        vehicleRef.setValue(mTrip);
        Log.d(LOG_TAG, mTrip.toString());


    }

    private void updateVehicles() {
        int front = MDCUtils.getRandomNumberInRange(20, 70);
        int rear = MDCUtils.getRandomNumberInRange(20, 70);
        if(front<30){
            mFrontVehicleImg.setImageResource(R.drawable.bus_background_danger);
        }else if(front<40){
            mFrontVehicleImg.setImageResource(R.drawable.bus_background_normal);
        }else{
            mFrontVehicleImg.setImageResource(R.drawable.bus_background_safe);
        }
        mFrontVehicleTxt.setText(front + " m");
        if(rear<30){
            mRearVehicleImg.setImageResource(R.drawable.bus_background_danger);
        }else if(rear<40){
            mRearVehicleImg.setImageResource(R.drawable.bus_background_normal);
        }else{
            mRearVehicleImg.setImageResource(R.drawable.bus_background_safe);
        }
        mRearVehicleTxt.setText(rear + " m");

    }

    private PendingIntent getGeofencePendingIntent(){
        Intent intent = new Intent(getActivity(), GeofenceService.class);
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofences);
        return builder.build();
    }

//    private void registerGeofences() {
//        if(mGoogleApiClient==null || !mGoogleApiClient.isConnected()){
//            Log.e(LOG_TAG, getString(R.string.not_connected));
//            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_LONG).show();
//            return;
//        }
//        try{
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    getGeofencingRequest(),
//                    getGeofencePendingIntent()
//            ).setResultCallback(this);
//        }catch (SecurityException e){
//            Log.e(LOG_TAG, e.getMessage());
//        }
//    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }






    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }











    //////////////////////////////////////////////////

    private void populateGeofenceList() {
        mGeofences = new ArrayList<Geofence>();
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Flinders")
                .setCircularRegion(-37.8210934,144.9686004, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Bourke Street")
                .setCircularRegion(-37.813471,144.9655192, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Around DHHS")
                .setCircularRegion(-37.8098068,144.9656684, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Domain Interchage")
                .setCircularRegion(-37.8339319,144.9714436, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
    }



    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();

    static {
        LANDMARKS.put("Domain Interchange", new LatLng(-37.8339319,144.9714436));

        LANDMARKS.put("Around DHHS", new LatLng(-37.8098068,144.9656684));

        LANDMARKS.put("Flinders Station", new LatLng(-37.8210934,144.9686004));

        LANDMARKS.put("Bourke Malls", new LatLng(-37.813471,144.9655192));
    }

    private void showGeofences(){
        for (Map.Entry<String, LatLng> entry : LANDMARKS.entrySet()) {
            mGoogleMap.addMarker(new MarkerOptions()
                .position((LatLng) entry.getValue())
            );
        }

    }

//    public void activateGeofences() {
//        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(getContext(), "Google API Client not connected!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            LocationServices.GeofencingApi.addGeofences(
//                    mGoogleApiClient,
//                    getGeofencingRequest(),
//                    getGeofencePendingIntent()
//            ).setResultCallback(this); // Result processed in onResult().
//        } catch (SecurityException securityException) {
//            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//        }
//    }

}
