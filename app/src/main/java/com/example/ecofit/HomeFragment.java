package com.example.ecofit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;
    private ProgressBar progressBar;
    private TextView steps,distance,calories,time,usersname;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private ImageButton waterBtn;

    int caloriesInt=5;
    int distanceInt=6;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private  String userID,dis,calor;

    private Calendar cal=Calendar.getInstance();
    private int currentDate=cal.get(Calendar.DAY_OF_YEAR);

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
        getActivity().startService(new Intent(getActivity(),HomeFragment.class));

        //database connection
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        usersname=(TextView)view.findViewById(R.id.usernameField);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        steps=(TextView)view.findViewById(R.id.steps);
        distance=(TextView)view.findViewById(R.id.distanceTxt);
        calories=(TextView)view.findViewById(R.id.caloriesTxt);
        time=(TextView)view.findViewById(R.id.weightLabel);

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
                    int progress=(int)( ((double)stepCount / (double)1000)*100);
                    progressBar.setProgress(progress);

                    distance.setText(calculateDistance(stepCount));
                    calories.setText(calculateCalories(stepCount));

                    calor=calculateCalories(stepCount);
                    caloriesInt=Integer.parseInt(calculateCalories(stepCount));

                }

            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(stepDetector,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        //arxi
        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReferenceRecords = db.collection("Records").document(userID);
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            //on success
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot document = task.getResult();
////                        if (value != null){
////                            weight.setText(value.getDouble("Username"));
////                          todo: add timestamp and check if is valid < 24 hours
////                        }``
//            }
//            //on failure
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("ERROR","Error getting documents.",e);
//            }
//        });

        //caloriesInt= Integer.parseInt(calculateCalories(stepCount));
        //distanceInt= Integer.parseInt(calculateDistance(stepCount));
        Map<String,Object> records = new HashMap<>();
        records.put("Steps",stepCount);
        records.put("Calories",caloriesInt);
        records.put("Distance",distanceInt);

        documentReferenceRecords.set(records);
//telos
        return view;

    }

    //calculate distance in KMs  around 75 cms per step, 78 for men, 70 for women
    private String calculateDistance(int stepCount){
        float dis;
        dis=(float)(stepCount*75)/(float)100000;
        String disString=(new DecimalFormat("###.##").format(dis));
        return disString;
    }

    //calories burned, around 0.04 per step
    private String calculateCalories(int stepCount){
        float cal=(float)(stepCount*(0.04));
        String calString=String.valueOf((new DecimalFormat("####").format(cal)));
        return calString;
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

}