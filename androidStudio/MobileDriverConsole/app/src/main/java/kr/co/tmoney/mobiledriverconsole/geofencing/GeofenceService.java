package kr.co.tmoney.mobiledriverconsole.geofencing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
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
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        if(geofencingEvent.hasError()){
//            String errorMessage = ErrorMessage.getErrorMessage(this, geofencingEvent.getErrorCode());
//            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
//            Log.e(LOG_TAG, errorMessage);
//            return;
//        }
//        // check Transition type
//        int transition = geofencingEvent.getGeofenceTransition();
//        String details = "";
//        if(transition== Geofence.GEOFENCE_TRANSITION_ENTER || transition==Geofence.GEOFENCE_TRANSITION_EXIT){
//            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
//            details = getGeofenceTransitionDetails(this, transition, geofences);
//            Toast.makeText(getApplicationContext(), details, Toast.LENGTH_LONG).show();
//            //sendNotification(details);
//            Log.d(LOG_TAG, details);
//        }else{
//            Toast.makeText(getApplicationContext(), "Error happen !!", Toast.LENGTH_LONG).show();
//
//            Log.e(LOG_TAG, getString(R.string.geofence_transition_invalid_type, transition));
//        }

        String details = "";
        try {
            Thread.sleep(10*1000);
            details = "10 sec later.....";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent backIntent = new Intent(Constants.BROADCAST_SERVICE);
        backIntent.putExtra(Constants.GEO_UPDATE_INTENT_SERVICE, details);
        // broadcast the intent to receiver
        LocalBroadcastManager.getInstance(this).sendBroadcast(backIntent);

        Log.e(LOG_TAG, "GeofenceService done");

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

    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MDCMainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this
        );

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MDCMainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}
