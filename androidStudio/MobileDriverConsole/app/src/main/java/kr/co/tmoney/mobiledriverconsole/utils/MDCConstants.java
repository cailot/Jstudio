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


    public static final String ROUTE_ID = "route_id";

    public static final String STRING_ARRAY_SEPARATOR = " *$#@^! ";

    public static final String STOPS_DETAIL_IN_ROUTE = "stops_detail";

    public static final String STOPS_ID_IN_ROUTE = "stops_id";

    public static final String STOPS_NAME_IN_ROUTE = "stops_name";

    public static final String STOPS_TYPE_IN_ROUTE = "stops_type";

    public static final String STOPS_LATITUDE_IN_ROUTE = "stops_latitude";

    public static final String STOPS_LONGITUDE_IN_ROUTE = "stops_longitude";








    public static final String FIREBASE_HOME = "https://tmoney-260ba.firebaseio.com/";


    //public static final String FIREBASE_ROUTE_LIST_PATH = "Jins/route_list";
    public static final String FIREBASE_ROUTE_LIST_PATH = "Thai_Private/TH_PMB/routes";




    public static final int STATUS_BASE = 2016;

    public static final int ON = STATUS_BASE + 1;

    public static final int OFF = STATUS_BASE + 2;

    public static final int NO_VALUE = STATUS_BASE + 3;

    public static final int FARE_ORIGIN_REQUEST = STATUS_BASE + 4;

    public static final int FARE_DESTINATION_REQUEST = STATUS_BASE + 5;




    ////////////////////////////
    //
    //  Dialog Tags
    //
    //////////////////////////////
    public static final String ROUTE_DIALOG_TAG = "Route Dialog";

    public static final String VEHICLE_DIALOG_TAG = "Vehicle Dialog";

    public static final String ORIGIN_DIALOG_TAG = "Origin Dialog";

    public static final String DESTINATION_DIALOG_TAG = "Destination Dialog";



    public static final int GOOGLE_MAP_ZOOM_LEVEL = 15;

    public static final int GOOGLE_MAP_POLLING_INTERVAL = 1000*10; // may need to change to 14 ~ 15 in beta test

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
