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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import kr.co.tmoney.mobiledriverconsole.geofencing.MDCGeofenceService;
import kr.co.tmoney.mobiledriverconsole.model.vo.VehicleVO;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCErrorMessage;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOnFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

// implements OnMapReadyCallback, LocationListener, ResultCallback<Status> {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOnFragment.class);

    Context mContext;

    private MapView mMapView;

    private GoogleMap mGoogleMap;

    private Marker mMarker;

    private MarkerOptions mMarkerOptions;

    TextView mTripOnTxt;


    // From MDCMainActivity

    protected GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    /**
     * Geofencing Data
     */
    protected ArrayList<Geofence> mGeofences;

    private String mVehicleId;

    private VehicleVO mVehicle;



    boolean isGeofenceOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_on_activity, null);
        mContext = container.getContext();

        mVehicleId = getValue(MDCConstants.VEHICLE_NAME, "No available vehicle");
        // get VehicleInfo from Firebase

        mTripOnTxt = (TextView) view.findViewById(R.id.trip_on_txt);
        mTripOnTxt.setText(mVehicleId);
        mTripOnTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "##########");
                registerGeofences();
            }
        });

//        MDCMainActivity mainActivity = (MDCMainActivity) getActivity();
//        mGoogleApiClient = mainActivity.getGoogleApiClient();
//        mLocationRequest = mainActivity.getLocationRequest();
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Log.d(LOG_TAG, "Permission check needs later....");
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


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
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(melbourne, MDCConstants.GOOGLE_MAP_ZOOM_LEVEL));
        // temporary showing geofences
        showGeofences();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "GoogleApiClient onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(MDCConstants.GOOGLE_MAP_POLLING_INTERVAL);

        // Android SDK 23
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
            Log.d(LOG_TAG, "Permission check needs later....");

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        // activate Geofences
        activateGeofences();

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
//        if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
//            mGoogleApiClient.disconnect();
//        }
    }

    @Override
    public void onDestroy() {
        if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(LOG_TAG, "Geofences Added");
            Toast.makeText(getActivity(), "Geofences Added", Toast.LENGTH_LONG).show();
        } else {
            String errorMsg = MDCErrorMessage.getErrorMessage(getActivity(), status.getStatusCode());
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, errorMsg);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(mMarker!=null){
            mMarker.remove();
        }
        String msg = mVehicleId + "\n" + "Lat : " + location.getLatitude()+"\nLon : " + location.getLongitude();
        mTripOnTxt.setText(msg);
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mMarkerOptions.position(current)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker));

        mMarker = mGoogleMap.addMarker(mMarkerOptions);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, MDCConstants.GOOGLE_MAP_ZOOM_LEVEL));
        Log.d(LOG_TAG, msg);
    }

    private PendingIntent getGeofencePendingIntent(){
        Intent intent = new Intent(getActivity(), MDCGeofenceService.class);
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofences);
        return builder.build();
    }

    private void registerGeofences() {
        if(mGoogleApiClient==null || !mGoogleApiClient.isConnected()){
            Log.e(LOG_TAG, getString(R.string.not_connected));
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_LONG).show();
            return;
        }
        try{
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        }catch (SecurityException e){
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }






    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(MDCConstants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

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
                .setCircularRegion(-37.8210934,144.9686004, MDCConstants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(MDCConstants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Bourke Street")
                .setCircularRegion(-37.813471,144.9655192, MDCConstants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(MDCConstants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Around DHHS")
                .setCircularRegion(-37.8098068,144.9656684, MDCConstants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(MDCConstants.GEOFENCE_EXPIRATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );
        mGeofences.add(new Geofence.Builder()
                .setRequestId("Domain Interchage")
                .setCircularRegion(-37.8339319,144.9714436, MDCConstants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(MDCConstants.GEOFENCE_EXPIRATION)
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

    public void activateGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getContext(), "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

}
