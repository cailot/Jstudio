package com.creapple.tms.mobiledriverconsole.dialog.adapter;

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
public class RouteCustomAdapter extends BaseAdapter{
    private Context mContext;
    private String[] mNames;
    private String[] mTeams;

    public RouteCustomAdapter(Context context, String[] names, String[] teams){
        mContext = context;
        mNames = names;
        mTeams = teams;
    }

    @Override
    public int getCount() {
        return mNames==null ? 0 : mNames.length;
    }

    @Override
    public Object getItem(int i) {
        return mNames==null ? "" :mNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.route_detail, null);
        }

        // Get View
        TextView stopName = (TextView) view.findViewById(R.id.route_id_txt);
        TextView stopZone = (TextView) view.findViewById(R.id.route_name_txt);
        stopName.setText(mNames==null ? "" : mNames[i]);
        stopZone.setText(mTeams==null ? "" : mTeams[i]);

        return view;
    }

}
