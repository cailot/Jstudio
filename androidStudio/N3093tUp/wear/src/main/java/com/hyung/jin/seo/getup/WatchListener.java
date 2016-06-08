package com.hyung.jin.seo.getup;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.hyung.jin.seo.getup.wear.utils.G3tUpWearableConstants;

/**
 * This class catches the start signal from connected Mobile and start WatchActivity
 */
public class WatchListener extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(messageEvent.getPath().equalsIgnoreCase(G3tUpWearableConstants.COMMUNICAITION_PATH_FROM_MOBILE))
        {
            final String message = new String(messageEvent.getData());
            Intent intent = new Intent(this, WatchActivity.class);
            intent.putExtra(G3tUpWearableConstants.FROM_MOBILE_DATA, message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Log.d(G3tUpWearableConstants.TAG_WATCH, "WatchActivity is starting...");
        }else {
            super.onMessageReceived(messageEvent);
        }
    }
}