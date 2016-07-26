package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.content.Context;
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

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

//import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.ui.dialog.LogOutDialog;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.LocaleHelper;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class SettingFragment extends Fragment{

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

//    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mHandOverTxt, mLogOutTxt, mUserInfoTxt, mLanguageDescTxt, mEmailTxt;

    private RadioButton mThaiBtn, mEnglishBtn;

    private ImageView mDriverImg;

    private Context mContext;

    private String mRouteId;

    private String mVehicleId;

    private MDCMainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_activity, null);
        mContext = container.getContext();

        mActivity = (MDCMainActivity) getActivity();

        mRouteId = MDCUtils.getValue(mContext, Constants.ROUTE_ID, getString(R.string.no_route_found));
        mVehicleId = MDCUtils.getValue(mContext, Constants.VEHICLE_NAME, getString(R.string.no_vehicle_found));

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
        mLogOutTxt.setEnabled(false);
        mUserInfoTxt = (TextView) view.findViewById(R.id.setting_user_info);
        String email = MDCUtils.getValue(mContext, Constants.USER_EMAIL, "");
        if(!email.equalsIgnoreCase("")) {
            ((TextView) view.findViewById(R.id.setting_email)).setText(email);
        }
        mLanguageDescTxt = (TextView) view.findViewById(R.id.setting_language_desc);



        mThaiBtn = (RadioButton) view.findViewById(R.id.setting_thai_btn);
        mEnglishBtn = (RadioButton) view.findViewById(R.id.setting_english_btn);


        String lang = MDCUtils.getValue(mContext, Constants.SELECTED_LANGUAGE, "");
        if(lang.equalsIgnoreCase(Constants.LANGUAGE_ENGLISH)){
            mEnglishBtn.setChecked(true);
        }else if(lang.equalsIgnoreCase(Constants.LANGUAGE_THAILAND)){
            mThaiBtn.setChecked(true);
        }else{
            mEnglishBtn.setChecked(true);
        }


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
        mEmailTxt = (TextView) view.findViewById(R.id.setting_email);
    }


    /**
     * Event handler method
     * @param view
     */
    private void settingEvents(View view){
        switch(view.getId()){
            case R.id.setting_hand_over_txt :
                setTripOff();
                break;
            case R.id.setting_log_out_txt :
                logOut();
                break;
            case R.id.setting_thai_btn :
                switchLanguage(Constants.LANGUAGE_THAILAND);
                break;
            case R.id.setting_english_btn :
                switchLanguage(Constants.LANGUAGE_ENGLISH);
                break;
        }
    }

    private void logOut() {
        Log.d(LOG_TAG, "logOut()");
        LogOutDialog logOutDialog = new LogOutDialog(mActivity);
        logOutDialog.show(mActivity.getFragmentManager(), Constants.LOGOUT_DIALOG_TAG);
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
        MDCUtils.put(mContext, Constants.VEHICLE_TRIP_ON, false);

        // notify user
//        Toast.makeText(mContext, "Trip Off Completed", Toast.LENGTH_SHORT).show();

        // disable components except Log Out
        disableComponents();

        // disable Tab movement
        mActivity.disableTabs();

        // close resources
        mActivity.closeResources();

        // enable logout button
        mLogOutTxt.setEnabled(true);
        mLogOutTxt.setTextColor(Color.WHITE);

    }


    /**
     * switch language between Thai & Eng
     * @param lang
     */
    private void switchLanguage(String lang) {
       if(Constants.LANGUAGE_THAILAND.equalsIgnoreCase(lang)) {
           LocaleHelper.setLocale(mContext.getApplicationContext(), Constants.LANGUAGE_THAILAND);
       }else{
           LocaleHelper.setLocale(mContext.getApplicationContext(), Constants.LANGUAGE_ENGLISH);
       }
        MDCUtils.put(mContext, Constants.SELECTED_LANGUAGE, lang);
        Log.d(LOG_TAG, lang + " is now saved");
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
        // change Tab names
        mActivity.updateTabNames();
    }


    /**
     * Disable all components except Log Out
     */
    private void disableComponents(){
        mThaiBtn.setEnabled(false);
        mThaiBtn.setTextColor(Color.BLACK);
        mEnglishBtn.setEnabled(false);
        mEnglishBtn.setTextColor(Color.BLACK);
        mHandOverTxt.setEnabled(false);
        mHandOverTxt.setTextColor(Color.BLACK);
        mHandOverTxt.setBackgroundResource(R.drawable.bg_grey_rounded_corners);
        mUserInfoTxt.setTextColor(Color.BLACK);
        mLanguageDescTxt.setTextColor(Color.BLACK);
        mEmailTxt.setTextColor(Color.BLACK);
        mDriverImg.setImageResource(R.drawable.driver_off);
    }
}