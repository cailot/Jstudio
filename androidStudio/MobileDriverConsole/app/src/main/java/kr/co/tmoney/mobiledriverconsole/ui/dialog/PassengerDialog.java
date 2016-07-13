package kr.co.tmoney.mobiledriverconsole.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import kr.co.tmoney.mobiledriverconsole.R;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class PassengerDialog extends DialogFragment{

    // Bind the value between user selection in dialog and stop name on FareFragment
    public interface PassValueFromPassengerDialogListener{
        void sendPassengerCount(String name);
    }

    public PassengerDialog() {
        mCount = new String[30];
        for(int i=0; i<mCount.length; i++){
            mCount[i] = (i + 1) + "";
        }
    }

    /**
     * This interface hooks up FareFragment and PassengerDialog by calling
     * 1. calling setPassValueFromPassengerDialogListener() from FareFragment
     * 2. passing value via sendPassengerCount() from PassengerDialog
     */
    PassValueFromPassengerDialogListener mPassValueFromPassengerDialogListener;

    String[] mCount;


    public void setPassValueFromPassengerDialogListener(PassValueFromPassengerDialogListener passValueFromPassengerDialogListener){
        mPassValueFromPassengerDialogListener = passValueFromPassengerDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.passenger_dialog, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.passenger_count_grid_view);
        PassengerCustomAdapter passengerCustomAdapter = new PassengerCustomAdapter(getActivity(), mCount);
        gridView.setAdapter(passengerCustomAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
//                String stopInfo = names[pos] + " : " + zones[pos];
                // update TexView in FareFragment according to user's choice
                mPassValueFromPassengerDialogListener.sendPassengerCount(mCount[pos]);
                dismiss();
            }
        });
        return view;
    }
}
