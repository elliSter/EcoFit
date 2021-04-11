package com.example.ecofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BiometricsActivity extends AppCompatActivity {

    private Button yesBtn,noBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometrics);

        yesBtn=findViewById(R.id.yesBtn);
        noBtn=findViewById(R.id.noBtn);

        //go to BMI calculator
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBMI = new Intent(BiometricsActivity.this, BMICalculatorActivity.class);
                startActivity(intentBMI);
                finish();
            }
        });

        //go to Home page
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(BiometricsActivity.this, HomeActivity.class);
                startActivity(intentHome);
                finish();
            }
        });

    }
}