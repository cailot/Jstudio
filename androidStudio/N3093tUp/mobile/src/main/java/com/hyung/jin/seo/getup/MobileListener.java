package com.hyung.jin.seo.getup;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileConstants;

/**
 * This class catches the stop signal from connected Wearable and trigger stopAlarm() in DisplayActivity
 */
public class MobileListener extends WearableListenerService{
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if(messageEvent.getPath().equalsIgnoreCase(G3tUpMobileConstants.COMMUNICAITION_PATH_FROM_WEAR))
        {
            final String message = new String(messageEvent.getData());
            Log.d(G3tUpMobileConstants.TAG_LISTENER, "Message comes from Watch  : " + message);
            if((DisplayActivity.instance != null) && (G3tUpMobileConstants.ALARM_STOP.equalsIgnoreCase(message)))
            {
                DisplayActivity stopIt = DisplayActivity.instance;
                stopIt.stopAlarm();
                Log.d(G3tUpMobileConstants.TAG_LISTENER, "Stop command from MobileListenr");
            }else{
                Log.d(G3tUpMobileConstants.TAG_LISTENER, "DisplayActivity reference is null");
            }
        }else {
            super.onMessageReceived(messageEvent);
        }
    }
}
