package kr.co.tmoney.mobiledriverconsole.utils;

/**
 * Created by jinseo on 2016. 6. 24..
 */
public interface MDCConstants {


    /*
     *  Identify unique name by using package name
     */
    public static final String PACKAGE_NAME = "kr.co.tmoney.mobiledriverconsole";

    /*
     *  Define name of SharedPreference
     */
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";



    public static final String FIREBASE_HOME = "https://tmoney-260ba.firebaseio.com";

    public static final String FIREBASE_DRIVE_PATH = "Thai_Private/TH_PMB";

    public static final String FIREBASE_USER_PATH = "users";

    public static final String FIREBASE_TEST_PATH = "Jins";





    public static final int STATUS_BASE = 2016;

    public static final int ON = STATUS_BASE + 1;

    public static final int OFF = STATUS_BASE + 2;

    public static final int NO_VALUE = STATUS_BASE + 3;




    public static final String GOOGLE_LOG_STATUS = "googleLog";

    public static final String TRIP_STATUS = "trip";

    public static final double ADULT_FARE = 25.0;


}
