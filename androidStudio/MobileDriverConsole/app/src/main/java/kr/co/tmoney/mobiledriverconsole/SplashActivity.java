package kr.co.tmoney.mobiledriverconsole;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by jinseo on 2016. 7. 8..
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        TextView textView = (TextView) findViewById(R.id.splash_loading_txt);
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(textView,"textColor", Color.WHITE, Color.TRANSPARENT);
        objectAnimator.setDuration(800);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 15000); // disappear in 5 sesc
    }
}
