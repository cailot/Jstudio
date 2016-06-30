package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.Manifest;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOffFragment extends TripFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOffFragment.class);

    TextView txtOutput;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;


    private MapView mMapView;

    private GoogleMap mGoogleMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_off_activity, null);
        mContext = container.getContext();


        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        txtOutput = (TextView) view.findViewById(R.id.tripOffTxt);

        mMapView=(MapView)view.findViewById(R.id.testMap);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);



        return view;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        // Android SDK 23
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
            Log.d(LOG_TAG, "Permission check needs later....");

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged");
        txtOutput.setText("Lat : " + location.getLatitude()+", Lon : " + location.getLongitude());

        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(current));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();

    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        initialiseMap();

    }

    private void initialiseMap() {
        LatLng melbourne = new LatLng(-37.8339319,144.9714436);
       // mGoogleMap.addMarker(new MarkerOptions().position(melbourne));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 15));
    }
}
