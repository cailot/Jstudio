package kr.co.tmoney.mobiledriverconsole.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import kr.co.tmoney.mobiledriverconsole.MDCMainActivity;
import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.TripOffActivity;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class TripOnConfirmationDialog extends DialogFragment {

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;

    private SpannableStringBuilder mSpannableStringBuilder;

    private TripOffActivity mTripOffActivity;

    public TripOnConfirmationDialog(Activity activity, SpannableStringBuilder spannableStringBuilder) {
        mTripOffActivity = (TripOffActivity) activity;
        mSpannableStringBuilder = spannableStringBuilder;
    }

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
        View view = inflater.inflate(R.layout.trip_on_confirmation, container, false);

        mConfirmTxt = (TextView) view.findViewById(R.id.trip_on_confimation_txt);
        mConfirmTxt.setText(mSpannableStringBuilder);
        mConfirmBtn = (Button) view.findViewById(R.id.trip_on_confirmation_btn);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTripOffActivity.saveStopsDetail();
                mTripOffActivity.saveStopGroupsDetail();
                mTripOffActivity.saveFaresDetail();
                mTripOffActivity.setTripOn();

                Intent i = new Intent(mTripOffActivity.getApplicationContext(), MDCMainActivity.class);
                mTripOffActivity.startActivity(i);
                dismiss();
            }
        });
        mCancelBtn = (Button) view.findViewById(R.id.trip_on_cancel_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
