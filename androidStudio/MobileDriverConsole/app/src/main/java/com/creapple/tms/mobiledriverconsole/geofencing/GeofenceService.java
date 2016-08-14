package com.creapple.tms.mobiledriverconsole.geofencing;

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

import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 7. 1..
 */
public class GeofenceService extends IntentService {


    private static final String LOG_TAG = MDCUtils.getLogTag(GeofenceService.class);

    private String mEventStop = "";

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
            Log.e(LOG_TAG, "Unknown error happens");
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
        } else {
            Log.e(LOG_TAG, "Wrong event passed");
        }

        Intent localIntent = new Intent(Constants.BROADCAST_SERVICE)
                // add status into the Intent
                .putExtra(Constants.GEOFENCE_INTENT_ACTION, geofenceTransition)
                .putExtra(Constants.GEOFENCE_INTENT_MESSAGE, geofenceTransitionDetails)
                .putExtra(Constants.GEOFENCE_INTENT_STOP, mEventStop);

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
            mEventStop = geofence.getRequestId();
            triggeringGeofencesIdsList.add(mEventStop);
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
