package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private Button signup;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextInputLayout username;
    private  TextInputLayout password;
    private TextInputLayout name;
    private  TextInputLayout surname;
    private TextInputLayout email;
    private  TextInputLayout confirmPass;
    private  String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        signup=findViewById(R.id.signUpBtn2);
        username= findViewById(R.id.username);
        password= (TextInputLayout)findViewById(R.id.password);
        confirmPass=findViewById(R.id.passwordConfirmation);
        email= (TextInputLayout) findViewById(R.id.email);
        name=findViewById(R.id.name);
        surname=findViewById(R.id.surname);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //signup
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = email.getEditText().getText().toString().trim();
                String sPassword = password.getEditText().getText().toString().trim();
                String sSurname = surname.getEditText().getText().toString().trim();
                String sFirstName = name.getEditText().getText().toString().trim();
                String sUsername = username.getEditText().getText().toString().trim();


                if(TextUtils.isEmpty(sEmail)){
                    email.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(sPassword)){
                    password.setError("Password is Required");
                    return;
                }
                if(sPassword.length() < 8 ){
                    password.setError("Password must be >= 8 characters");
                    return;
                }

                //register the user in firebase
                firebaseAuth.createUserWithEmailAndPassword(sEmail,sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful()){
                            // send email verification
                            FirebaseUser firebaseUserr = firebaseAuth.getCurrentUser();
                            firebaseUserr.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignupActivity.this,"Verification email has been sent" , Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","Error email not sent" + e.getMessage() );
                                }
                            });

                            Toast.makeText(SignupActivity.this,"User Created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            //register user in cloud firestore
                            DocumentReference documentReference = db.collection("Users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("FirstName",sFirstName);
                            user.put("Surname",sSurname);
                            user.put("Email",sEmail);
                            user.put("Username",sUsername);
                            user.put("Login",0);

                            documentReference.set(user);
                            //.addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("TAG","onSuccess:user profile is created for " +userID );
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("TAG","Error adding document" + e );
//
//                                }
//                            });

                            //goto
                            Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            Toast.makeText(SignupActivity.this,"Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });
    }
}