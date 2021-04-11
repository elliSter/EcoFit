package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LaunchActivity extends AppCompatActivity {


    private Button login;
    private Button signup;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private LoginButton facebookBtn;
    private static final String TAG = "FacebookAuthentication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        facebookBtn =findViewById(R.id.facebookBtn);
        login=findViewById(R.id.logInBtn1);
        signup=findViewById(R.id.signUpBtn1);
        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        //login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //signup
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

        //go to home page if user is logged in
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(LaunchActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
        //facebook login
        facebookBtn.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();
        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError"+error);

            }
        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fuser= firebaseAuth.getCurrentUser();
                if(fuser!=null){
                    //todo preview details to home page
                }else{

                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    firebaseAuth.signOut();
                }
            }
        };


    }
    //handle facebook token
    private void handleFacebookToken(AccessToken token){
        Log.d(TAG,"handleFacebookToken" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        //check facebook credentials in db
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"sign with credentials successfully");
                    Toast.makeText(LaunchActivity.this,"Login with facebook successfully" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LaunchActivity.this,HomeActivity.class);
                    startActivity(intent);
                }else{
                    Log.d(TAG,"sign with credentials failed");
                    Toast.makeText(LaunchActivity.this,"Login with facebook failed" , Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LaunchActivity.this,"Login failed"+e.getMessage() , Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
//
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener !=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}