package com.example.ecofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class BMICalculatorActivity extends AppCompatActivity {

    private Button calcBtn,goToHome;
    private TextInputLayout weight,height;
    private TextView result;
    private String calculation, BMIResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i_calculator);

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
                float bmi;

                //saveDetails
                //user.put("Weight",weightS);

                //calculate BMI
                bmi=weightValue/ (heightValue*heightValue);

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

                calculation ="Result:\n\n"+bmi+"\n"+BMIResult;

                result.setText(calculation);

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
}