package com.hyung.jin.seo.getup.mobile.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;


public class MultiSelectPreference extends ListPreference {

    private boolean[] mClickedDialogEntryIndices;

    public MultiSelectPreference(Context context)
    {
        super(context);
    }

    public MultiSelectPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    protected void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null
                || entries.length != entryValues.length) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        mClickedDialogEntryIndices = new boolean[entryValues.length] ;
        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, mClickedDialogEntryIndices,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which,
                                        boolean val) {
                        mClickedDialogEntryIndices[which] = val;
                    }
                });
    }

    public static String[] parseStoredValue(CharSequence val) {
        if ("".equals(val) || val == null )
            return null;
        else
            return ((String) val).split(G3tUpMobileConstants.SEPARATOR);
    }

    private void restoreCheckedEntries() {
        CharSequence[] entryValues = getEntryValues();
//        String[] vals = parseStoredValue(getValue());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String[] vals = parseStoredValue(preferences.getString(G3tUpMobileConstants.REPEAT_DAY, ""));
        if (vals != null) {
            for (int j = 0; j < vals.length; j++) {
                String val = vals[j].trim();
                for (int i = 0; i < entryValues.length; i++) {
                    CharSequence entry = entryValues[i];
                    if (entry.equals(val)) {
                        mClickedDialogEntryIndices[i] = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // super.onDialogClosed(positiveResult);

        CharSequence[] entryValues = getEntryValues();
        if (positiveResult && entryValues != null) {
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < entryValues.length; i++) {
                if (mClickedDialogEntryIndices[i]) {
                    value.append(entryValues[i]).append(G3tUpMobileConstants.SEPARATOR);
                }
            }

            if (callChangeListener(value)) {
                String val = value.toString();
                if (val.length() > 0)
                    val = val.substring(0, val.length() - G3tUpMobileConstants.SEPARATOR.length());
//                setValue(val);
                SharedPreferences.Editor editor = getEditor();
                editor.putString(G3tUpMobileConstants.REPEAT_DAY, val);
                editor.commit();

            }
        }
    }
}