package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BMICalculatorActivity extends AppCompatActivity {

    private Button calcBtn,goToHome;
    private TextInputLayout weight,height;
    private TextView result;
    private String calculation, BMIResult;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private  String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i_calculator);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        weight=findViewById(R.id.weightTxt);
        height=findViewById(R.id.heightTxt);
        result=findViewById(R.id.BMIResult);
        calcBtn=findViewById(R.id.calculateBtn);
        goToHome=findViewById(R.id.goToHome);


        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String weightS=weight.getEditText().getText().toString();
                String heightS=height.getEditText().getText().toString();
                float weightValue=Float.parseFloat(weightS);
                float heightValue=Float.parseFloat(heightS)/100;

                userID = firebaseAuth.getCurrentUser().getUid();

                DocumentReference documentReference = db.collection("Metrics").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    //on success
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
//                        if (value != null){
//                            weight.setText(value.getDouble("Username"));
//                          todo: add timestamp and check if is valid < 24 hours
//                        }
                    }
                    //on failure
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR","Error getting documents.",e);
                    }
                });

                Map<String,Object> metrics = new HashMap<>();
                metrics.put("Weight",weightValue);
                metrics.put("Height",heightValue);
                documentReference.set(metrics);

                result.setText(BMIresultCalc(weightValue,heightValue));
            }
        });

        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(BMICalculatorActivity.this, HomeActivity.class);
                startActivity(intentHome);
                finish();
            }
        });
    }

    private String BMIresultCalc(float weight,float height){
        String BMIResult;
        float bmi;

        bmi=weight/(height*height);

        if(bmi<16)
            BMIResult="Severely Under weight";
        else if(bmi<18.5)
            BMIResult="Under weight";
        else if(bmi>=18.5 && bmi<=24.5)
            BMIResult="Normal weight";
        else if(bmi>=25 && bmi<=29.9)
            BMIResult="Overweight";
        else
            BMIResult="Obese";

        calculation =bmi+"\n"+BMIResult;
        return calculation;
    }

}