package com.creapple.tms.mobiledriverconsole.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

//import org.apache.log4j.Level;

import java.io.File;

//import de.mindpipe.android.logging.log4j.LogConfigurator;
import com.creapple.tms.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class ConfigureLog4J {
    @TargetApi(Build.VERSION_CODES.M)
    public static void configure(Context context){
//        LogConfigurator configurator = new LogConfigurator();

        // path
        String appName = context.getString(R.string.app_name);
        String logPath = Environment.getExternalStorageDirectory() + File.separator + appName;

        // create directory, which directory does not exits

        File file = new File(logPath);
        if(file.exists()){
            file.mkdirs();
        }
//        new File(logPath).mkdirs();

        logPath += File.separator + appName + ".log";

//        configurator.setFileName(logPath);
//        configurator.setFilePattern("%d - [%p::%C] - %m%n");
//        configurator.setMaxFileSize(512*1024);
//        configurator.setMaxBackupSize(10);
//
//        configurator.setRootLevel(Level.DEBUG);
//        configurator.setUseLogCatAppender(true);
//
//        configurator.setLevel("org.apache", Level.ERROR);
//        configurator.configure();
    }
}
