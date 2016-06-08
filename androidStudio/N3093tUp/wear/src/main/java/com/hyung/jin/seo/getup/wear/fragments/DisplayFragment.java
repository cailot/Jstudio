package com.hyung.jin.seo.getup.wear.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyung.jin.seo.getup.R;
import com.hyung.jin.seo.getup.WatchActivity;

/**
 * A simple fragment for showing the count
 */
public class DisplayFragment extends AbstractFragment {


    private TextView bodyText;
    private ImageView bodyImage;

    private WatchActivity watchActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_layout, container, false);
        watchActivity = (WatchActivity) getActivity();
        bodyText = (TextView) view.findViewById(R.id.completeText);
        setText("Have a nice day!");
        bodyImage = (ImageView)view.findViewById(R.id.completeImage);
        bodyImage.setBackgroundResource(R.drawable.w_complete);
//        bodyText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopAction();
//            }
//        });
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

    @Override
    public void stopAction() {
//        Log.d(G3tUpWearableConstants.TAG_DISPLAY, "stopAction");
//        watchActivity.goodBye();
    }
}
