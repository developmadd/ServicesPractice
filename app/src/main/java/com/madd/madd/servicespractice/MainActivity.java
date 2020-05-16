package com.madd.madd.servicespractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    GoogleMap map;
    Button buttonMusic;
    CheckBox checkBoxMusic;
    Button buttonMusicIService;
    Button buttonLocation;
    Button buttonLongTask;
    EditText editTextLongTask;

    Intent intentServiceMusic;
    Intent serviceMusic;
    FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMusic = findViewById(R.id.BTN_Music);
        buttonMusicIService = findViewById(R.id.BTN_Music_Intent_Service);
        buttonLocation = findViewById(R.id.BTN_Location);
        checkBoxMusic = findViewById(R.id.CHK_Music);
        buttonLongTask = findViewById(R.id.BTN_Long_Task);
        editTextLongTask = findViewById(R.id.ET_Long_Task);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });



        // Start a music service this is executed on main thread and is possible to stop it
        // when activity also stop
        buttonMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonMusic.getText().equals("Comenzar") ) {
                    serviceMusic = new Intent(getApplicationContext(), ServiceMusic.class);
                    serviceMusic.putExtra("Bruno","Salas");
                    startService(serviceMusic);
                    serviceMusic.putExtra("Bruno","Sandoval");
                    startService(serviceMusic);
                    serviceMusic.putExtra("Bruno","Solis");
                    startService(serviceMusic);
                    buttonMusic.setText("Detener");
                } else {
                    stopService(serviceMusic);
                    serviceMusic = null;
                    buttonMusic.setText("Comenzar");
                }
            }
        });


        // Start a music service this is executed on main thread and is possible to stop it
        // when activity also stop
        buttonMusicIService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonMusicIService.getText().equals("Comenzar") ) {
                    intentServiceMusic = new Intent(getApplicationContext(), ServiceMusicIntent.class);
                    intentServiceMusic.putExtra("Bruno","Salas");
                    startService(intentServiceMusic);
                    intentServiceMusic.putExtra("Bruno","Sandoval");
                    startService(intentServiceMusic);
                    intentServiceMusic.putExtra("Bruno","Solis");
                    startService(intentServiceMusic);
                    buttonMusicIService.setText("Detener");
                } else {
                    stopService(intentServiceMusic);
                    intentServiceMusic = null;
                    buttonMusicIService.setText("Comenzar");
                }
            }
        });

        //Start a long task that sleep a thread for n seconds(duration). This service is executed in a second thread.
        buttonLongTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editTextLongTask.getText().toString().isEmpty()) {
                    Intent intentLongTask = new Intent(getApplicationContext(), ServiceCounter.class);
                    intentLongTask.putExtra("duration", Integer.valueOf(editTextLongTask.getText().toString()));
                    startService(intentLongTask);
                }
            }
        });

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonLocation.getText().equals("Comenzar") ) {
                    buttonLocation.setText("Detener");
                    LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult != null) {
                                if (locationResult.getLastLocation() != null) {

                                    Log.i("LOCATION", getDate(locationResult.getLastLocation().getTime()));
                                    if( !onBackground ) {
                                        Log.i("LOCATION", "Reported at map");
                                        showLocationAtMap(locationResult);
                                    }


                                }
                            }
                        }
                    };

                    fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper());
                } else {
                    buttonLocation.setText("Comenzar");
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }

            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().
                addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if( !task.isSuccessful() ){
                    return;
                }
                String token = task.getResult().getToken();
                Log.d("TAG", token);
            }
        });




    }


    private LocationCallback locationCallback;
    private boolean onBackground = false;


    private void showLocationAtMap(LocationResult locationResult){
        LatLng latLng = new LatLng(
                locationResult.getLastLocation().getLatitude(),
                locationResult.getLastLocation().getLongitude());
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }
    private String getDate(long time){
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentTimeZone = new Date(time);
        return sdf.format(currentTimeZone);
    }






    // Register broadcast receiver when the app is on foreground
    // Check if the music service is created, restart
    @Override
    protected void onResume() {
        super.onResume();
        if( serviceMusic != null ) {
            startService(serviceMusic);
        }
        onBackground = false;
    }


    // Unregister broadcast receiver when the app is on background
    // Check if the music service is created and user decides to stop service when activities does
    @Override
    protected void onPause() {
        super.onPause();
        if( serviceMusic != null && checkBoxMusic.isChecked() ) {
            stopService(serviceMusic);
        }
        onBackground = true;
    }


}
