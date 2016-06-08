package com.hyung.jin.seo.getup.mobile.utils;


import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;

/**
 * Custom preference for rington selection.
 */
public class SoundPreference extends RingtonePreference {

//    Context context;
    /**
     * Creates a preference for choosing a time based on its XML declaration.
     *
     * @param context
     * @param attributes
     */
    public SoundPreference(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public SoundPreference(Context context) {
        super(context);
    }

    @Override
    protected void onSaveRingtone(Uri ringtoneUri) {
        super.onSaveRingtone(ringtoneUri);
        String name = null;
        if(ringtoneUri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringtoneUri);
            name = ringtone.getTitle(getContext());
            setSummary(name);
        }
//        Log.d(G3tUpMobileConstants.TAG, "Rington - " + name);
    }

}