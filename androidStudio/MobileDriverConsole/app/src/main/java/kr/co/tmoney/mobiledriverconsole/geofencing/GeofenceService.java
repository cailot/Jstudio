package kr.co.tmoney.mobiledriverconsole.geofencing;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 7. 1..
 */
public class GeofenceService extends IntentService {


    private static final String LOG_TAG = MDCUtils.getLogTag(GeofenceService.class);

    public GeofenceService(String name) {
        super(name);
    }

    public GeofenceService(){
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(LOG_TAG, "알수 없는 에러가 발생하였습니다.");
            return;
        }

        String geofenceTransitionDetails = "";
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            Log.i(LOG_TAG, geofenceTransitionDetails);
        } else { // 위에 언급된 상수이외 다른 상수는 제공하지 않는다.
            Log.e(LOG_TAG, "잘못된 메시지입니다.");
        }

        Intent localIntent = new Intent(Constants.BROADCAST_SERVICE)
                // add status into the Intent
                .putExtra(Constants.GEOFENCE_INTENT_ACTION, geofenceTransition)
                .putExtra(Constants.GEOFENCE_INTENT_MESSAGE, geofenceTransitionDetails);

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);


    }

    @Override
    public void onDestroy() {
        Log.e(LOG_TAG, "GeofenceService onDestory()");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LOG_TAG, "GeofenceService onStartCommand()");

        return super.onStartCommand(intent, flags, startId);
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
