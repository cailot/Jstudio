package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import org.apache.log4j.Logger;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class SettingFragment extends Fragment{

    private static final String LOG_TAG = MDCUtils.getLogTag(FareFragment.class);

    private Logger logger = Logger.getLogger(LOG_TAG);

    private TextView mHandOverTxt, mLogOutTxt;

    private RadioButton mThaiBtn, mEnglishBtn;

    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_activity, null);
        mContext = container.getContext();
        mHandOverTxt = (TextView) view.findViewById(R.id.setting_hand_over_txt);
        mLogOutTxt = (TextView) view.findViewById(R.id.setting_log_out_txt);
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
        mThaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingEvents(view);
            }
        });
        mEnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingEvents(view);
            }
        });


        logger.error("setting.....");
        return view;

    }



    private void settingEvents(View view){
        switch(view.getId()){
            case R.id.setting_hand_over_txt :
                logger.debug("Hand Over Event");
                break;
            case R.id.setting_log_out_txt :
                logger.debug("Log Out Event");
                break;
            case R.id.setting_thai_btn :
                logger.debug("Thai Language Event");
                break;
            case R.id.setting_english_btn :
                logger.debug("English Language Event");
                break;
        }
    }
}