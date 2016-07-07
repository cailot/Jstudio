package kr.co.tmoney.mobiledriverconsole.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        return Constants.PRE_LOG_ENTRY +  className.getSimpleName() + Constants.POST_LOG_ENTRY;
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
        String[] value = StringUtils.splitByWholeSeparator(list, Constants.STRING_ARRAY_SEPARATOR);
        return value!=null ? value : new String[]{};


    }

    /**
     * Generate concatenated String with ','
     * @param list
     * @return
     */
    public static String convertStringArrayToString(String[] list){
        String value = StringUtils.join(list, Constants.STRING_ARRAY_SEPARATOR);
        return value!=null ? value : "";
//        if(list==null || list.length==0){
//            return "";
//        }
//        StringBuffer stringBuffer = new StringBuffer();
//        for(String str : list){
//            stringBuffer.append(str + Constants.STRING_ARRAY_SEPARATOR);
//        }
//        String string = stringBuffer.toString();
//        string = StringUtils.substringBeforeLast(string, Constants.STRING_ARRAY_SEPARATOR);
//        return string;
    }


    /**
     * Randomise int number within range
     * @param min
     * @param max
     * @return
     */
    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    /**
     * Return node name under trips (ex> 20160707222443_SV580003 )
     * @param vehicle
     * @return
     */
    public static String getTipNode(String vehicle){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        return date + "_" + vehicle;
    }

}
