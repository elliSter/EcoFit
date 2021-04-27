package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import java.util.Calendar;

public class WaterActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String userID;

    private Button drinkBtn,decGlassBtn;
    private TextView glassCounter,mlCounter;
    private int glassCountInt=0,mlCountInt=0;
    private LottieAnimationView waterAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        drinkBtn = (Button) findViewById(R.id.drinkBtn);
        decGlassBtn = (Button) findViewById(R.id.decGlassBtn);
        glassCounter=(TextView) findViewById(R.id.glassCounter);
        mlCounter=(TextView) findViewById(R.id.mlCounter);
        waterAnimation=(LottieAnimationView)findViewById(R.id.animationView);

        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView glasses,ml;
                //fetch db
                userID = firebaseAuth.getCurrentUser().getUid();

                DocumentReference documentReference = db.collection("Water").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    //on success
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
//                        if (value != null){
//                            glasses.setText(value.getDouble("Username"));
//                          todo: add timestamp and check if is valid < 24 hours
//                        }
                        System.out.println(document);
                    }
                    //on failure
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR","Error getting documents.",e);
                    }
                });

                glassCountInt++;
                mlCountInt+=250;


                Map<String,Object> water = new HashMap<>();
                water.put("Glass",glassCountInt);
                water.put("Ml",mlCountInt);
                documentReference.set(water);

                glassCounter.setText(glassCountInt+" Glasses");
                mlCounter.setText(mlCountInt+" ml");
                waterAnimation.playAnimation();

            }
        });

        decGlassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userID = firebaseAuth.getCurrentUser().getUid();

                DocumentReference documentReference = db.collection("Water").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    //on success
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
//                        if (value != null){
//                            glasses.setText(value.getDouble("Username"));
//                          todo: add timestamp and check if is valid < 24 hours
//                        }
                        System.out.println(document);
                    }
                    //on failure
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR","Error getting documents.",e);
                    }
                });

                if((glassCountInt!=0) && (mlCountInt!=0)) {
                    glassCountInt--;
                    mlCountInt -= 250;

                    Map<String,Object> water = new HashMap<>();
                    water.put("Glass",glassCountInt);
                    water.put("Ml",mlCountInt);
                    documentReference.set(water);

                    glassCounter.setText(glassCountInt + " Glasses");
                    mlCounter.setText(mlCountInt + " ml");
                }
            }
        });

    }
}