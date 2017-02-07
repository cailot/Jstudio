package com.creapple.tms.mobiledriverconsole;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * This class disables soft keys on screen to avoid any miscontrol by accident
 * Created by jinseo on 2016. 11. 14..
 */

public class FullScreeenActivity extends AppCompatActivity {

    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                 | View.SYSTEM_UI_FLAG_FULLSCREEN
                 | View.SYSTEM_UI_FLAG_IMMERSIVE;
        // This work only for android 4.4+
        if(currentApiVersion > Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        // This work only for android 4.4+
//        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
//        {
//
//            getWindow().getDecorView().setSystemUiVisibility(flags);
//
//            // Code below is to handle presses of Volume up or Volume down.
//            // Without this, after pressing volume buttons, the navigation bar will
//            // show up and won't hide
//            final View decorView = getWindow().getDecorView();
//            decorView
//                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
//                    {
//
//                        @Override
//                        public void onSystemUiVisibilityChange(int visibility)
//                        {
//                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
//                            {
//                                decorView.setSystemUiVisibility(flags);
//                            }
//                        }
//                    });
//        }

        // apply transperent color on navigation bar
//        if(currentApiVersion >= Build.VERSION_CODES.LOLLIPOP){
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

}
