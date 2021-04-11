package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private TextInputLayout email;
    private  TextInputLayout  password;
    private  TextView forgotPass;
    private  String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        login=findViewById(R.id.logInBtn);
        email=findViewById(R.id.email1);
        password=findViewById(R.id.password);
        forgotPass = findViewById(R.id.forgotPas);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        //login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = email.getEditText().getText().toString().trim();
                String sPassword = password.getEditText().getText().toString().trim();


                if (TextUtils.isEmpty(sEmail)) {
                    email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(sPassword)) {
                    password.setError("Password is Required");
                    return;
                }
                if (sPassword.length() < 8) {
                    password.setError("Password must be >= 8 characters");
                    return;
                }

                    //authenticate the user
                    firebaseAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                firebaseUser = firebaseAuth.getCurrentUser();
                                userID = firebaseUser.getUid();
                                //check if email is verified
                                if (!firebaseUser.isEmailVerified()) {
                                    TextView emailVerification = new TextView(v.getContext());
                                    AlertDialog.Builder passwordVerificationDialog = new AlertDialog.Builder(v.getContext());
                                    passwordVerificationDialog.setTitle("Email verification");
                                    passwordVerificationDialog.setMessage("Verify your email to login");
                                    emailVerification.setText(sEmail);
                                    passwordVerificationDialog.setView(emailVerification);

                                    passwordVerificationDialog.setPositiveButton("Resend", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //resend verification email
                                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(v.getContext(), "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, "Error link is not sent" + e.fillInStackTrace(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });

                                    //cancel dialog
                                    passwordVerificationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // close the dialog
                                        }
                                    });
                                    //display dialog
                                    passwordVerificationDialog.create().show();
                                    //logged in successfully
                                }else {
                                    //get how many times user logged in
                                     double[] loginTimes = new double[1];
                                    Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference = db.collection("Users").document(userID);
                                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                if (value!=null){
                                                    // TODO: 1/3/2021  double always 0.0 
                                                    loginTimes[0] =value.getDouble("Login");
                                                }
                                        }
                                    });

                                    Intent intent;
                                    //if is 1st time go to biometrics else go to home
                                    if(loginTimes[0]==0){
                                        intent = new Intent(LoginActivity.this, BiometricsActivity.class);
                                    }
                                    else{
                                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    }

                                    startActivity(intent);
                                    finish();

                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
        });


        //reset password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordDResetDialog = new AlertDialog.Builder(v.getContext());
                passwordDResetDialog.setTitle("Reset Password");
                passwordDResetDialog.setMessage("Enter your email to receive reset link");
                passwordDResetDialog.setView(resetEmail);

                passwordDResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract email and send reset link
                        String mail = resetEmail.getText().toString().trim();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Reset link sent to to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error link is not sent" + e.fillInStackTrace(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                //cancel dialog
                passwordDResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog

                    }
                });
                //display dialog
                passwordDResetDialog.create().show();
            }
        });
    }
    public void  loginCounter(){

    }
}