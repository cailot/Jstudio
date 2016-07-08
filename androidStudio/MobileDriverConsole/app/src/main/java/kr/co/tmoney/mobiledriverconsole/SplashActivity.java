package kr.co.tmoney.mobiledriverconsole;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by jinseo on 2016. 7. 8..
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 5000); // disappear in 5 sesc
    }
}
