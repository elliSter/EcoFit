package com.example.ecofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class WaterActivity extends AppCompatActivity {
    private Button drinkBtn,decGlassBtn;
    private TextView glassCounter;
    private int glassCountInt=0;
    private LottieAnimationView waterAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        drinkBtn = (Button) findViewById(R.id.drinkBtn);
        decGlassBtn = (Button) findViewById(R.id.decGlassBtn);
        glassCounter=(TextView) findViewById(R.id.glassCounter);
        waterAnimation=(LottieAnimationView)findViewById(R.id.animationView);

        drinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                glassCountInt++;
                glassCounter.setText(glassCountInt+" Glasses");
                waterAnimation.playAnimation();

            }
        });

        decGlassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(glassCountInt!=0)glassCountInt--;
                glassCounter.setText(glassCountInt+" Glasses");
            }
        });

    }
}