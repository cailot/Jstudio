package kr.co.tmoney.mobiledriverconsole;

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
        mProgressDialog.show();
    }


    /**
     * Hide 'Loading...' Dialog
     */
    public void hideProgressDialog(){
        if(mProgressDialog !=null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}
