package com.creapple.tms.mobiledriverconsole.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.creapple.tms.mobiledriverconsole.MDCMainActivity;
import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.model.vo.VehicleVO;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
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

//import org.apache.log4j.Logger;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOnFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOnFragment.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    Context mContext;

    private MapView mMapView;

    private GoogleMap mGoogleMap;

    private Marker mMarker;

    private MarkerOptions mMarkerOptions;

    private TextView mTripOnTxt; // vehicle info

    private ImageView mFrontVehicleImg, mRearVehicleImg;

    private TextView mFrontVehicleTxt, mRearVehicleTxt, mFrontVehicleIdTxt, mRearVehicleIdTxt, mCurrentVehicleIdTxt;


    // From MDCMainActivity

    protected GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private Location mLastLocation;

    /**
     * Geofencing Data
     */
    protected ArrayList<Geofence> mGeofences;

    private String mVehicleId;

    private VehicleVO mFrontVehicle;

    private VehicleVO mRearVehicle;

    private String mRouteId;

//    private TripVO mTrip;

    private Firebase mFirebase;

    int mGpsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_on_activity, null);
        mContext = container.getContext();

        // build UI
        initialiseUI(savedInstanceState, view);

        // set up googleApi
        buildGoogleApiClient();

        // set up basic info
        initialiseTripInfo();

        return view;
    }


    /**
     * build up UI & MapView
     * @param savedInstanceState
     * @param view
     */
    private void initialiseUI(Bundle savedInstanceState, View view) {
        mTripOnTxt = (TextView) view.findViewById(R.id.trip_on_txt);
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
        mFrontVehicleIdTxt = (TextView) view.findViewById(R.id.trip_on_front_vehicle_name);
        mRearVehicleIdTxt = (TextView) view.findViewById(R.id.trip_on_rear_vehicle_name);

        mMapView = (MapView) view.findViewById(R.id.trip_on_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mMarkerOptions = new MarkerOptions();
        mGoogleMap.setTrafficEnabled(true);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "GoogleApiClient onConnected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.GOOGLE_MAP_POLLING_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            LatLng last = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker))
                    .position(last)
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(last, Constants.GOOGLE_MAP_ZOOM_LEVEL));
            mGoogleMap.setTrafficEnabled(true);
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

        // Trip Off event updated status from true to false. so release resouces and stop service
        boolean isKeepOn = MDCUtils.getValue(mContext, Constants.VEHICLE_TRIP_ON, false);
        if(!isKeepOn){
            // release resource and exit
            Log.d(LOG_TAG, "Release resouces and going to exit");
            // stop searching location
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            // disconnect google api client
            if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
            }
            return;
        }

        mGpsCount++;
        if(mGpsCount > Constants.GPS_UPDATE_MAXIMUM){ // just in case, prevent count going out of int range
            mGpsCount=0;
        }
        Log.d(LOG_TAG, mGpsCount+"");
        if(mGpsCount==5){ // make sure it already got the front/rear vehicle id from initialiseTripInfo(), is there more elegant way to implement ??
            updateFrontAndBackVehicles();

        }


        // add speed into list to calculate average interval speed
        if(MDCMainActivity.isSpeedCheckTurnOn()){
            MDCMainActivity.addAverageSpeed(location.getSpeed());
        }









        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, Constants.GOOGLE_MAP_ZOOM_LEVEL));
        if(mMarker != null){
            mMarker.remove();
        }

        mMarkerOptions.position(current)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker));
        mMarker = mGoogleMap.addMarker(mMarkerOptions);

        // Decorate Info
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        String speed = String.format( "%.1f", location.getSpeed() * 3600 / 1000);// + " km/h";
        SpannableString speedS = new SpannableString(speed);
        speedS.setSpan(new RelativeSizeSpan(3f), 0, speed.length(), 0);
        speedS.setSpan(new StyleSpan(Typeface.BOLD), 0, speed.length(), 0);
        spannable.append(speedS);
        String km = " Km/h";
        spannable.append(km);
        spannable.append("\n");

        spannable.append(MDCMainActivity.nextStopName);
        spannable.append("\n\n");

        SpannableString currentVehicle = new SpannableString(mVehicleId);
        currentVehicle.setSpan(new ForegroundColorSpan(Color.BLACK), 0, mVehicleId.length(), 0);
        currentVehicle.setSpan(new StyleSpan(Typeface.BOLD), 0, mVehicleId.length(), 0);
        spannable.append(currentVehicle);

        mTripOnTxt.setText(spannable);

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

        mFrontVehicle = new VehicleVO();
        mRearVehicle = new VehicleVO();

        mFirebase = new Firebase(Constants.FIREBASE_HOME);
        mRouteId = MDCUtils.getValue(mContext, Constants.ROUTE_ID, getString(R.string.no_route_found));
        mVehicleId = MDCUtils.getValue(mContext, Constants.VEHICLE_NAME, getString(R.string.no_vehicle_found));

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
//                logger.debug("Front : " + mFrontVehicle.getId() + " - Rear :" + mRearVehicle.getId());
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
//            Log.d(LOG_TAG,"Front - path : " + frontRef.getPath());
            frontRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Constants.VEHICLE_LATITUDE).getValue() != null) {
                        mFrontVehicle.setLat((Double) dataSnapshot.child(Constants.VEHICLE_LATITUDE).getValue());
                    }
                    if (dataSnapshot.child(Constants.VEHICLE_LONGITUDE).getValue() != null) {
                        mFrontVehicle.setLon((Double) dataSnapshot.child(Constants.VEHICLE_LONGITUDE).getValue());
                    }
