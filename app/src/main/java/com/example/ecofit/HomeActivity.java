  package com.example.ecofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Button verifyBtn;
    private TextView username;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private  String userID;
    private Toolbar toolbar;
    ImageView menuIcon;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init db
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //side menu
        navigationView=findViewById(R.id.side_menu);
        drawerLayout=findViewById(R.id.drawer_layout);
        menuIcon=findViewById(R.id.menuIcon);

        //update username
        updateNavHeader();
        loginCounter();
        //call side_menu method
        navigationDrawer();

        //show bottom menu
        bottomNavigationView=findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();



    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else  drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
        super.onBackPressed();
    }




    //bottom menu fragments
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;

            switch (item.getItemId()){
                case R.id.nav_home:
                    selectedFragment=new HomeFragment();
                    break;
                case R.id.nav_goal:
                    selectedFragment=new GoalsFragment();
                    break;
                case R.id.nav_run:
                    selectedFragment=new OutTrainFragment();
                    break;
                case R.id.nav_stretch:
                    selectedFragment=new StretchFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container,selectedFragment).commit();

            return true;
        }
    };

    //side menu fragments
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment=null;
        switch (id){

            case R.id.nav_home:
                selectedFragment=new HomeFragment();
                break;
            case R.id.nav_profile:
                selectedFragment=new ProfileFragment();
                break;
            case R.id.nav_settings:
                selectedFragment=new SettingsFragment();
                break;
            case R.id.nav_contact:
                //selectedFragment=new ContactFragment();
                Intent intentContact = new Intent(HomeActivity.this, ContactUsActivity.class);
                startActivity(intentContact);
                finish();
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LaunchActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        if (selectedFragment!=null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
        }
        return true;
    }

    //update header
    public void  updateNavHeader(){

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername =  headerView.findViewById(R.id.usernameHeader);
        userID = firebaseAuth.getCurrentUser().getUid();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

      //fetch data from db

        DocumentReference documentReference = db.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    navUsername.setText(value.getString("Username"));

                }
            }
        });
    }
    public void  loginCounter(){
        userID = firebaseAuth.getCurrentUser().getUid();
        //register user in cloud firestore
         double[] loginCounter = new double[1];
        DocumentReference documentReference = db.collection("Users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){
                    loginCounter[0] =value.getDouble("Login");
                }
            }
        });
        Map<String,Object> user = new HashMap<>();
        user.put("Login",loginCounter[0]+=1);

        documentReference.set(user, SetOptions.merge());
    }


}