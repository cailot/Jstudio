package com.creapple.tms.mobiledriverconsole.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

    private EditText mEmailTxt, mPasswordTxt;

    private ImageView mLoginImg;

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
        Log.d(LOG_TAG, "Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mEmailTxt = (EditText) findViewById(R.id.log_in_email_txt);
        mPasswordTxt = (EditText) findViewById(R.id.log_in_password_txt);
        mLoginImg = (ImageView) findViewById(R.id.log_in_img);
        mLoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    isFirst = false;

                }else { // user is signed out
                    Log.d(LOG_TAG, "Signed out");
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
        Log.d(LOG_TAG, " Key ID :::::  --> " + keyId);

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
                        Log.d(LOG_TAG, "logIn : onComplete - " + task.isSuccessful());
                        if(!task.isSuccessful()){
                            Log.w(LOG_TAG, "logIn exception - " + task.getException());
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
                    Log.d(LOG_TAG, "************* " + alreadyLogIn);
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mEmailTxt.setError(null);
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
                Log.d(LOG_TAG, "checking status ....... isDone ? " + isDone);
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
                Log.w(LOG_TAG, "logIn exception - Account is locked ");
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
}
