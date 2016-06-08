package com.hyung.jin.seo.getup.mobile.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.hyung.jin.seo.getup.R;

/**
 * Custom preference for time selection. Hour and minute are persistent and
 * stored separately as ints in the underlying shared preferences under keys
 * KEY.hour and KEY.minute, where KEY is the preference's key.
 */
public class TimePreference extends DialogPreference {

    /** The widget for picking a time */
    private TimePicker timePicker;

    /**
     * Creates a preference for choosing a time based on its XML declaration.
     *
     * @param context
     * @param attributes
     */
    public TimePreference(Context context, AttributeSet attributes) {
        super(context, attributes);
        setPersistent(false);
    }

    /**
     * Initialize time picker to currently stored time preferences.
     *
     * @param view
     * The dialog preference's host view
     */
    @Override
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        timePicker = (TimePicker) view.findViewById(R.id.prefTimePicker);
        timePicker.setCurrentHour(getSharedPreferences().getInt(G3tUpMobileConstants.ALARM_HOUR, 0));
        timePicker.setCurrentMinute(getSharedPreferences().getInt(G3tUpMobileConstants.ALARM_MINUTE, 0));
        timePicker.setIs24HourView(DateFormat.is24HourFormat(timePicker.getContext()));
    }

    /**
     * Handles closing of dialog. If user intended to save the settings, selected
     * hour and minute are stored in the preferences with keys ALARM_HOUR and
     * ALARM_MINUTE.
     *
     * @param okToSave
     * True if user wanted to save settings, false otherwise
     */
    @Override
    protected void onDialogClosed(boolean okToSave) {
        super.onDialogClosed(okToSave);
        if (okToSave) {
            timePicker.clearFocus();
            SharedPreferences.Editor editor = getEditor();
//            boolean c = timePicker.is24HourView();
            editor.putInt(G3tUpMobileConstants.ALARM_HOUR, timePicker.getCurrentHour());
            editor.putInt(G3tUpMobileConstants.ALARM_MINUTE, timePicker.getCurrentMinute());
            editor.commit();
        }
    }
}