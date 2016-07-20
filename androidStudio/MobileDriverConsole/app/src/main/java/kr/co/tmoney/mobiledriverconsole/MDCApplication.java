package kr.co.tmoney.mobiledriverconsole;

import android.app.Application;
import android.util.Log;

import com.firebase.client.Firebase;

import org.apache.log4j.Logger;

import kr.co.tmoney.mobiledriverconsole.utils.ConfigureLog4J;
import kr.co.tmoney.mobiledriverconsole.utils.LocaleHelper;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class MDCApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // language switch helper class
            LocaleHelper.onCreate(getBaseContext());

            ConfigureLog4J.configure(getApplicationContext());
            Logger logger = Logger.getLogger(MDCApplication.class);
            logger.info("initialise log file");
        }catch(Exception e){
            Log.e("LogApplication", e.getMessage());
        }
        Firebase.setAndroidContext(this);
    }
}
