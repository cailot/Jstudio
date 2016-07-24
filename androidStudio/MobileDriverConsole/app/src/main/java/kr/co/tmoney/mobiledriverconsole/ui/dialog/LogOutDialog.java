package kr.co.tmoney.mobiledriverconsole.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.Constants;

/**
 * Created by jinseo on 2016. 7. 17..
 */
public class LogOutDialog extends DialogFragment {

    private TextView mConfirmTxt;

    private Button mConfirmBtn, mCancelBtn;

    private Activity mActivity;

    public LogOutDialog(Activity activity) {
        mActivity = activity;
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
        View view = inflater.inflate(R.layout.log_out_confirmation, container, false);

        mConfirmTxt = (TextView) view.findViewById(R.id.log_out_confimation_txt);
        mConfirmBtn = (Button) view.findViewById(R.id.log_out_confirmation_btn);
        mConfirmBtn.setText(getString(R.string.log_off_confirm));
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // log out user
                Firebase firebase = new Firebase(Constants.FIREBASE_HOME);
                firebase.unauth();
                // finish all activity
                mActivity.finishAffinity();
                // disappear
                dismiss();
            }
        });
        mCancelBtn = (Button) view.findViewById(R.id.log_out_cancel_btn);
        mCancelBtn.setText(getString(R.string.log_off_cancel));
        mCancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
