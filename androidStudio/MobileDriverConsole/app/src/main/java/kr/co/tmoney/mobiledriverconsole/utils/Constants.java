package kr.co.tmoney.mobiledriverconsole.utils;

/**
 * Created by jinseo on 2016. 6. 24..
 */
public interface Constants {


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

    public static final String STOPS_TYPE = "stop";

    public static final String STRING_ARRAY_SEPARATOR = " *$#@^! ";

    public static final String STOPS_DETAIL_IN_ROUTE = "stops_detail";

    public static final String STOPS_IN_ROUTE = "stops_id";

    public static final String FARES_IN_ROUTE = "fares_in_route";

    public static final String STOP_GROUPS_IN_ROUTE = "stop_groups_in_route";

    public static final String STOPS_LATITUDE_IN_ROUTE = "stops_latitude";

    public static final String STOPS_LONGITUDE_IN_ROUTE = "stops_longitude";

    public static final String VEHICLE_NAME = "vehicle_name";

    public static final String VEHICLE_TRIP_ON = "tripOn";

    public static final String VEHICLE_INDEX = "index";

    public static final String VEHICLE_TRIP = "trip";

    public static final String VEHICLE_CURRENT_ROUTE = "currentRoute";

    public static final String VEHICLE_REAR = "rearVehicle";

    public static final String VEHICLE_FRONT = "frontVehicle";

    public static final String VEHICLE_PASSENGERS = "passengers";

    public static final String VEHICLE_LATITUDE = "lat";

    public static final String VEHICLE_LONGITUDE = "lon";

    public static final String VEHICLE_UPDATED = "updated";

    public static final String FRONT_VEHICLE_ID = "front_vehicle_id";

    public static final String FRONT_VEHICLE_INDEX = "front_vehicle_index";



    public static final String AUTH_EMAIL = "email";

    public static final String AUTH_AREA_TAG = "areaTag";

    public static final String AUTH_COMPANY_TAG = "companyTag";

    public static final String AUTH_TYPE = "type";

    public static final String AUTH_LOG_IN_TIME = "loginTimestamp";

    public static final String AUTH_LOG_OUT_TIME = "logoutTimestamp";

    public static final String AUTH_AREA_TAG_VALE = "Thai_Private";

    public static final String AUTH_COMPANY_TAG_VALE = "TH_PMB";

    public static final String AUTH_TYPE_VALE = "DR";

    public static final String AUTH_ROUTE = "route";

    public static final String AUTH_VEHICLE = "vehicle";


    public static final String USER_UID = "user_uid";

    public static final String USER_EMAIL = "user_email";

    public static final String USER_PATH = "user_path";





    public static final String PRINT_TICKET_NUMBER = "print_ticket_number";

    public static final String PRINT_DATE = "print_date";

    public static final String PRINT_ROUTE = "print_route";

    public static final String PRINT_BUS = "print_bus";

    public static final String PRINT_FROM = "print_from";

    public static final String PRINT_TO = "print_to";

    public static final String PRINT_NUMBER_OF_PERSON = "print_number_of_person";

    public static final String PRINT_FARE_PER_PERSON = "print_fare_per_person";

    public static final String PRINT_TOTAL = "print_total";






//    public static final String FIREBASE_HOME = "https://tmoney-260ba.firebaseio.com/Thai_Private/TH_PMB/";

    public static final String FIREBASE_HOME = "https://tmoney-260ba.firebaseio.com/Thai_Private/development/";

    public static final String FIREBASE_ROUTE_LIST_PATH = "routes";

    public static final String FIREBASE_VEHICLE_LIST_PATH = "vehicles";

    public static final String FIREBASE_FARE_LIST_PATH = "fares";

    public static final String FIREBASE_USER_LIST_PATH = "users";

    public static final String FIREBASE_TRIP_LIST_PATH = "trips";




    public static final int TRIP_ON_FRAGMENT_TAB = 0;

    public static final int FARE_FRAGMENT_TAB = 1;

    public static final int SETTING_FRAGMENT_TAB = 2;

    public static final int STATUS_BASE = 2016;

    public static final int ON = STATUS_BASE + 1;