//                    Log.d(LOG_TAG, "Front Vehicle's  Id : " + mFrontVehicle.getId() +  " , lat : "  + mFrontVehicle.getLat() + " , lon : " + mFrontVehicle.getLon());
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
//                    Log.d(LOG_TAG, "Rear Vehicle's  Id : " + mRearVehicle.getId() +  " , lat : "  + mRearVehicle.getLat() + " , lon : " + mRearVehicle.getLon());
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
//        @Override
//        protected Map doInBackground(Double... doubles) {
//            double lat = doubles[0];
//            double lon = doubles[1];
////            // update GPS on current vehicle
//            Firebase currentVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
//            Map<String, Object> currentTripOn = new HashMap<String, Object>();
//            currentTripOn.put(Constants.VEHICLE_PASSENGER_SUM, MDCMainActivity.mPassengerCountSum);
//            currentTripOn.put(Constants.VEHICLE_FARE_SUM, MDCMainActivity.mFareCashSum);
//            currentTripOn.put(Constants.VEHICLE_LATITUDE, lat);
//            currentTripOn.put(Constants.VEHICLE_LONGITUDE, lon);
//            currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
//            currentVehicle.updateChildren(currentTripOn);
////
////            // get distance between current vehicle & fron/rear vehicle by calling Google Distance Matrix
//            Map<String, String[]> distances = new HashMap<String, String[]>();
//
//            String[] frontInfo = null;
//            String[] rearInfo = null;
//
//            // dummy for test
//            frontInfo = new String[]{MDCUtils.getRandomNumberInRange(90, 1500)+"", MDCUtils.getRandomNumberInRange(2, 10)+""};
//            rearInfo = new String[]{MDCUtils.getRandomNumberInRange(90, 1500)+"", MDCUtils.getRandomNumberInRange(2, 10)+""};
//
////            if (mFrontVehicle == null || mFrontVehicle.getLat() == 0.0 || mFrontVehicle.getLon() == 0.0) {
////                // no need to subscribe
////            } else {
////                frontInfo = MDCUtils.getDistanceInfo(lat, lon, mFrontVehicle.getLat(), mFrontVehicle.getLon());
////            }
////            if (mRearVehicle == null || mRearVehicle.getLat() == 0.0 || mRearVehicle.getLon() == 0.0) {
////                // no need to subscribe
////            } else {
////                rearInfo = MDCUtils.getDistanceInfo(lat, lon, mRearVehicle.getLat(), mRearVehicle.getLon());
////            }
//            distances.put(Constants.VEHICLE_FRONT, frontInfo);
//            distances.put(Constants.VEHICLE_REAR, rearInfo);
//            return distances;
//        }
        @Override
        protected Map doInBackground(Double... doubles) {

            double lat = doubles[0];
            double lon = doubles[1];
//
//            // update GPS on current vehicle
//            Firebase currentVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
//            Map<String, Object> currentTripOn = new HashMap<String, Object>();
//            currentTripOn.put(Constants.VEHICLE_LATITUDE, lat);
//            currentTripOn.put(Constants.VEHICLE_LONGITUDE, lon);
//            currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
//            currentVehicle.updateChildren(currentTripOn);

            Firebase currentVehicle = mFirebase.child(Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
            Map<String, Object> currentTripOn = new HashMap<String, Object>();
            currentTripOn.put(Constants.VEHICLE_PASSENGER_SUM, MDCMainActivity.mPassengerCountSum);
            currentTripOn.put(Constants.VEHICLE_FARE_SUM, MDCMainActivity.mFareCashSum);
            currentTripOn.put(Constants.VEHICLE_LATITUDE, lat);
            currentTripOn.put(Constants.VEHICLE_LONGITUDE, lon);
            currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
            currentVehicle.updateChildren(currentTripOn);

            // get distance between current vehicle & fron/rear vehicle by calling Google Distance Matrix
            Map<String, String[]> distances = new HashMap<String, String[]>();
            String[] frontInfo = new String[]{"0", "0"};
            String[] rearInfo = new String[]{"0", "0"};
            if (mFrontVehicle == null || mFrontVehicle.getLat() == 0.0 || mFrontVehicle.getLon() == 0.0) {
                // no need to subscribe
            } else {
                frontInfo = MDCUtils.getDistanceInfo(lat, lon, mFrontVehicle.getLat(), mFrontVehicle.getLon());
            }
            if (mRearVehicle == null || mRearVehicle.getLat() == 0.0 || mRearVehicle.getLon() == 0.0) {
                // no need to subscribe
            } else {
                rearInfo = MDCUtils.getDistanceInfo(lat, lon, mRearVehicle.getLat(), mRearVehicle.getLon());
            }
            distances.put(Constants.VEHICLE_FRONT, frontInfo);
            distances.put(Constants.VEHICLE_REAR, rearInfo);
            return distances;
        }

        @Override
        protected void onPostExecute(Map info) {// dummy data
            String[] frontInfo = (String[]) info.get(Constants.VEHICLE_FRONT);
            String[] rearInfo = (String[]) info.get(Constants.VEHICLE_REAR);
            int frontDistance = Integer.parseInt(frontInfo[0]);
            int rearDistance = Integer.parseInt(rearInfo[0]);


//            String front = "\tSV580003";
            String front = mFrontVehicle.getId();
            SpannableString frontS = new SpannableString(front);
            frontS.setSpan(new StyleSpan(Typeface.BOLD), 0, front.length(), 0);
            mFrontVehicleIdTxt.setText(frontS);

//            String rear = "\tSV580005";
            String rear = mRearVehicle.getId();
            SpannableString rearS = new SpannableString(rear);
            rearS.setSpan(new StyleSpan(Typeface.BOLD), 0, rear.length(), 0);
            mRearVehicleIdTxt.setText(rearS);

            if (frontDistance < Constants.DISTANCE_THRESHOLD_DANGER) {
                mFrontVehicleImg.setImageResource(R.drawable.bus_background_danger);
                mFrontVehicleTxt.setTextColor(Color.WHITE);
            } else if (frontDistance < Constants.DISTANCE_THRESHOLD_SAFE) {
                mFrontVehicleImg.setImageResource(R.drawable.bus_background_normal);
                mFrontVehicleTxt.setTextColor(Color.BLACK);
            } else {
                mFrontVehicleImg.setImageResource(R.drawable.bus_background_safe);
                mFrontVehicleTxt.setTextColor(Color.WHITE);
            }
            mFrontVehicleTxt.setText(getDrivingInfo(frontInfo));



            if (rearDistance < Constants.DISTANCE_THRESHOLD_DANGER) {
                mRearVehicleImg.setImageResource(R.drawable.bus_background_danger);
                mRearVehicleTxt.setTextColor(Color.WHITE);
            } else if (rearDistance < Constants.DISTANCE_THRESHOLD_SAFE) {
                mRearVehicleImg.setImageResource(R.drawable.bus_background_normal);
                mRearVehicleTxt.setTextColor(Color.BLACK);
            } else {
                mRearVehicleImg.setImageResource(R.drawable.bus_background_safe);
                mRearVehicleTxt.setTextColor(Color.WHITE);
            }
            mRearVehicleTxt.setText(getDrivingInfo(rearInfo));
        }

    }


    public SpannableStringBuilder getDrivingInfo(String[] info){

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString durationS = new SpannableString(info[1]);
        durationS.setSpan(new RelativeSizeSpan(1.3f), 0, info[1].length(), 0);
        durationS.setSpan(new StyleSpan(Typeface.BOLD), 0, info[1].length(), 0);
        spannableStringBuilder.append(durationS);
//        String time = " mins";
//        SpannableString timeS = new SpannableString(time);
//        timeS.setSpan(new RelativeSizeSpan(0.7f), 0, time.length(), 0);
//        spannableStringBuilder.append(timeS);
        spannableStringBuilder.append("\t\t");

        String speed = MDCUtils.getDistanceFormat(Integer.parseInt(info[0]));
        String meter = " m";
        if(Integer.parseInt(info[0]) > 1000){
            meter = " km";
        }

        SpannableString speedS = new SpannableString(speed);
        speedS.setSpan(new RelativeSizeSpan(1.3f), 0, speed.length(), 0);
        speedS.setSpan(new StyleSpan(Typeface.BOLD), 0, speed.length(), 0);
        SpannableString meterS = new SpannableString(meter);
//        meterS.setSpan(new RelativeSizeSpan(0.7f), 0, meterS.length(), 0);
        spannableStringBuilder.append(speedS);
        spannableStringBuilder.append(meterS);
        return spannableStringBuilder;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
