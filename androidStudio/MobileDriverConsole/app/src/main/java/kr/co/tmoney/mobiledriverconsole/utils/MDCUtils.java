package kr.co.tmoney.mobiledriverconsole.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class MDCUtils {

    private static DecimalFormat mDecimalFormat = new DecimalFormat("##.00");

    private MDCUtils(){}


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


    /**
     * // http://www.movable-type.co.uk/scripts/latlong.html
     * // Under Creative Commons License http://creativecommons.org/licenses/by/3.0/
     * Calculate distance between two GPS co-ordinate
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double getDistanceMeters(double lat1, double lon1, double lat2, double lon2){
        int r = 6371;
        double dLat = Math.abs(lat2-lat1) * (Math.PI/180);
        double dLon = Math.abs(lon2-lon1) * (Math.PI/180);
        double a = (Math.sin(dLat/2) * Math.sin(dLat/2)) +
                (Math.cos(lat1 * (Math.PI/180)) * Math.cos(lat2 * (Math.PI/180)) * Math.sin(dLon/2) * Math.sin(dLon/2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = r * c * 1000; // show as meters

        return Double.parseDouble(mDecimalFormat.format(d));
    }


    /**
     * Get the index of closest distance
     * @param gps
     * @return
     */
    public static int getMinDistanceIndex(double[] gps){
        int index = 0;
        double min = gps[0];
        for(int i=1; i<gps.length; i++){
            if(gps[i]<min){
                min = gps[i];
                index = i;
            }
        }
        return index;
    }


    /**
     * Get distance information between two GPS
     * @param originLat
     * @param originLon
     * @param destLat
     * @param destLon
     * @return
     */
    public static String[] getDistanceInfo(double originLat, double originLon, double destLat, double destLon) {

        String[] infos = new String[]{"0","0"};

        String address = Constants.GOOGLE_DISTANCE_MATRIX_ADDRESS;
        address += originLat + "," + originLon;
        address += "&destinations=";
        address += destLat + "," + destLon;
        address += "&mode=driving&units=metric&language=en&key=";
        address += Constants.GOOGLE_DISTANCE_MATRIX_API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        Response response = null;
        String dist = null;
        try {
            response = client.newCall(request).execute();
            dist = response.body().string();
        } catch (IOException e) {
            return infos;
        }

        Log.d("@@@@@@", dist);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(dist);
        } catch (ParseException e) {
            return infos;
        }

        // status check as well

        JSONArray rows = (JSONArray) jsonObject.get(Constants.GOOGLE_DISTANCE_MATRIX_ROWS);
        for (int i = 0; i < rows.size(); i++) {
            JSONObject obj = (JSONObject) rows.get(i);
            JSONArray elements = (JSONArray) obj.get(Constants.GOOGLE_DISTANCE_MATRIX_ELEMENTS);
            for (int j = 0; j < elements.size(); j++) {
                JSONObject datas = (JSONObject) elements.get(j);
                JSONObject distance = (JSONObject) datas.get(Constants.GOOGLE_DISTANCE_MATRIX_DISTANCE);
                JSONObject duration = (JSONObject) datas.get(Constants.GOOGLE_DISTANCE_MATRIX_DURATION);
                infos[0] = distance.get(Constants.GOOGLE_DISTANCE_MATRIX_VALUE)+"";
                infos[1] = duration.get(Constants.GOOGLE_DISTANCE_MATRIX_TEXT)+"";

            }

        }
        String status = jsonObject.get(Constants.GOOGLE_DISTANCE_MATRIX_STATUS).toString();
//        Log.d("@@@@@@", status);
        if (!StringUtils.equalsIgnoreCase(Constants.GOOGLE_DISTANCE_MATRIX_OK, status)) {
            return  infos;
        }
        return infos;
    }


    /**
     * Convert into proper format of distance such as 1.2 km or 234 m
     * @param digit
     * @return
     */
    public static String getDistanceFormat(int digit){
        String info = "";
        if(digit > 1000){
            double d = digit/1000.0;
            info = String.format( "%.1f", d) ;
        }else{
            info = Integer.toString(digit);
        }
        return info;
    }


    /**
     * Bring up Stop groups per interval as String array
     * @param data
     * @return
     */
    public static String[] getStopGroups(String data){
        String[] stops = null;
        if(data!=null && data.length()>0){
            String start = StringUtils.stripStart(data, "[[");
            String end = StringUtils.stripEnd(start, "]]");
            stops = StringUtils.splitByWholeSeparator(end, "],[");
        }
        return stops;
    }


    /**
     * Add String information into SharedPreferences
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    /**
     * Add boolean information into SharedPreferences
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * Add Object information into SharedPreferences
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String json = new Gson().toJson(value);
        editor.putString(key, json);
        editor.commit();
    }


    /**
     * Retrieve String information into SharedPreferences
     * @param context
     * @param key
     * @param dftValue
     * @return
     */
    public static String getValue(Context context, String key, String dftValue) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }


    /**
     * Retrieve boolean information into SharedPreferences
     * @param context
     * @param key
     * @param dftValue
     * @return
     */
    public static boolean getValue(Context context, String key, boolean dftValue) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}
