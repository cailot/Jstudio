package com.creapple.tms.mobiledriverconsole;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jinseo on 2016. 7. 26..
 */
public class ProgressActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;


    /**
     * Display 'Loading...' Dialog
     */
    public void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        // prevent background click, which results in disappearing Dialog
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }


    /**
     * Hide 'Loading...' Dialog
     */
    public void hideProgressDialog(){
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}
