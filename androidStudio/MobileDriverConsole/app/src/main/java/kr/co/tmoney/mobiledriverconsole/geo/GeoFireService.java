package kr.co.tmoney.mobiledriverconsole.geo;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.List;

import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 7. 5..
 */
public class GeoFireService extends IntentService {

    private static final String LOG_TAG = MDCUtils.getLogTag(GeoFireService.class);

    //public boolean isRun = true;

    GeoFire mGeoFire;

    String message;

    List<GeoLocation> lists = new ArrayList<GeoLocation>();

    public GeoFireService(){
        super(LOG_TAG);
        mGeoFire = new GeoFire(new Firebase(MDCConstants.FIREBASE_HOME));
        lists.add(new GeoLocation(-37.832375, 144.971998));
        lists.add(new GeoLocation(-37.818458, 144.967918));
        lists.add(new GeoLocation(-37.813471,144.9655192));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        message = intent.getStringExtra("fromMain");

        for(GeoLocation location : lists){
            GeoQuery geoQuery = mGeoFire.queryAtLocation(location, 0.3); // 300 m
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    String msg = message + " - "+ key + " entered at " + location.latitude + " , " + location.longitude;
                    Log.d(LOG_TAG, msg);
                    fireIntent(msg);
                }

                @Override
                public void onKeyExited(String key) {
                    String msg = message + " - " + key + " exit ";
                    Log.d(LOG_TAG, msg);
                    fireIntent(msg);
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(FirebaseError error) {

                }
            });
        }
    }

    private void fireIntent(String msg){
        Intent intent = new Intent();
//        intent.setAction(MDCMainActivity.GeoReceiver.MDC_RECEIVER);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(MDCConstants.GEOFIRE_INTENT_SERVICE, msg);
        sendBroadcast(intent);
    }
}
