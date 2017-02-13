package com.creapple.tms.mobiledriverconsole;

import android.app.ProgressDialog;
import android.view.WindowManager;

/**
 * Created by jinseo on 2016. 7. 26..
 */
//public class ProgressActivity extends AppCompatActivity {
public class ProgressActivity extends FullScreeenActivity{
    public ProgressDialog mProgressDialog;


    /**
     * Display 'Loading...' Dialog
     */
    public void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //
            //              Immersive mode for Dialog
            //
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            mProgressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//            //Set the dialog to immersive
//            mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
//            //Clear the not focusable flag from the window
//            mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        }
        // prevent background click, which results in disappearing Dialog
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mProgressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //Set the dialog to immersive
        mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

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
