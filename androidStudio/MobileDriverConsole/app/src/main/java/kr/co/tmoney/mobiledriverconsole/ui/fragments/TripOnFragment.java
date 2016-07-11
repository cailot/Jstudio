package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
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
import kr.co.tmoney.mobiledriverconsole.model.vo.TripVO;
import kr.co.tmoney.mobiledriverconsole.model.vo.VehicleVO;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOnFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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

    private VehicleVO mFrontVehicle;

    private VehicleVO mRearVehicle;

    private String mRouteId;

    private TripVO mTrip;

    private Firebase mFirebase;

//    private MDCMainActivity mMainActivity;

    int mGpsCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_on_activity, null);
        mContext = container.getContext();
//        mMainActivity = (MDCMainActivity) getActivity();

        // build UI
        initialiseUI(savedInstanceState, view);

        // set up googleApi
        buildGoogleApiClient();


        // set up basic info
        initialiseTripInfo();

        // inflate dummy Geofence list
        populateGeofenceList();

        return view;
    }


    /**
     * build up UI & MapView
     * @param savedInstanceState
     * @param view
     */
    private void initialiseUI(Bundle savedInstanceState, View view) {
        mTripOnTxt = (TextView) view.findViewById(R.id.trip_on_txt);
//        mTripOnTxt.setText(mVehicleId);
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

        mMapView = (MapView) view.findViewById(R.id.trip_on_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng melbourne = new LatLng(-37.835909, 144.981128);
        mMarkerOptions = new MarkerOptions();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(melbourne, Constants.GOOGLE_MAP_ZOOM_LEVEL));
        mGoogleMap.setTrafficEnabled(true);
        // temporary showing geofences
//        showGeofences();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "GoogleApiClient onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.GOOGLE_MAP_POLLING_INTERVAL);

        // Android SDK 23
        requestLocationUpdate();


        // activate Geofences
//        activateGeofences();
    }

    /**
     * GPS permission handles - Android SDK 23
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.GPS_PERMISSION_GRANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdate();
                    Log.e(LOG_TAG, "User grants GPS permission");
                } else {
                    Log.e(LOG_TAG, "User should grant a permission to proceed");
                }
                return;
            }
        }
    }


    /**
     * From SDK 23, device checks the required permission in runtime.
     * This will also cover Android Marshmallow 6 or the above for furture device upgrade
     */
    private void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.GPS_PERMISSION_GRANT);
        }else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

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


