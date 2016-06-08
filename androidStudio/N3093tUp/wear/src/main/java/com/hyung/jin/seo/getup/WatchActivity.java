package com.hyung.jin.seo.getup;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.hyung.jin.seo.getup.wear.fragments.AbstractFragment;
import com.hyung.jin.seo.getup.wear.fragments.DisplayFragment;
import com.hyung.jin.seo.getup.wear.fragments.ExerciseFragment;
import com.hyung.jin.seo.getup.wear.utils.G3tUpWearableConstants;

/**
 * The main activity for the Jumping Jack application. This activity registers itself to receive
 * sensor values. Since on wearable devices a full screen activity is very short-lived, we set the
 * FLAG_KEEP_SCREEN_ON to give user adequate time for taking actions but since we don't want to
 * keep screen on for an extended period of time, there is a SCREEN_ON_TIMEOUT_MS that is enforced
 * if no interaction is discovered.
 * <p>
 * This activity includes a {@link android.support.v4.view.ViewPager} with two pages, one that
 * shows the current count and one that allows user to reset the counter. the current value of the
 * counter is persisted so that upon re-launch, the counter picks up from the last value. At any
 * stage, user can set this counter to 0.
 * <p>
 * What to do ?
 * 1. start exercise
 * 2. start animation & vibration
 * 3. update exercise count
 * 4. when count reaches to goal
 * 4-1 trigger stop alarm & vibration on Mobile
 * 4-2 display good message
 */
