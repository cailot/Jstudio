package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.LocaleHelper;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class SettingFragment extends Fragment{

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mHandOverTxt, mLogOutTxt, mUserInfoTxt, mLanguageDescTxt;

    private RadioButton mThaiBtn, mEnglishBtn;

    private ImageView mDriverImg;

    private Context mContext;

    private String mRouteId;

    private String mVehicleId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_activity, null);
        mContext = container.getContext();

        mRouteId = getValue(Constants.ROUTE_ID, getString(R.string.no_route_found));
        mVehicleId = getValue(Constants.VEHICLE_NAME, getString(R.string.no_vehicle_found));

        initialiseUI(view);

        return view;
    }

    /**
     * Build up UI components
     * @param view
     */
    private void initialiseUI(View view) {
        mHandOverTxt = (TextView) view.findViewById(R.id.setting_hand_over_txt);
        mLogOutTxt = (TextView) view.findViewById(R.id.setting_log_out_txt);
        mUserInfoTxt = (TextView) view.findViewById(R.id.setting_user_info);
        mLanguageDescTxt = (TextView) view.findViewById(R.id.setting_language_desc);
        mThaiBtn = (RadioButton) view.findViewById(R.id.setting_thai_btn);
        mEnglishBtn = (RadioButton) view.findViewById(R.id.setting_english_btn);
        mHandOverTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingEvents(view);
            }
        });
        mLogOutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingEvents(view);
            }
        });
        mThaiBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingEvents(view);
            }
        });
        mEnglishBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingEvents(view);
            }
        });
        mDriverImg = (ImageView) view.findViewById(R.id.setting_driver_img);
    }


    /**
     * Event handler method
     * @param view
     */
    private void settingEvents(View view){
        switch(view.getId()){
            case R.id.setting_hand_over_txt :
                logger.debug("Hand Over Event");
                setTripOff();
                break;
            case R.id.setting_log_out_txt :
                logger.debug("Log Out Event");
                break;
            case R.id.setting_thai_btn :
                logger.debug("Thai Language Event");
                switchLanguage(Constants.LANGUAGE_KOREAN);
                break;
            case R.id.setting_english_btn :
                switchLanguage(Constants.LANGUAGE_ENGLISH);
                logger.debug("English Language Event");
                break;
        }
    }

    /**
     * Trip Off events
     * 1. update vechicle info  such as changing to false on tripOn
     * 2. remove vehicle info under routes/{routeId}/vehicles/{vehicleId}
     * 3. Update tripOn to false
     * 4. show Toast message
     * 5. disable all component except Log Out
     */
    private void setTripOff() {
        // update Vehicle info under 'vehicles'
        Firebase currentVehicle = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_VEHICLE_LIST_PATH + "/" + mVehicleId);
        Map<String, Object> currentTripOn = new HashMap<String, Object>();
        currentTripOn.put(Constants.VEHICLE_TRIP_ON, false);
        currentTripOn.put(Constants.VEHICLE_UPDATED, ServerValue.TIMESTAMP);
        currentVehicle.updateChildren(currentTripOn);

        // delete car under routes/554/vehicles
        Firebase routeVehicle = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_ROUTE_LIST_PATH + "/" + mRouteId + "/vehicles/" + mVehicleId);
        routeVehicle.removeValue();

        // turn off 'Trip On'
        put(Constants.VEHICLE_TRIP_ON, false);

        // notify user
        Toast.makeText(mContext, "Trip Off Completed", Toast.LENGTH_SHORT).show();

        // disable components except Log Out

    }


    /**
     * switch language between Thai & Eng
     * @param lang
     */
    private void switchLanguage(String lang) {
       if(Constants.LANGUAGE_KOREAN.equalsIgnoreCase(lang)) {
           LocaleHelper.setLocale(mContext.getApplicationContext(), Constants.LANGUAGE_KOREAN);
       }else{
           LocaleHelper.setLocale(mContext.getApplicationContext(), Constants.LANGUAGE_ENGLISH);
       }
        put(Constants.SELECTED_LANGUAGE, lang);
        Log.e(LOG_TAG, lang + " is now saved");
        applyLanguageSetting();
    }


    /**
     * apply new language setting on screen
     */
    private void applyLanguageSetting(){
        mThaiBtn.setText(R.string.setting_thai_language);
        mEnglishBtn.setText(R.string.setting_english_language);
        mHandOverTxt.setText(R.string.setting_hand_over);
        mLogOutTxt.setText(R.string.setting_log_out);
        mUserInfoTxt.setText(R.string.setting_user_info);
        mLanguageDescTxt.setText(R.string.setting_language_title);
    }


    private void disableComponents(){
        mThaiBtn.setEnabled(false);
        mThaiBtn.setTextColor(Color.BLACK);
        mEnglishBtn.setEnabled(false);
        mEnglishBtn.setTextColor(Color.BLACK);
        mHandOverTxt.setEnabled(false);
        mHandOverTxt.setTextColor(Color.BLACK);
        mUserInfoTxt.setTextColor(Color.BLACK);
        mLanguageDescTxt.setTextColor(Color.BLACK);
        mDriverImg.setImageResource(R.drawable.driver_off);
    }

    public void put(String key, String value){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}