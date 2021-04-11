package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.type.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class OutTrainActivity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    private Chronometer chronometer;
    private boolean timerRunning;
    private long pauseOffset = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView timerText;
    Button stopStartButton;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_train);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        timerText = (TextView) findViewById(R.id.timerText);
        stopStartButton = (Button) findViewById(R.id.startStopButton);
        timer = new Timer();
        //init fused location
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        //check persmission
//        if (ActivityCompat.checkSelfPermission(OutTrainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            //when permission granted
//            getLocation();
//        } else {
//            //request permission
//            ActivityCompat.requestPermissions(OutTrainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//        }

    }

//    private void getLocation() {
//        //init task location
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(final Location location) {
//                // when success
//                if(location != null){
//                    // sync map
//                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(GoogleMap googleMap) {
//                            //init lat lng
//
//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                            //create marker
//                            MarkerOptions options= new MarkerOptions().position(latLng).title("I am there");
//                            //zoom Map
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//                            //add marker on map
//                            googleMap.addMarker(options);
//
//                        }
//                    });
//                }
//            }
//        });
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 44 ){
//            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                //when permission granted
//                //call method
//                getLocation();
//            }
//        }
//    }

    public void resetTapped(View view)
    {
        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Reset Timer");
        resetAlert.setMessage("Are you sure you want to reset the timer?");
        resetAlert.setPositiveButton("Reset", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(timerTask != null)
                {
                    timerTask.cancel();
                    setButtonUI("START", R.color.teal_200);
                    time = 0.0;
                    timerStarted = false;
                    timerText.setText(formatTime(0,0,0));

                }
            }
        });

        resetAlert.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //do nothing
            }
        });

        resetAlert.show();

    }

    public void startStopTapped(View view)
    {
        if(timerStarted == false)
        {
            timerStarted = true;
            setButtonUI("STOP", R.color.magenta);

            startTimer();
        }
        else
        {
            timerStarted = false;
            setButtonUI("START", R.color.purple_700);

            timerTask.cancel();
        }
    }

    private void setButtonUI(String start, int color)
    {
        stopStartButton.setText(start);
        stopStartButton.setTextColor(ContextCompat.getColor(this, color));
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

//    public void startChronometer(View view){
//        if(!timerRunning){
//            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
//            chronometer.start();
//            timerRunning=true;
//        }
//    }
//
//    public void pauseChronometer(View view){
//        if(timerRunning){
//            chronometer.stop();
//            pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
//            timerRunning=false;
//        }
//    }
//
//    public void stopChronometer(View view){
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        pauseOffset=0;
//    }
}