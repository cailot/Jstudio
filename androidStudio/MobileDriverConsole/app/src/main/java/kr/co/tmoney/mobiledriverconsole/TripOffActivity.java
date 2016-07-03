package kr.co.tmoney.mobiledriverconsole;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import kr.co.tmoney.mobiledriverconsole.ui.dialog.RouteDialog;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOffActivity extends AppCompatActivity implements RouteDialog.PassValueFromDialogListener {

    private static final String LOG_TAG = MDCUtils.getLogTag(TripOffActivity.class);

    private TextView mRouteTxt, mVehicleTxt;
    private Button mLogoutBtn, mTripOnBtn;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_off_activity);

        initialiseUI();

        // setup Firebase on Android
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(MDCConstants.FIREBASE_HOME);


    }

    private void initialiseUI() {
        mRouteTxt = (TextView) findViewById(R.id.trip_off_route_txt);
        mRouteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mVehicleTxt = (TextView) findViewById(R.id.trip_off_vehicle_txt);
        mVehicleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mLogoutBtn = (Button) findViewById(R.id.trip_off_logout_btn);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });
        mTripOnBtn = (Button) findViewById(R.id.trip_off_tripon_btn);
        mTripOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripOffEvents(view);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    RouterVO routerVO = snapshot.getValue(RouterVO.class);
//                    String name = routerVO.getRouteId();
//                    Log.d(LOG_TAG, name);
//                }
                Log.d(LOG_TAG, dataSnapshot.getValue()+"");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(LOG_TAG, firebaseError.getMessage());
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void tripOffEvents(View view){
        switch(view.getId()){
            case R.id.trip_off_route_txt :
                Log.d(LOG_TAG, "Route Event");
                showRouteDialog();
                break;
            case R.id.trip_off_vehicle_txt :
                Log.d(LOG_TAG, "Vehicle Event");
                break;
            case R.id.trip_off_logout_btn :
                Log.d(LOG_TAG, "Logout Event");
                break;
            case R.id.trip_off_tripon_btn :
                Log.d(LOG_TAG, "TripOn Event");
                turnOnTripOn();
                break;
        }
    }

    private void turnOnTripOn() {
        Intent i = new Intent(getApplicationContext(), MDCMainActivity.class);
        startActivity(i);
       // setContentView(R.layout.avtivity_next);
    }

    private void logout(){
        Firebase ref = new Firebase(MDCConstants.FIREBASE_HOME);
        ref.child("testKey").setValue("myValue");
    }

    @Override
    public void sendRouteName(String route) {
        // update route name according to user's choice
        mRouteTxt.setText(route);
    }

    private void showRouteDialog() {

        RouteDialog routeDialog = new RouteDialog();
        // link itself to be updated via 'PassValueFromDialogListener.sendStopName()'
        routeDialog.setPassValueFromDialogListener(TripOffActivity.this);
        routeDialog.show(getFragmentManager(), MDCConstants.ROUTE_DIALOG_TAG);
    }
}
