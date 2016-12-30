package com.creapple.tms.mobiledriverconsole.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creapple.tms.mobiledriverconsole.ProgressActivity;
import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.TripOffActivity;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinseo on 2016. 7. 22..
 */
public class LoginManager extends ProgressActivity {

    private EditText mPinTxt, mPasswordTxt;

    private Button[] mButton;

//    private ImageView mLoginImg;

    // this is reported bug that 'onAuthStateChanged()' called twice times.
    // http://stackoverflow.com/questions/37674823/firebase-android-onauthstatechanged-fire-twice-after-signinwithemailandpasswor
    private boolean isFirst = true;

    private String alreadyLogIn = "";

    private boolean isSplashNeed = true;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String LOG_TAG = MDCUtils.getLogTag(LoginManager.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mPinTxt = (EditText) findViewById(R.id.log_in_pin_txt);
        mPasswordTxt = (EditText) findViewById(R.id.log_in_password_txt);
        mPinTxt.requestFocus();
        mButton = new Button[12];
        mButton[0] = (Button) findViewById(R.id.login_btn_0);
        mButton[1] = (Button) findViewById(R.id.login_btn_1);
        mButton[2] = (Button) findViewById(R.id.login_btn_2);
        mButton[3] = (Button) findViewById(R.id.login_btn_3);
        mButton[4] = (Button) findViewById(R.id.login_btn_4);
        mButton[5] = (Button) findViewById(R.id.login_btn_5);
        mButton[6] = (Button) findViewById(R.id.login_btn_6);
        mButton[7] = (Button) findViewById(R.id.login_btn_7);
        mButton[8] = (Button) findViewById(R.id.login_btn_8);
        mButton[9] = (Button) findViewById(R.id.login_btn_9);
        mButton[10] = (Button) findViewById(R.id.login_btn_10); // back
        mButton[11] = (Button) findViewById(R.id.login_btn_11); // enter

//        Drawable enterImage = getResources().getDrawable(R.drawable.key_enter);
////        enterImage.setBounds(0,0, (int)(enterImage.getIntrinsicWidth()*0.5), (int)(enterImage.getIntrinsicHeight()*0.5));
////        ScaleDrawable scaleEnter = new ScaleDrawable(enterImage, 0, enterImage.getIntrinsicWidth(), enterImage.getIntrinsicHeight());
//        mButton[11].setCompoundDrawables(enterImage, null, null, null);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null && isFirst){ // user is signed in
//                    Log.d(LOG_TAG, "Signed in - " + user.getUid());
                    isFirst = false;

                }else { // user is signed out
//                    Log.d(LOG_TAG, "Signed out");
                }
            }
        };
    }

    /**
     * update user info when sigining in
     * @param user
     */
    private void updateLoginRecord(FirebaseUser user) {

        Firebase firebase = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_USER_LIST_PATH + "/" + user.getUid());
        // update logIn flag from false to true
        firebase.child(Constants.USER_LOGIN).setValue(true);

        // add new node
        Firebase userPost = firebase.push();
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put(Constants.AUTH_LOG_IN_TIME, ServerValue.TIMESTAMP);
        userPost.setValue(auth);
        String keyId = userPost.getKey();
