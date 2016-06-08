package com.hyung.jin.seo.getup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileConstants;
import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * This class ensures alarm start after device reboots
 */
public class AutoStartReceiver extends BroadcastReceiver {
    /***************************************************
     * System Managers
     **************************************************/
    private AlarmManager alarmManager;

    /***************************************************
     * Config Items
     **************************************************/
    private int hour, minute;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(G3tUpMobileConstants.TAG, "DisplayReceiver tosses ~~~~~~~~~~~~~~~~~~~~    " + new Date());
        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(G3tUpMobileConstants.TAG, action);
//            // set the alarm manager
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // load config items
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            hour = preferences.getInt(G3tUpMobileConstants.ALARM_HOUR, 0);
            minute = preferences.getInt(G3tUpMobileConstants.ALARM_MINUTE, 0);

            Intent i = new Intent(context, DisplayActivity.class);
            i.putExtra(G3tUpMobileConstants.ALARM_STATE, G3tUpMobileConstants.ALARM_START);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, G3tUpMobileConstants.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar cal = G3tUpMobileUtils.getAlarmTime(hour, minute);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*2, pendingIntent);
            Log.d(G3tUpMobileConstants.TAG, "Autostart At Hour - " + hour + "\t Min - " + minute);
        }
    }
}
