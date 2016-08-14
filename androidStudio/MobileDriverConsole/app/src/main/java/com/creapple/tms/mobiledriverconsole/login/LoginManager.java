package com.creapple.tms.mobiledriverconsole.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import com.creapple.tms.mobiledriverconsole.ProgressActivity;
import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.TripOffActivity;
import com.creapple.tms.mobiledriverconsole.utils.Constants;
import com.creapple.tms.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 7. 22..
 */
public class LoginManager extends ProgressActivity {

    private EditText mEmailTxt, mPasswordTxt;

    private ImageView mLoginImg;

    // this is reported bug that 'onAuthStateChanged()' called twice times.
    // http://stackoverflow.com/questions/37674823/firebase-android-onauthstatechanged-fire-twice-after-signinwithemailandpasswor
    private boolean isFirst = true;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String LOG_TAG = MDCUtils.getLogTag(LoginManager.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // loading Splash page
        startActivity(new Intent(this, SplashActivity.class));

        mEmailTxt = (EditText) findViewById(R.id.log_in_email_txt);
        mPasswordTxt = (EditText) findViewById(R.id.log_in_password_txt);
        mLoginImg = (ImageView) findViewById(R.id.log_in_img);
        mLoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // error check later
                logIn(mEmailTxt.getText().toString(), mPasswordTxt.getText().toString());
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null && isFirst){ // user is signed in
                    Log.d(LOG_TAG, "Signed in - " + user.getUid());
                    updateLoginRecord(user);
                    isFirst = false;
                }else { // user is signed out
                    Log.d(LOG_TAG, "Signed out");
                }
                updateUI(user);
            }
        };
    }

    /**
     * update user info when sigining in
     * @param user
     */
    private void updateLoginRecord(FirebaseUser user) {
        Firebase firebase = new Firebase(Constants.FIREBASE_HOME + Constants.FIREBASE_USER_LIST_PATH + "/" + user.getUid());
        Firebase userPost = firebase.push();
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put(Constants.AUTH_EMAIL, user.getEmail());
        auth.put(Constants.AUTH_AREA_TAG, Constants.AUTH_AREA_TAG_VALE);
        auth.put(Constants.AUTH_COMPANY_TAG, Constants.AUTH_COMPANY_TAG_VALE);
        auth.put(Constants.AUTH_TYPE, Constants.AUTH_TYPE_VALE);
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
                        hideProgressDialog();
                        if(!task.isSuccessful()){
                            Log.w(LOG_TAG, "logIn exception - " + task.getException());
                            Toast.makeText(LoginManager.this, "Email and Password does not match", Toast.LENGTH_SHORT).show();
                        }else{
                            mProgressDialog.dismiss();
                            Intent intent = new Intent(LoginManager.this, TripOffActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }


    /**
     * Validate form data
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;
        String email = mEmailTxt.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmailTxt.setError("Required");
            valid = false;
        }else if(!StringUtils.contains(email, "@") || !StringUtils.contains(email, ".")) {
            mEmailTxt.setError("Invalid Email Format");
            valid = false;
        }else{
            mEmailTxt.setError(null);
        }

        String password = mPasswordTxt.getText().toString();
        if(TextUtils.isEmpty(password)) {
            mPasswordTxt.setError("Required");
            valid = false;
        }else if(password.length()<6){
            mPasswordTxt.setError("Too Short Password");
            valid = false;
        }else{
            mPasswordTxt.setError(null);
        }

        return valid;

    }


    /**
     * Update UI but not used too much in this case
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if(user !=null){
            String info = user.getUid() + "\t" + user.getEmail();
            Log.d(LOG_TAG, "updateUI() - " + info);
        }else{

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mEmailTxt.setError(null);
        mPasswordTxt.setError(null);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