//    /**
//     * Check whether current tab is selected or not
//     * @param isVisibleToUser
//     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(this.isVisible()){
//            if(isVisibleToUser){
//                Log.d(LOG_TAG, "TripOnFragment is visible");
//            }else{
//                Log.d(LOG_TAG, "TripOnFragment is not visible");
//            }
//        }
//    }

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

    /**
     * Update several informaton
     * 1. update Marker on map
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mGpsCount++;
        if(mGpsCount > Constants.GPS_UPDATE_MAXIMUM){ // just in case, prevent count going out of int range
            mGpsCount=0;
        }
        if(mGpsCount==10){ // make sure it already got the front/rear vehicle id from initialiseTripInfo(), is there more elegant way to implement ??
            updateFrontAndBackVehicles();
        }
        // update Map
        if(mMarker!=null){
            mMarker.remove();
        }
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mMarkerOptions.position(current)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker));
        mMarker = mGoogleMap.addMarker(mMarkerOptions);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, Constants.GOOGLE_MAP_ZOOM_LEVEL));

        // update current vehicle Info
        String msg = mVehicleId + "\n";
        if (location.hasSpeed()) {
            msg += "Speed : " + String.format( "%.2f", location.getSpeed() * 3600 / 1000) + " km/h" + "\n";
        }
        msg += "Passenger : " + "0"; // will subscribe passenger count in future....
        mTripOnTxt.setText(msg);

        if(mGpsCount%Constants.GPS_UPDATE_VEHICLES_INTERVAL ==0) { // runs every 10 seconds
            new SubscribeDistanceTask().execute(location.getLatitude(), location.getLongitude());
        }
    }

    /**
     * Initialise Trip info such as
     * 1. initialise VOs
     * 2. initialise Firebase
     * 3. Retreive routeId
     * 4. Retreive vehicleId
     * 5. Retreive front/rear vehicleIds & update GPS
     */
    private void initialiseTripInfo() {
        mTrip = new TripVO();
        mFrontVehicle = new VehicleVO();
        mRearVehicle = new VehicleVO();

        mFirebase = new Firebase(Constants.FIREBASE_HOME);
        mRouteId = getValue(Constants.ROUTE_ID, "No avaiable route");
        mVehicleId = getValue(Constants.VEHICLE_NAME, "No available vehicle");

        // get front/rear car info
        Firebase vehicleRef = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
        vehicleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Constants.VEHICLE_FRONT).getValue()!=null){
                    mFrontVehicle.setId(dataSnapshot.child(Constants.VEHICLE_FRONT).getValue().toString());
                }
                if(dataSnapshot.child(Constants.VEHICLE_REAR).getValue()!=null){
                    mRearVehicle.setId(dataSnapshot.child(Constants.VEHICLE_REAR).getValue().toString());
                }
                Log.d(LOG_TAG, "Front : " + mFrontVehicle.getId() + " - Rear :" + mRearVehicle.getId());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * subscribe updated front/rear vehicle's GPS
     */
    private void updateFrontAndBackVehicles() {
        // update front gps
//        Log.d(LOG_TAG, "==> Front : " + mFrontVehicle.getId() + " - Rear :" + mRearVehicle.getId());
        if(mFrontVehicle.getId()!=null && !mFrontVehicle.getId().equalsIgnoreCase("")) {
            Firebase frontRef = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mFrontVehicle.getId());
            Log.d(LOG_TAG,"Front - path : " + frontRef.getPath());
            frontRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Constants.VEHICLE_LATITUDE).getValue() != null) {
                        mFrontVehicle.setLat((Double) dataSnapshot.child(Constants.VEHICLE_LATITUDE).getValue());
                    }
                    if (dataSnapshot.child(Constants.VEHICLE_LONGITUDE).getValue() != null) {
                        mFrontVehicle.setLon((Double) dataSnapshot.child(Constants.VEHICLE_LONGITUDE).getValue());
                    }
                    Log.d(LOG_TAG, "Front Vehicle's  Id : " + mFrontVehicle.getId() +  " , lat : "  + mFrontVehicle.getLat() + " , lon : " + mFrontVehicle.getLon());
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        // update rear gps
        if(mRearVehicle.getId()!=null && !mRearVehicle.getId().equalsIgnoreCase("")) {
            Firebase rearRef = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mRearVehicle.getId());
            rearRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Constants.VEHICLE_LATITUDE).getValue() != null) {
                        mRearVehicle.setLat((Double) dataSnapshot.child(Constants.VEHICLE_LATITUDE).getValue());
                    }
                    if (dataSnapshot.child(Constants.VEHICLE_LONGITUDE).getValue() != null) {
                        mRearVehicle.setLon((Double) dataSnapshot.child(Constants.VEHICLE_LONGITUDE).getValue());
                    }
                    Log.d(LOG_TAG, "Rear Vehicle's  Id : " + mRearVehicle.getId() +  " , lat : "  + mRearVehicle.getLat() + " , lon : " + mRearVehicle.getLon());
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }


    /**
     * This is worker thread class running every 10 secs for...
     * 1. update current GPS
     * 2. bring distance info between front/rear vehicles
     */
    public class SubscribeDistanceTask extends AsyncTask<Double, Void, Map> {
        @Override
        protected Map doInBackground(Double... doubles) {

            double lat = doubles[0];
            double lon = doubles[1];

            // update GPS on current vehicle
            Firebase currentVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
            Map<String, Object> currentTripOn = new HashMap<String, Object>();
            currentTripOn.put(Constants.VEHICLE_LATITUDE, lat);
            currentTripOn.put(Constants.VEHICLE_LONGITUDE, lon);
            currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
            currentVehicle.updateChildren(currentTripOn);

            // get distance between current vehicle & fron/rear vehicle by calling Google Distance Matrix
            Map<String, String[]> distances = new HashMap<String, String[]>();
            String[] frontInfo = new String[]{"0","0"};
            String[] rearInfo = new String[]{"0","0"};
            if(mFrontVehicle==null || mFrontVehicle.getLat() == 0.0 || mFrontVehicle.getLon()==0.0){
                // no need to subscribe
            }else {
                frontInfo = MDCUtils.getDistanceInfo(lat, lon, mFrontVehicle.getLat(), mFrontVehicle.getLon());
            }
            if(mRearVehicle==null || mRearVehicle.getLat() == 0.0 || mRearVehicle.getLon()==0.0){
                // no need to subscribe
            }else {
                rearInfo = MDCUtils.getDistanceInfo(lat, lon, mRearVehicle.getLat(), mRearVehicle.getLon());
            }
            distances.put(Constants.VEHICLE_FRONT, frontInfo);
            distances.put(Constants.VEHICLE_REAR, rearInfo);
            return distances;
        }

        @Override
        protected void onPostExecute(Map info) {
            String[] frontInfo = (String[])info.get(Constants.VEHICLE_FRONT);
            String[] rearInfo = (String[])info.get(Constants.VEHICLE_REAR);
            int frontDistance = Integer.parseInt(frontInfo[0]);
            int rearDistance = Integer.parseInt(rearInfo[0]);
            if(frontDistance < Constants.DISTANCE_THRESHOLD_DANGER){
                mFrontVehicleImg.setImageResource(R.drawable.bus_background_danger);
                mFrontVehicleTxt.setTextColor(Color.WHITE);
            }else if(frontDistance < Constants.DISTANCE_THRESHOLD_SAFE){
                mFrontVehicleImg.setImageResource(R.drawable.bus_background_normal);
                mFrontVehicleTxt.setTextColor(Color.BLACK);
            }else{
                mFrontVehicleImg.setImageResource(R.drawable.bus_background_safe);
                mFrontVehicleTxt.setTextColor(Color.WHITE);
            }
            mFrontVehicleTxt.setText(MDCUtils.getDistanceFormat(frontDistance) + "\n" + frontInfo[1]);
            if(rearDistance < Constants.DISTANCE_THRESHOLD_DANGER){
                mRearVehicleImg.setImageResource(R.drawable.bus_background_danger);
                mRearVehicleTxt.setTextColor(Color.WHITE);
            }else if(rearDistance < Constants.DISTANCE_THRESHOLD_SAFE){
                mRearVehicleImg.setImageResource(R.drawable.bus_background_normal);
                mRearVehicleTxt.setTextColor(Color.BLACK);
            }else{
                mRearVehicleImg.setImageResource(R.drawable.bus_background_safe);
                mRearVehicleTxt.setTextColor(Color.WHITE);
            }
            mRearVehicleTxt.setText(MDCUtils.getDistanceFormat(rearDistance) + "\n" + rearInfo[1]);
        }
    }


    /**
     * Insert transaction record into Firebase
     * @param lat
     * @param lon
     */
    private void auditTransaction(double lat, double lon) {
        String key = MDCUtils.getTipNode(mVehicleId);
        mTrip.setCurrentStopId("1");
        mTrip.setDriverId("jinhyung.seo");
        mTrip.setRouteKey(mRouteId);
        mTrip.setLat(lat);
        mTrip.setLon(lon);
        Firebase vehicleRef = mFirebase.child(key);
//        vehicleRef.setValue(mTrip);
//        Log.d(LOG_TAG, mTrip.toString());
    }


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
