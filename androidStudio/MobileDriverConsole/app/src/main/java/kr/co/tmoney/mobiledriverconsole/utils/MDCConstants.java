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

    public static final int FARE_ORIGIN_REQUEST = STATUS_BASE + 4;

    public static final int FARE_DESTINATION_REQUEST = STATUS_BASE + 5;




    public static final int GOOGLE_MAP_ZOOM_LEVEL = 15;

    public static final String GOOGLE_LOG_STATUS = "googleLog";

    public static final String TRIP_STATUS = "trip";

    public static final double ADULT_FARE = 25.0;


    public static final String PRE_LOG_ENTRY = "###\t";

    public static final String POST_LOG_ENTRY = "\t###";

    ///////////////////////////// Geofencing Test ////////////////////////////////
    /**
     * For this sample, geofences expire after one hour.
     */
    public static final long GEOFENCE_EXPIRATION = 60 * 60 * 1000;
    //public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    public static final float GEOFENCE_RADIUS_IN_METERS = 1; // 1 mile, 1.6 km


}
