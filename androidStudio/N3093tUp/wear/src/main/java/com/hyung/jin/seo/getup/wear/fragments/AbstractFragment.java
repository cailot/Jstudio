package com.hyung.jin.seo.getup.wear.fragments;

import android.app.Fragment;

/**
 * Created by jinseo on 2015. 6. 11..
 */
public abstract class AbstractFragment extends Fragment
{
    // display information on TextView
    public abstract void setText(int i);

    // display informatin on TextView
    public abstract void setText(String s);

    // stop specific action in Fragment
    public abstract void stopAction();
}