package kr.co.tmoney.mobiledriverconsole.utils;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

import kr.co.tmoney.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 7. 1..
 */
public class GeoFencingErrorMessage {
    /**
     * Will use only static methods
     */
    private GeoFencingErrorMessage(){}

    public static String getErrorMessage(Context context, int error){
        Resources resources = context.getResources();
        switch (error){
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE :
                return resources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return resources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS :
                return resources.getString(R.string.geofence_too_many_pending_intents);
            default:
                return resources.getString(R.string.unknown_geofence_error);
        }
    }

}
