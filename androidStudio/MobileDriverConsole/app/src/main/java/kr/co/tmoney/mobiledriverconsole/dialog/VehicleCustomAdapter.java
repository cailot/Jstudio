package kr.co.tmoney.mobiledriverconsole.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 7. 1..
 */
public class VehicleCustomAdapter extends BaseAdapter{
    private Context mContext;
    private String[] mNames;

    public VehicleCustomAdapter(Context context, String[] names){
        mContext = context;
        mNames = names;
    }

    @Override
    public int getCount() {
        return mNames==null ? 0 : mNames.length;
    }

    @Override
    public Object getItem(int i) {
        return mNames==null ? "" : mNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.vehicle_detail, null);
        }

        // Get View
        TextView stopName = (TextView) view.findViewById(R.id.vehicle_name_txt);
        stopName.setText(mNames==null ? "" : mNames[i]);

        return view;
    }

}
