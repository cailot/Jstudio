package kr.co.tmoney.mobiledriverconsole.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class MDCUtils {


    public static String getValueFromSharedPreferences(Context context, String key){
        String value = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = preferences.getString(key, "");
        return value;
    }


}
