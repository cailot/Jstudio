package com.hyung.jin.seo.getup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileConstants;
import com.hyung.jin.seo.getup.mobile.utils.G3tUpMobileUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Created by jinseo on 2016. 3. 10..
 */
public class DisplayActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    /***************************************************
     UI Components
     **************************************************/

    private ImageView imageView, soundImage, vibrationImage;

    private AnimationDrawable animationDrawable;


    /***************************************************
     Configration items in Preferences
     **************************************************/

    private boolean isSound, isVibration;

    private String exerciseTime;

    private String[] repeatDays;


    /***************************************************
     Vibration Resource
     **************************************************/

    Vibrator vibrator;

    private long pattern[] = {10, 200, 0};


    /***************************************************
     Audio Resource
     **************************************************/

    AudioManager audioManager;

    MediaPlayer mediaPlayer;

    private int originalVolume;

    private int maxVolume;

    private Uri alarmSound;

    private String ringtoneName;

//    private Ringtone ringtone;


    // test
    private static int count;


    /***************************************************
     GoogleApiClient for communication to Wearable
     **************************************************/

    private GoogleApiClient googleApiClient;


    /***************************************************
     MobileListener will use this to stop alarm
     **************************************************/

    public static DisplayActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayActivity.count++;
        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Display start\t" + count + "\t" + this.toString());

        instance = DisplayActivity.this;

//        if(DisplayActivity.count%3 == 0)
//        {
//            Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Display exit" + "\t" + new Date());
//            SettingActivity previous = (SettingActivity) SettingActivity.settings;
//            if(previous!=null) {
//                previous.finish();
//            }
//            finish();
//            return;
//        }

        setContentView(R.layout.display_activity);

        setUpConfig();

        if(!G3tUpMobileUtils.isDayForAlarm(repeatDays)) // DO NOT trigger alarms if today is not ticked in repeated list on Preferences
        {
            Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Display exit...." + "\t" + new Date());
            SettingActivity previous = (SettingActivity) SettingActivity.settings;
            if(previous!=null) {
                previous.finish();
            }
            finish();
            return;
        }

        setUpGoogleApi();

        setUpUI();

        // 'alarm start' from SettingActivity
        Intent intent = getIntent();
        String action = StringUtils.defaultString(intent.getStringExtra(G3tUpMobileConstants.ALARM_STATE));

        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Acton : " + action);

        if(G3tUpMobileConstants.ALARM_START.equalsIgnoreCase(action)) {
            startAlarm();
            // clear intent contents
            //intent.putExtra(G3tUpMobileConstants.ALARM_STATE, G3tUpMobileConstants.ALARM_STOP);
        }else{
            Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Abnormal start so no action requires");
        }
    }

    /**
     * Establish googleApi connection and will be connected in onStart() method
     */
    private void setUpGoogleApi() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();
        // moved from onStart()
