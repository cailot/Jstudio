package com.hyung.jin.seo.getup;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileConstants;
import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileUtils;

import java.util.Arrays;
import java.util.Calendar;

public class SettingActivity extends ActionBarActivity
{
    /***************************************************
        System Managers
     **************************************************/
    private AlarmManager alarmManager;

    /***************************************************
        Keep Preferences
     **************************************************/
    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    /***************************************************
        Config Items
     **************************************************/
    private int hour, minute;
    private String[] repeatDays;
    private String exerciseTime;
    private boolean isSoundOn, isVibrationOn;
    private String ringtone;

    /*******************************************************
        DisplayActivity will use this to terminate activity
     ********************************************************/

    public static Activity settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = SettingActivity.this;
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        // register system manager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        setUpConfig();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        registerPreferenceListener();
    }

    public static class SettingFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }

    /**
     * It executes whenever any change happens in Preferences
     */
    private void registerPreferenceListener() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(
                    SharedPreferences sharedPreferences, String key) {

                // reload changed config items
                setUpConfig();
                //  display updated values
                showSetting();
                //  set alarm based on updated value
                startAlarm();
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Load config items from Preference
     */
    private void setUpConfig() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isSoundOn = preferences.getBoolean(G3tUpMobileConstants.SOUND_SET, false);
        isVibrationOn = preferences.getBoolean(G3tUpMobileConstants.VIBRATION_SET, false);
        hour = preferences.getInt(G3tUpMobileConstants.ALARM_HOUR, 0);
        minute = preferences.getInt(G3tUpMobileConstants.ALARM_MINUTE, 0);
        exerciseTime = preferences.getString(G3tUpMobileConstants.EXERCISE_TIME, "");
        ringtone = preferences.getString(G3tUpMobileConstants.RINGTON_SET,"");
        repeatDays = preferences.getString(G3tUpMobileConstants.REPEAT_DAY, "").split(G3tUpMobileConstants.SEPARATOR);
    }

    /**
     * Print out config items
     */
    private void showSetting() {

        StringBuilder builder = new StringBuilder();
        builder.append("1. Timer : " + hour + " : " + minute);
        builder.append("\n2. Repeat : " + Arrays.toString(repeatDays));
        builder.append("\n3. Exercise Time : " + exerciseTime);
        builder.append("\n4. Sound : " + isSoundOn);
        builder.append("\n5. Vibration : " + isVibrationOn);
        builder.append("\n6. Ringtone : " + ringtone);
        Log.d(G3tUpMobileConstants.TAG_SETTING, builder.toString());
    }

    /**
     * 1. Start animation on screen
     * 2. Trigger alarm to G3tUpReceiver
     */
    private void startAlarm()
    {
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra(G3tUpMobileConstants.ALARM_STATE, G3tUpMobileConstants.ALARM_START);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), G3tUpMobileConstants.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = G3tUpMobileUtils.getAlarmTime(hour, minute);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*5, pendingIntent);
    }
}