    public static final int OFF = STATUS_BASE + 2;

    public static final int NO_VALUE = STATUS_BASE + 3;

    public static final int FARE_ORIGIN_REQUEST = STATUS_BASE + 4;

    public static final int FARE_DESTINATION_REQUEST = STATUS_BASE + 5;

    public static final int REQUEST_ENABLE_BT = STATUS_BASE + 6;





    public static final int GPS_PERMISSION_GRANT = 6;

    public static final int GPS_UPDATE_VEHICLES_INTERVAL = 10;

    public static final int DISTANCE_THRESHOLD_SAFE = 500;

    public static final int DISTANCE_THRESHOLD_DANGER = 100;

    public static final int GPS_UPDATE_MAXIMUM = 99999;


    ////////////////////////////
    //
    //  Dialog Tags
    //
    //////////////////////////////
    public static final String ROUTE_DIALOG_TAG = "Route Dialog";

    public static final String VEHICLE_DIALOG_TAG = "Vehicle Dialog";

    public static final String ORIGIN_DIALOG_TAG = "Origin Dialog";

    public static final String DESTINATION_DIALOG_TAG = "Destination Dialog";

    public static final String PASSENGER_DIALOG_TAG = "Passenger Dialog";

    public static final String LOGOUT_DIALOG_TAG = "Logout Dialog";


    ////////////////////////////////
    //
    //  Receiver for IntentService
    //
    /////////////////////////////////

    public static final String BROADCAST_SERVICE = PACKAGE_NAME + ".BROADCAST";

    public static final String GEOFENCE_INTENT_MESSAGE = "geofence_intent_message";

    public static final String GEOFENCE_INTENT_ACTION = "geofence_intent_action";

    public static final String GEOFENCE_INTENT_STOP = "geofence_intent_stop";

    public static final int GEOFENCE_NOTIFICATION_RESPONSIVENESS = 1000;




    public static final int GOOGLE_MAP_ZOOM_LEVEL = 15;

    public static final int GOOGLE_MAP_POLLING_INTERVAL = 1000; // every second GPS update


    public static final String PRE_LOG_ENTRY = "###\t";

    public static final String POST_LOG_ENTRY = "\t###";

    public static final String GOOGLE_DISTANCE_MATRIX_ADDRESS = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";

    public static final String GOOGLE_DISTANCE_MATRIX_API_KEY = "AIzaSyARcc57YdbbyaiS517LoES5i4sqBl2kyh8";

    public static final String GOOGLE_DISTANCE_MATRIX_OK = "OK";

    public static final String GOOGLE_DISTANCE_MATRIX_STATUS = "status";

    public static final String GOOGLE_DISTANCE_MATRIX_ROWS = "rows";

    public static final String GOOGLE_DISTANCE_MATRIX_ELEMENTS = "elements";

    public static final String GOOGLE_DISTANCE_MATRIX_DISTANCE = "distance";

    public static final String GOOGLE_DISTANCE_MATRIX_DURATION = "duration";

    public static final String GOOGLE_DISTANCE_MATRIX_VALUE = "value";

    public static final String GOOGLE_DISTANCE_MATRIX_TEXT = "text";


    ///////////////////////////// Geofencing Test ////////////////////////////////
    /**
     * For this sample, geofences expire after one hour.
     */
    public static final long GEOFENCE_EXPIRATION = 5 * 60 * 60 * 1000;

    public static final float GEOFENCE_RADIUS_IN_METERS = 150;


    ///////////////////////////// Bluetooth Printer ///////////////////////////////
    public static final int FLAG_STATE_CHANGE = 32;

    public static final int FLAG_FAIL_CONNECT = 33;

    public static final int FLAG_SUCCESS_CONNECT = 34;

    public static final String BLUETOOTH_PRINTER = "820USEB";



    //////////////////////////// Language Setting /////////////////////////////////////

    public static final String SELECTED_LANGUAGE = "selected_language";

    public static final String LANGUAGE_KOREAN = "ko";

    public static final String LANGUAGE_ENGLISH = "en";

    public static final String LANGUAGE_THAILAND = "th";





    public static final int THREAD_SLEEP = 1000;



}