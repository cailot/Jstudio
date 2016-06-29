package kr.co.tmoney.mobiledriverconsole.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.MDCConstants;

/**
 * Created by jinseo on 2016. 6. 25..
 */
public class FareFragment extends Fragment {

    private static final String LOG_TAG = FareFragment.class.getSimpleName();

    private Button mPaymentBtn;

    private NumberPicker mNumberPicker;

    private TextView mPriceTxt;

    String[] stops = {"City Hall", "Flinders Station", "St. Kilda", "Lunar Park"};

    boolean[] checkedItems = new boolean[stops.length];

    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fare_activity, null);
        mContext = container.getContext();
        initialiseUI(view);
        // initialise Firebase
//        Firebase root = new Firebase(MDCConstants.FIREBASE_HOME);
//        root.child(MDCConstants.FIREBASE_TEST_PATH);
//        root.child("users/mchen/name");



        return view;
    }

    private void initialiseUI(View view) {
        mPriceTxt = (TextView) view.findViewById(R.id.fareTxt);
        mPaymentBtn = (Button) view.findViewById(R.id.paymentBtn);
        mPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        // set the minimum value of NumberPicker
        mNumberPicker.setMinValue(0);
        // set the maximum value of NumberPicker
        mNumberPicker.setMaxValue(10);
        // get whether the selector wheel wraps when reaching the min/max value
        mNumberPicker.setWrapSelectorWheel(true);
        // event listener for NumberPicker
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int recent) {
                final String DOUBLE_BYTE_SPACE = "\u2060";
                mPriceTxt.setText(" $ " + recent* MDCConstants.ADULT_FARE + DOUBLE_BYTE_SPACE);
            }
        });
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
