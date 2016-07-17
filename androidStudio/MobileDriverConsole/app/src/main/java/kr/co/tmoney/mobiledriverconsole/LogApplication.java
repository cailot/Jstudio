package kr.co.tmoney.mobiledriverconsole;

import android.app.Application;
import android.util.Log;

import org.apache.log4j.Logger;

import kr.co.tmoney.mobiledriverconsole.utils.ConfigureLog4J;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class LogApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ConfigureLog4J.configure(getApplicationContext());
            Logger logger = Logger.getLogger(LogApplication.class);
            logger.info("initialise log file");
        }catch(Exception e){
            Log.e("LogApplication", e.getMessage());
        }
    }
}
