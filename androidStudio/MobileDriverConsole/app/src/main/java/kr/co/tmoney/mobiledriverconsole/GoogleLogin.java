package kr.co.tmoney.mobiledriverconsole;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import kr.co.tmoney.mobiledriverconsole.utils.Constants;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;

/**
 * Created by jinseo on 2016. 7. 22..
 */
public class GoogleLogin extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    SignInButton signInButton;
    Button signOutButton;
    TextView statusTextView;
    EditText mGoogleId, mGooglePassword;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String LOG_TAG = MDCUtils.getLogTag(GoogleLogin.class);

    private final static int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_oauth_activity);

        initialiseGoogle();


        // loading Splash page
        startActivity(new Intent(this, SplashActivity.class));


        statusTextView = (TextView) findViewById(R.id.status_textview);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signOutButton = (Button) findViewById(R.id.sign_out_button);

        mGoogleId = (EditText) findViewById(R.id.login_google_id);
        mGooglePassword = (EditText) findViewById(R.id.login_google_password);
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){ // user is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged : signed in - " + user.getUid());
                }else { // user is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged : signed out");
                }
                updateUI(user);
            }
        };


    }


    private void initialiseGoogle(){

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, connectionResult.getErrorMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button :
                signIn();
                break;
            case R.id.sign_out_button :
                signOut();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(LOG_TAG, "Result : " + result.isSuccess());
            if(result.isSuccess()){
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(LOG_TAG, acct.getServerAuthCode() + "\n" + acct.getEmail() + "\n" + acct.getFamilyName());
//                firebaseAuthWithGoogle(acct);


                Firebase fire = new Firebase(Constants.FIREBASE_HOME);
                fire.authWithOAuthToken("google", acct.getIdToken(), new Firebase.AuthResultHandler(){

                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Log.d(LOG_TAG, "onAuthenticated");
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Log.e(LOG_TAG, "onAuthenticationError");
                    }
                });




            }else{
                updateUI(null);
                Log.e(LOG_TAG, "Google Sign In Failed");
            }
        }
    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle : " + acct.getId());

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential : onComplete - " + task.isSuccessful());

                        if(!task.isSuccessful()){
                            Log.e(LOG_TAG, "signInWithCredential ", task.getException());
                            Toast.makeText(GoogleLogin.this, "Authentication failed ", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }


    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void signOut() {

        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(null);
                statusTextView.setText("Sign Out");
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if(user != null){
            statusTextView.setText("Google User : " + user.getEmail());
        }else{
            statusTextView.setText("Signed Out");
        }
    }
}


class BaseActivity extends AppCompatActivity{
    private ProgressDialog dialog;

    public void showProgressDialog(){
        if(dialog == null){
            dialog = new ProgressDialog(this);
            dialog.setMessage("LLLLLoading.....");
            dialog.setIndeterminate(true);
        }
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }

        @Override
        public void onDestroy(){
            super.onDestroy();
            hideProgressDialog();
        }

}
