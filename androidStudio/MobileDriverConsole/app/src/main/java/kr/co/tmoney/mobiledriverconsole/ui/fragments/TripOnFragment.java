package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.geofencing.MDCGeofenceService;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCErrorMessage;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOnFragment extends TripFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOnFragment.class);

    private MapView mMapView;

    private GoogleMap mGoogleMap;

    MarkerOptions mMarker;

    TextView tx;


    LocationRequest mLocationRequest;

    /**
     * Geofencing Data
     */
    protected ArrayList<Geofence> mGeofences;
    protected GoogleApiClient mGoogleApiClient;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.trip_on_activity, null);
        mContext = container.getContext();




        tx = (TextView)view.findViewById(R.id.tripOnTxt);
        tx.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "##########");
                registerGeofences();
            }
        });





        mMapView=(MapView)view.findViewById(R.id.tripMap);
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
        LatLng melbourne = new LatLng(-37.8339319,144.9714436);
//        mGoogleMap.addMarker(new MarkerOptions().position(melbourne));
        mMarker = new MarkerOptions();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 15));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "GoogleApiClient onConnected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000*5);

        // Android SDK 23
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
            Log.d(LOG_TAG, "Permission check needs later....");

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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
        if(mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
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
        String msg = "Lat : " + location.getLatitude()+"\t, Lon : " + location.getLongitude();
        tx.setText("GPS subscription test  \t\t" + msg);
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mMarker.position(current)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker));
        mGoogleMap.addMarker(mMarker);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, MDCConstants.GOOGLE_MAP_ZOOM_LEVEL));
        Log.d(LOG_TAG, msg);
    }

    private PendingIntent getGeofencePendingIntent(){
        Intent intent = new Intent(getActivity(), MDCGeofenceService.class);
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

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

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
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

}
