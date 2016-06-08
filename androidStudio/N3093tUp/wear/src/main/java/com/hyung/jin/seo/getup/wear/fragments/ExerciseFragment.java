package com.hyung.jin.seo.getup.wear.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyung.jin.seo.getup.R;
import com.hyung.jin.seo.getup.WatchActivity;
import com.hyung.jin.seo.getup.wear.utils.G3tUpWearableConstants;

/**
 * A simple fragment for showing the count
 */
public class ExerciseFragment extends AbstractFragment {

    private TextView bodyText;
    private boolean up = false;
    private Drawable downDrawable;
    private Drawable upDrawable;

    private WatchActivity watchActivity;
    private int totalCount;

    private Vibrator vibrator;

    private Handler handler = new Handler();

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            //
            bodyText.setCompoundDrawablesWithIntrinsicBounds(
                    up ? upDrawable : downDrawable, null, null, null);
            up = !up;
            vibrator.vibrate(G3tUpWearableConstants.DEFAULT_VIBRATION_DURATION_MS);
            Log.d(G3tUpWearableConstants.TAG_EXERCISE, "Up & Down - " + up);
            handler.postDelayed(this, G3tUpWearableConstants.DEFAULT_VIBRATION_DURATION_MS);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_layout, container, false);
        downDrawable = getResources().getDrawable(R.drawable.w_exercise_down);
        upDrawable = getResources().getDrawable(R.drawable.w_exercise_up);
        bodyText = (TextView) view.findViewById(R.id.exerciseText);
        // Trick only for test
        bodyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButton();
            }
        });
        if(vibrator == null)
        {
            vibrator =  (Vibrator) getActivity().getSystemService(getActivity().getApplicationContext().VIBRATOR_SERVICE);
        }
        watchActivity = (WatchActivity) getActivity();
        totalCount = watchActivity.getExerciseCount();
        setText(0 + " / " + totalCount);
        handler.postDelayed(task, G3tUpWearableConstants.DEFAULT_VIBRATION_DURATION_MS);
        Log.d(G3tUpWearableConstants.TAG_EXERCISE, "Exercise Starts !!!!!\t" + this.toString());
        return view;
    }


    @Override
    public void setText(int i) {
        setText(i < 0 ? "0" : String.valueOf(i));
    }

    @Override
    public void setText(String s) {
        bodyText.setText(s);
    }

    /**
     * Stop vibration and timer
     */
    @Override
    public void stopAction() {
        handler.removeCallbacks(task);
        Log.d(G3tUpWearableConstants.TAG_EXERCISE, "stopAction");
    }

    public void clickButton()
    {
        watchActivity.trick();
    }
}