public class WatchActivity extends WearableActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    /**************************************************
        System Resource
    **************************************************/

    private SensorManager sensorManager;

    private Sensor sensor;


    /**************************************************
        UI Components
    **************************************************/

    private long lastTime = 0;

    private boolean up;

    private int jumpCounter = 0;

    private int status = 0;


    /**************************************************
        Framment
    **************************************************/
    private AbstractFragment fragment;


    /**************************************************
        Excercise count coming from Mobile
    **************************************************/
    private int exerciseCount;


    /**************************************************
        In order to send 'stop' message to mobile
     **************************************************/

    private Node mobileNode;

    private GoogleApiClient googleApiClient;


    /**************************************************
        Indicator to show that watch is triggered from mobile
     ***************************************************/
    private boolean fromMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        setUpThreshold();

        setUpGoogleApi();

        status = G3tUpWearableConstants.EXERCISE_STATE; // first fragment

        selectFragment();

        setUpResource();

    }

    /**
     * Set up SensorManager & Sensor
     */
    private void setUpResource() {
        if(sensorManager == null)
        {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        }
    }

    /**
     *  Determine displaying fragment based on status
     */
    private void selectFragment() {
        fragment = null;
        if(status==G3tUpWearableConstants.EXERCISE_STATE)
        {
            fragment = new ExerciseFragment();
        }else if(status==G3tUpWearableConstants.DISPLAY_STATE){
            fragment = new DisplayFragment();
        }else{
            // throw error
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Set up exercise time
     */
    private void setUpThreshold() {
        String message = getIntent().getStringExtra(G3tUpWearableConstants.FROM_MOBILE_DATA);
        if(message==null || message.equalsIgnoreCase(""))
        {
            message = "Listening....";
            exerciseCount = 3;
        }else if(message.contains(G3tUpWearableConstants.START_EXERCISE_MESSAGE))
        {
            int index = message.indexOf(G3tUpWearableConstants.START_EXERCISE_MESSAGE);
            exerciseCount = Integer.parseInt(message.substring(0, index).trim());
            fromMobile = true;
        }else{
            exerciseCount = 10;
        }
        Log.d(G3tUpWearableConstants.TAG_WATCH, "Got message from Mobile - " + message + "\t" + this.toString());
    }

    /**
     * Set up GoogleApiClient to communicate with Mobile
     */
    private void setUpGoogleApi() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();
    }


    // event handler when button pressed. this is just for testing purpose


    public void trick()
    {
        increaseCount();
    }

    /**
     * Terminate Activity
     */
    public void goodBye()
    {
        Log.d(G3tUpWearableConstants.TAG_WATCH, "Dismiss WatchActivity\t" + this.toString());
        finish();
    }

    /**
     * Return exercise count
     * @return exercise count coming from Mobile
     */
    public int getExerciseCount()
    {
        return exerciseCount;
    }




    /**
     * This method sends the signal of stop alarm in Moblie
     * @param message G3tUpWearableConstants.ALARM_STOP
     */
    private void sendMessage(String message) {
        if(mobileNode != null && googleApiClient != null)
        {
            Wearable.MessageApi.sendMessage(googleApiClient, mobileNode.getId(), G3tUpWearableConstants.COMMUNICAITION_PATH_FROM_WEAR, message.getBytes())
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if(!sendMessageResult.getStatus().isSuccess())
                            {
                                Log.d(G3tUpWearableConstants.TAG_WATCH, "Failed message : " + sendMessageResult.getStatus().getStatusCode());
                            }else{
                                Log.d(G3tUpWearableConstants.TAG_WATCH, "Message succeeded");
                            }
                        }});
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        Log.d(G3tUpWearableConstants.TAG_WATCH, "onStart()");
    }

    @Override
    protected void onStop() {
        if(googleApiClient!=null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Log.d(G3tUpWearableConstants.TAG_WATCH, "onStop - disconnect");
        }else{
            Log.d(G3tUpWearableConstants.TAG_WATCH, "onStop - nothing");
        }
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(G3tUpWearableConstants.TAG_WATCH, "onResume");
        if (sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            if (Log.isLoggable(G3tUpWearableConstants.TAG_WATCH, Log.DEBUG)) {
                Log.d(G3tUpWearableConstants.TAG_WATCH, "Successfully registered for the sensor updates");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(G3tUpWearableConstants.TAG_WATCH, "onPause");
        sensorManager.unregisterListener(this);
        if (Log.isLoggable(G3tUpWearableConstants.TAG_WATCH, Log.DEBUG)) {
            Log.d(G3tUpWearableConstants.TAG_WATCH, "Unregistered for sensor events");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(G3tUpWearableConstants.TAG_WATCH, "onConnected() start");
        Wearable.NodeApi.getConnectedNodes(googleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node : nodes.getNodes()) {
                            if (node != null && node.isNearby()) {
                                mobileNode = node;
                                Log.d(G3tUpWearableConstants.TAG_WATCH, "Connected to " + mobileNode.getDisplayName());
                            }
                        }
                        if (mobileNode == null) {
                            Log.d(G3tUpWearableConstants.TAG_WATCH, "Not connected");
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(G3tUpWearableConstants.TAG_WATCH, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(G3tUpWearableConstants.TAG_WATCH, "onConnectionFailed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        detectJump(event.values[0], event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


///////////////////////////////////////////////////////////////////////////////////////////////////
//
//                              JumpingJack logic
//
////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * A simple algorithm to detect a successful up-down movement of hand(s). The algorithm is
     * based on the assumption that when a person is wearing the watch, the x-component of gravity
     * as measured by the Gravity Sensor is +9.8 when the hand is downward and -9.8 when the hand
     * is upward (signs are reversed if the watch is worn on the right hand). Since the upward or
     * downward may not be completely accurate, we leave some room and instead of 9.8, we use
     * GRAVITY_THRESHOLD. We also consider the up <-> down movement successful if it takes less than
     * TIME_THRESHOLD_NS.
     */
    private void detectJump(float xValue, long timestamp) {
        if ((Math.abs(xValue) > G3tUpWearableConstants.GRAVITY_THRESHOLD)) {
            if (timestamp - lastTime < G3tUpWearableConstants.TIME_THRESHOLD_NS && up != (xValue > 0)) {
                onJumpDetected(!up);
            }
            up = xValue > 0;
            lastTime = timestamp;
        }
    }

    /**
     * Called on detection of a successful down -> up or up -> down movement of hand.
     */
    private void onJumpDetected(boolean up) {
        // we only count a pair of up and down as one successful movement
        if (up) {
            return;
        }
        // update screen
        increaseCount();
    }

    /**
     * Increase exercise count aligns to the Jumping Jack activity
     * It should count and act only in Exercise mode
     */
    private void increaseCount() {

        // if motion detected not in exercise mode, please ignore
        if(status!=G3tUpWearableConstants.EXERCISE_STATE) return;

        jumpCounter++;
        Log.d(G3tUpWearableConstants.TAG_WATCH, "Exercise - " + jumpCounter + "    State - " +
                (status==G3tUpWearableConstants.EXERCISE_STATE? "EXERCISE_STATE" : "DISPLAY_STATE" ));

        if (jumpCounter >= exerciseCount) {
//        if (jumpCounter == exerciseCount) { // in case of sending the signal again by accidentally exceeding threshold
            fragment.stopAction();

            // send 'stop' message to mobile and switch to DisplayFragment
            if(fromMobile) {
                Log.d(G3tUpWearableConstants.TAG_WATCH, "stopAlarm");
                sendMessage(G3tUpWearableConstants.ALARM_STOP);
            }
            status = G3tUpWearableConstants.DISPLAY_STATE;
            selectFragment();
            return;
        }
        fragment.setText(jumpCounter + " / " + exerciseCount);
    }

}
