package com.creapple.tms.mobiledriverconsole.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creapple.tms.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 7. 1..
 */
public class ZoneCustomAdapter extends BaseAdapter{
    private Context mContext;
    private int[] mFares;
    private String[] mZones;

    public ZoneCustomAdapter(Context context, int[] names, String[] types){
        mContext = context;
        mFares = names;
        mZones = types;
    }

    @Override
    public int getCount() {
        return mFares ==null ? 0 : mFares.length;
    }

    @Override
    public Object getItem(int i) {
        return mFares ==null ? "" : mFares[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.zone_detail, null);
        }

        // Get View
        TextView stopName = (TextView) view.findViewById(R.id.zone_name_txt);
        TextView stopZone = (TextView) view.findViewById(R.id.zone_fare_txt);
        stopName.setText(mFares ==null ? "" : mFares[i]+"");
        stopZone.setText(mZones ==null ? "" : mZones[i]);
        return view;
    }

}
