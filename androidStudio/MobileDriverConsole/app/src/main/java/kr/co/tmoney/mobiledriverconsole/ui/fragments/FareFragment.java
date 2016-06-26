package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.tmoney.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment {

    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fare_activity, null);
        mContext = container.getContext();
        return view;
    }
}