//        googleApiClient.connect();
    }

    /**
     * Load config items from Preference
     */
    private void setUpConfig() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isSound = preferences.getBoolean(G3tUpMobileConstants.SOUND_SET, false);
        ringtoneName = preferences.getString(G3tUpMobileConstants.RINGTON_SET, "");

        isVibration = preferences.getBoolean(G3tUpMobileConstants.VIBRATION_SET, false);
        exerciseTime = preferences.getString(G3tUpMobileConstants.EXERCISE_TIME, "5");
        repeatDays = preferences.getString(G3tUpMobileConstants.REPEAT_DAY, "").split(G3tUpMobileConstants.SEPARATOR);
    }

    /**
     * Initialise UI components
     */
    private void setUpUI()
    {
        imageView = (ImageView) findViewById(R.id.imageAnimation);
        imageView.setBackgroundResource(R.drawable.animation_image);
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        soundImage = (ImageView)findViewById(R.id.soundImage);
        vibrationImage = (ImageView)findViewById(R.id.vibrationImage);
    }


    /**
     * Start alarm with sound & vibration according to the condition in Preferences
     */
    private void startAlarm()
    {
        if(isSound){
            audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            alarmSound = Uri.parse(ringtoneName);

//            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
//            String name = ringtone.getTitle(getApplicationContext());
//
//            Log.d(G3tUpMobileConstants.TAG_SETTING, "Ringtone : " + name);


            if(mediaPlayer == null)
            {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), alarmSound);
            }
            mediaPlayer.reset();

            // max volume
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            try{
                mediaPlayer.setDataSource(getApplicationContext(), alarmSound);
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e(G3tUpMobileConstants.TAG_DISPLAY, "Failed to prepare media player to play alarm \n" + e);
            }
            mediaPlayer.start();
            soundImage.setBackgroundResource(R.drawable.m_sound_on);
        }else{
            soundImage.setBackgroundResource(R.drawable.m_sound_off);
        }

        if(isVibration) {
            vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, 0);
            vibrationImage.setBackgroundResource(R.drawable.m_vibration_on);
        }else{
            vibrationImage.setBackgroundResource(R.drawable.m_vibration_off);
        }
        animationDrawable.start();
        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Alarm Start at " + new Date());
    }


    /**
     * Stop alarm if sound or vibration turns on, stop them as well
     */
    public void stopAlarm()
    {
        if(isSound){
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
            if((mediaPlayer != null) && (mediaPlayer.isPlaying()))
            {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        if(isVibration) {
            vibrator.cancel();
        }
        animationDrawable.stop();

        SettingActivity previous = (SettingActivity) SettingActivity.settings;
        if (previous != null) {
            previous.finish();
        }
        finish();

        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Alarm Stop at " + new Date());
    }

    /**
     * Send start command to Wearable when connected
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "onConnected");
        sendMessage(exerciseTime + G3tUpMobileConstants.START_EXERCISE_MESSAGE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "onConnectionFailed");
    }

    /**
     * Connect googleApliClient
     */
    @Override
    protected void onStart() {
        Log.d(G3tUpMobileConstants.TAG_DISPLAY, "onStart");
        super.onStart();
        googleApiClient.connect();
    }

    /**
     * Disconnect googleApiClient
     */
    @Override
    protected void onStop() {
        if(googleApiClient!=null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Log.v(G3tUpMobileConstants.TAG_DISPLAY, "onStop - disconnect");
        }else{
            Log.v(G3tUpMobileConstants.TAG_DISPLAY, "onStop - nothing");
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.v(G3tUpMobileConstants.TAG_DISPLAY, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v(G3tUpMobileConstants.TAG_DISPLAY, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.v(G3tUpMobileConstants.TAG_DISPLAY, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        // Reset the alarm volume to the user's original setting.
        Log.v(G3tUpMobileConstants.TAG_DISPLAY, "onDestroy");
//        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
//        mediaPlayer.release();
        super.onDestroy();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
//
//                              Sending Message Via Google API
//
////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method sends the signal of starting activity with exercise time in Wearable
     * @param message exerciseTime + G3tUpMobileConstants.START_EXERCISE_MESSAGE
     */
    public void sendMessage(String message)
    {
        if(googleApiClient.isConnected())
        {
            if(message == null || message.equals(""))
            {
                message = "NOTHING from mobile";
            }
            new SendMessageToDataLayer(G3tUpMobileConstants.COMMUNICAITION_PATH_FROM_MOBILE, message).start();

        }else{
            Log.d(G3tUpMobileConstants.TAG_DISPLAY, "Not connected at SendMessage()");
        }
    }

    /**
     * Inner class handles communication from Mobile to Wearable via Thread
     */
    public class SendMessageToDataLayer extends Thread
    {
        String path;
        String message;

        public SendMessageToDataLayer(String path, String message)
        {
            this.path = path;
            this.message = message;
        }

        @Override
        public void run() {
            NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for(Node node : nodesResult.getNodes())
            {
                MessageApi.SendMessageResult messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, message.getBytes()).await();
                if(messageResult.getStatus().isSuccess())
                {
                    Log.v(G3tUpMobileConstants.TAG_DISPLAY, "Successfully sent to " + node.getDisplayName() + "'s node id is " + node.getId());
                    Log.v(G3tUpMobileConstants.TAG_DISPLAY, "Total Node size is " + nodesResult.getNodes().size());
                }else{
                    Log.e(G3tUpMobileConstants.TAG_DISPLAY, "Error while sending message");
                }
            }
        }
    }
}