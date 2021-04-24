package com.example.ecofit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.time.Instant;

import io.grpc.internal.JsonUtil;


public class HomeFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;
    private ProgressBar progressBar;
    private TextView steps,distance,calories,time;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private ImageButton waterBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        //start service
        getActivity().startService(new Intent(getActivity(),HomeFragment.class));
        //requireActivity().startService(new Intent(getContext(), HomeFragment.class));

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        steps=(TextView)view.findViewById(R.id.steps);
        distance=(TextView)view.findViewById(R.id.distanceTxt);
        calories=(TextView)view.findViewById(R.id.caloriesTxt);
        time=(TextView)view.findViewById(R.id.timeTxt);

        //to start Water Activity
        waterBtn=(ImageButton)view.findViewById(R.id.water);
        waterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),WaterActivity.class));
            }
        });

        //set sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //Accelerometer sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector=new SensorEventListener() {
            @Override
            //Reading Events
            public void onSensorChanged(SensorEvent event) {
                if(event!=null){
                    float x_acceleration = event.values[0];
                    float y_acceleration = event.values[1];
                    float z_acceleration = event.values[2];

                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude-MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 6){
                        stepCount++;
                    }

                    steps.setText(stepCount.toString());

                    //progress circle
                    int progress=(int)( (stepCount/260)*100);
                    progressBar.setProgress(progress);

                    //calculate distance in KMs (mpakalistika) around 75 cms per step
                    float dis;
                    dis=(float)(stepCount*75)/(float)100000;
                    String disString=String.valueOf((new DecimalFormat("###.##").format(dis)));
                    distance.setText(disString);

                    //calories burnt, around 0.04 per step
                    float cal;
                    cal=(float)(stepCount*(0.04));
                    String calString=String.valueOf((new DecimalFormat("####").format(cal)));
                    calories.setText(calString);

                    //time



                }

            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(stepDetector,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        //show distance
//        int stepsInt= Integer.parseInt(steps.toString());
//        //float kms=getDistanceRun(stepsInt);
//        String kmString=Float.toString(getDistanceRun(stepsInt));
//        distance.setText(kmString);

        //Long stepsLong=Long.parseLong(steps.toString());
        //String test=String.valueOf(getDistanceRun(80));
        //distance.setText( test);


        return view;

    }

    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences =getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    public void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }

    //function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistanceRun(long steps){
        //---------78 for men, 70 for women
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }
}