package kr.co.tmoney.mobiledriverconsole.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class MDCUtils {

    private MDCUtils(){}

    public static String getValueFromSharedPreferences(Context context, String key){
        String value = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = preferences.getString(key, "");
        return value;
    }

    /**
     * Generate LOG_TAG per each class
     * @param className
     * @return
     */
    public static String getLogTag(Class className){
        return MDCConstants.PRE_LOG_ENTRY +  className.getSimpleName() + MDCConstants.POST_LOG_ENTRY;
    }

    /**
     * Convert Object[] into String[]
     * @param from
     * @return
     */
    public static String[] convertListToStringArray(List from){
        // avoid NPE
        if(from==null || from.size()==0){
            return new String[]{};
        }
        String[] converts = new String[from.size()];
        int index = 0;
        for(Object obj : from){
            converts[index] = obj.toString();
            index++;
        }
        return converts;
    }

    /**
     * Generate String[] by ' , '
     * @param list
     * @return
     */
    public static String[] convertStringToStringArray(String list){
        String[] value = StringUtils.splitByWholeSeparator(list, MDCConstants.STRING_ARRAY_SEPARATOR);
        return value!=null ? value : new String[]{};


    }

    /**
     * Generate concatenated String with ','
     * @param list
     * @return
     */
    public static String convertStringArrayToString(String[] list){
        String value = StringUtils.join(list, MDCConstants.STRING_ARRAY_SEPARATOR);
        return value!=null ? value : "";
//        if(list==null || list.length==0){
//            return "";
//        }
//        StringBuffer stringBuffer = new StringBuffer();
//        for(String str : list){
//            stringBuffer.append(str + MDCConstants.STRING_ARRAY_SEPARATOR);
//        }
//        String string = stringBuffer.toString();
//        string = StringUtils.substringBeforeLast(string, MDCConstants.STRING_ARRAY_SEPARATOR);
//        return string;
    }
}
