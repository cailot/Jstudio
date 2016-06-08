package com.hyung.jin.seo.getup.wear.utils;

/**
 * Created by jinseo on 2015. 6. 9..
 */
public interface G3tUpWearableConstants
{
    public static final String TAG = "### JINHYUNG ###";

    public static final String TAG_WATCH = "### WATCH ###";

    public static final String TAG_EXERCISE = "### EXERCISE ###";

    public static final String COMMUNICAITION_PATH_FROM_MOBILE = "/getup/from/mobile";

    public static final String COMMUNICAITION_PATH_FROM_WEAR = "/getup/from/wear";

    public static final String FROM_MOBILE_DATA = "from_mobile_data";

    public static final String FROM_WEARABLE_DATA = "from_wearable_data";

    public static final String ALARM_STATE = "alarm_state";

    public static final String ALARM_START = "alarm_start";

    public static final String ALARM_STOP = "alarm_stop";

    public static final int DEFAULT_VIBRATION_DURATION_MS = 1000; // in millis

    public static final String PREF_KEY_COUNTER = "counter";

    /** an up-down movement that takes more than this will not be registered as such **/
    public static final long TIME_THRESHOLD_NS = 2000000000; // in nanoseconds (= 2sec)

    /**
     * Earth gravity is around 9.8 m/s^2 but user may not completely direct his/her hand vertical
     * during the exercise so we leave some room. Basically if the x-component of gravity, as
     *
     * measured by the Gravity sensor, changes with a variation (delta) > GRAVITY_THRESHOLD,
     * we consider that a successful count.
     */
    public static final float GRAVITY_THRESHOLD = 7.0f;


    public static final long SECOND = 1000; // in milliseconds


    // staus of fragment
    public static final int START_STATE = 0;

    public static final int EXERCISE_STATE = START_STATE + 1;

    public static final int DISPLAY_STATE = START_STATE + 2;

    public static final String START_EXERCISE_MESSAGE = " times Start !!!!";

    public static final String JUMP_TOTAL_COUNT = "jump_total_count";

}