//        Log.d(LOG_TAG, " Key ID :::::  --> " + keyId);

        // save user info for other Activity
        MDCUtils.put(this, Constants.USER_UID, user.getUid());
        MDCUtils.put(this, Constants.USER_EMAIL, user.getEmail());
        MDCUtils.put(this, Constants.USER_PATH, keyId);
    }





    /**
     * Sign in process
     * @param email
     * @param password
     */
    private void logIn(String email, String password) {
        if(!validateForm()){
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(LOG_TAG, "logIn : onComplete - " + task.isSuccessful());
                        if(!task.isSuccessful()){
//                            Log.w(LOG_TAG, "logIn exception - " + task.getException());
                            hideProgressDialog();
                            Toast.makeText(LoginManager.this, "Email and Password does not match", Toast.LENGTH_SHORT).show();
                        }else{
                            checkAlradyLogIn();
                            new CheckLogInTask().execute();
                        }
                    }
                });
    }

    /**
     * Get logIn status under Uid node
     */
    private void checkAlradyLogIn() {
        Firebase firebase = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_USER_LIST_PATH + "/" + mAuth.getCurrentUser().getUid());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constants.USER_LOGIN).getValue() != null) {
                    alreadyLogIn = Boolean.toString((Boolean) dataSnapshot.child(Constants.USER_LOGIN).getValue());
//                    Log.d(LOG_TAG, "************* " + alreadyLogIn);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    /**
     * Validate form data
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;
//        String email = mEmailTxt.getText().toString();
//        if(TextUtils.isEmpty(email)){
//            mEmailTxt.setError("Required");
//            valid = false;
//        }else if(!StringUtils.contains(email, "@") || !StringUtils.contains(email, ".")) {
//            mEmailTxt.setError("Invalid Email Format");
//            valid = false;
//        }else{
//            mEmailTxt.setError(null);
//        }
//
//        String password = mPasswordTxt.getText().toString();
//        if(TextUtils.isEmpty(password)) {
//            mPasswordTxt.setError("Required");
//            valid = false;
//        }else if(password.length()<6){
//            mPasswordTxt.setError("Too Short Password");
//            valid = false;
//        }else{
//            mPasswordTxt.setError(null);
//        }
        String pinInput = StringUtils.defaultString(mPinTxt.getText().toString()).trim();
        if(pinInput.length() != 6){
            mPinTxt.setError("PIN requires 6 Digits");
            valid = false;
        }else{
            mPinTxt.setError(null);
        }

        String passInput = StringUtils.defaultString(mPasswordTxt.getText().toString()).trim();
        if(passInput.length() != 6){
            mPasswordTxt.setError("Password requires 6 Digits");
            valid = false;
        }else{
            mPasswordTxt.setError(null);
        }

        return valid;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mPinTxt.setError(null);
        mPasswordTxt.setError(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // loading Splash page
        if (isSplashNeed) {
            isSplashNeed = false;
            startActivity(new Intent(this, SplashActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * This task make sure we retrieve login value to check if someone is already occupying the account
     */
    public class CheckLogInTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            boolean isDone = false;
            while(!isDone){
//                Log.d(LOG_TAG, "checking status ....... isDone ? " + isDone);
                if(!StringUtils.defaultString(alreadyLogIn).equalsIgnoreCase("")){
                    isDone = true;
                }
                try {
                    Thread.sleep(Constants.THREAD_SLEEP);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            hideProgressDialog();

            if(StringUtils.defaultString(alreadyLogIn).equalsIgnoreCase("true")){ // someone is already using this account
//                Log.w(LOG_TAG, "logIn exception - Account is locked ");
                Toast.makeText(LoginManager.this, "Account is locked by another user", Toast.LENGTH_SHORT).show();
            }else{ // everything is fine and ready to GO !!!!
                updateLoginRecord(mAuth.getCurrentUser());
                mProgressDialog.dismiss();
                Intent intent = new Intent(LoginManager.this, TripOffActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /** Called when the user touches the button */
    public void pushButton(View view) {
        mPinTxt.setError(null);
        mPasswordTxt.setError(null);
        int event = view.getId();
        switch (event){
            case R.id.login_btn_0 :
                inputNumber(0);
                break;
            case R.id.login_btn_1 :
                inputNumber(1);
                break;
            case R.id.login_btn_2 :
                inputNumber(2);
                break;
            case R.id.login_btn_3 :
                inputNumber(3);
                break;
            case R.id.login_btn_4 :
                inputNumber(4);
                break;
            case R.id.login_btn_5 :
                inputNumber(5);
                break;
            case R.id.login_btn_6 :
                inputNumber(6);
                break;
            case R.id.login_btn_7 :
                inputNumber(7);
                break;
            case R.id.login_btn_8 :
                inputNumber(8);
                break;
            case R.id.login_btn_9 :
                inputNumber(9);
                break;
            case R.id.login_btn_10 : // back
                backSpace();
                break;
            case R.id.login_btn_11 : // enter
                if(mPinTxt.isFocused()){
                    // move focus from Pin to Password
                    mPasswordTxt.requestFocus();
                }else if(mPasswordTxt.isFocused()){
                    // submit
                    logIn("pmbus." + mPinTxt.getText().toString().trim() + "@gmail.com", mPasswordTxt.getText().toString().trim());
                }
                break;
        }
    }

    /**
     * When users select any digit from normal keypad
     * @param num
     */
    private void inputNumber(int num){
        String input = null;

        if(mPinTxt.isFocused()){
            if(mPinTxt.getText()==null || mPinTxt.getText().toString().trim().equals("")){
                input = "";
            }else{
                input = mPinTxt.getText().toString().trim();
            }
            // No more input, if length is already 6
            if(input.length()>=6){
                mPinTxt.setError("No More Input");
            }else{
                input += num;
                mPinTxt.setText(input);
            }
        }else if(mPasswordTxt.isFocused()){
            if(mPasswordTxt.getText()==null || mPasswordTxt.getText().toString().trim().equals("")){
                input = "";
            }else{
                input = mPasswordTxt.getText().toString().trim();
            }
            // No more input, if length is already 6
            if(input.length()>=6){
                mPasswordTxt.setError("No More Input");
            }else{
                input += num;
                mPasswordTxt.setText(input);
            }
        }

    }

    /**
     * When users select backspace key
     */
    private void backSpace() {
        if (mPinTxt.isFocused()) {
            if (mPinTxt.getText() == null || mPinTxt.getText().toString().trim().equals("") || mPinTxt.getText().length() == 1) {
                mPinTxt.setText("");
            } else {
                String input = mPinTxt.getText().toString().trim();
                int txtLenght = input.length();
                if (txtLenght >= 2) {
                    input = input.substring(0, txtLenght - 1);
                    mPinTxt.setText(input);
                }
            }
        } else if (mPasswordTxt.isFocused()) {
            if (mPasswordTxt.getText() == null || mPasswordTxt.getText().toString().trim().equals("") || mPasswordTxt.getText().length() == 1) {
                mPasswordTxt.setText("");
            } else {
                String input = mPasswordTxt.getText().toString().trim();
                int txtLenght = input.length();
                if (txtLenght >= 2) {
                    input = input.substring(0, txtLenght - 1);
                    mPasswordTxt.setText(input);
                }
            }
        }
    }
}
