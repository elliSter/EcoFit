package com.example.ecofit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view= inflater.inflate(R.layout.fragment_map, container, false);


        //initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //Async mao
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions=new MarkerOptions();

                        //set position off marfker
                        markerOptions.position(latLng);

                        markerOptions.title(latLng.latitude+" : "+latLng.longitude);

                        //rmv all markers
                        googleMap.clear();;

                        //zoom to marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng,10
                        ));

                        //add marker on map
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });

        return view;
    }
}