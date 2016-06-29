package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.co.tmoney.mobiledriverconsole.R;


/**
 * Created by jinseo on 2016. 6. 25..
 */
public class TripOnFragment extends TripFragment {

    private static final String LOG_TAG = TripOnFragment.class.getSimpleName();

    private Button mOriginBtn, mDestinationBtn;

    String[] stops = {"City Hall", "Flinders Station", "St. Kilda", "Lunar Park"};

    boolean[] checkedItems = new boolean[stops.length];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.trip_on_activity, null);
        mContext = container.getContext();
        mOriginBtn = (Button) view.findViewById(R.id.originBtn);
        mDestinationBtn = (Button) view.findViewById(R.id.destinationBtn);
        return view;
    }


    public void originButtonClicked(View view)
    {
        showOriginDialog();

    }

    public void destinationButtonClicked(View view)
    {

    }

    private void showOriginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select your Origin Stop");
        builder.setIcon(R.drawable.ic_media_play);
        builder.setSingleChoiceItems(stops, 0,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(LOG_TAG, which + " is selected");
                    }
                })
                .setPositiveButton("Edit",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.i(LOG_TAG, which
                                        + " is selected with Edit button");
                                // Change screen via Intent
//                                Intent intent = new Intent(M7k37Main.this,
//                                        M7k37Edit.class);
//                                intent.putExtra(M7k37Constants.EDIT_LIST,
//                                        nodeList[editSelected]);
//                                editSelected = 0;
//
//                                // keep editable state when coming back to Main
//                                isEditIntent = true;
//                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.i(LOG_TAG,
                                        which
                                                + " is selected and will close with Close button");
//                                editSelected = 0;
                                dialog.dismiss();
                            }
                        });
        builder.show();
    }
}
