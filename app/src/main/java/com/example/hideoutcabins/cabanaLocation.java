package com.example.hideoutcabins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hideoutcabins.pojo.Cabin;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cabanaLocation extends AppCompatActivity implements OnMapReadyCallback {
    Button dtnnext;
    EditText txtlat,txtlon;

    String Hname, Aaddress,Emails,TeLphon,Password,Numofromms,SingleRoomPr,DouBleRoomPr;
    DatabaseReference referece;
    long maxId = 0;
    Cabin cabin;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    private boolean locationPermissionGranted = false;



    Marker marker;
    GoogleMap TgoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabana_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locater);
        mapFragment.getMapAsync(this);

        dtnnext = findViewById(R.id.btnNext);
        txtlat = findViewById(R.id.txtlat);
        txtlon = findViewById(R.id.txtlon);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        Intent intent = getIntent();
        Hname = intent.getStringExtra("hotelNae") ;
        Aaddress = intent.getStringExtra("adress") ;
        Emails = intent.getStringExtra("eMmail") ;
        TeLphon = intent.getStringExtra("tElNumber") ;
        Password = intent.getStringExtra("paSsword") ;
        Numofromms = intent.getStringExtra("Numofromms") ;
        SingleRoomPr = intent.getStringExtra("singleRoomPr") ;;
        DouBleRoomPr = intent.getStringExtra("DouBleRoomPr") ;


        referece = FirebaseDatabase.getInstance().getReference().child("cabana");
        referece.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    maxId  = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        cabin = new Cabin();
        dtnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cabin.setAddress(Aaddress);
                cabin.setEmail(Emails);
                cabin.setTp(TeLphon);
                cabin.setPasword(Password);
                cabin.setRoom_Single_Price(Double.valueOf(SingleRoomPr));
                cabin.setRoom_Double_Price(Double.valueOf(DouBleRoomPr));
                cabin.setName(Hname);
                cabin.setLocation_lat(Double.valueOf(txtlat.getText().toString()));
                cabin.setLocation_lon(Double.valueOf(txtlon.getText().toString()));


                Long tsLong = System.currentTimeMillis() / 1000;
                String id = tsLong.toString();

                referece.child("C"+id).setValue(cabin);

                Toast.makeText(cabanaLocation.this,"data added successfully",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(cabanaLocation.this,addPhotos.class);
                intent.putExtra("CabanaID",id);
                startActivity(intent);

            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        TgoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(cabanaLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(cabanaLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                LatLng latLng = new LatLng(lat,lon);
                MarkerOptions options = new MarkerOptions().position(latLng).draggable(true);
                marker=TgoogleMap.addMarker(options);
                CameraPosition colombo = CameraPosition.builder().target(latLng).zoom(15).bearing(0).tilt(45).build();
                TgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));


                txtlat.setText(String.valueOf(lat));
                txtlon.setText(String.valueOf(lon));

                Log.d("latLng", String.valueOf(latLng.latitude));
                Log.d("latLng", String.valueOf(latLng.longitude));

            }
        });

        TgoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d("setOnMarkerDragListener", "onMarkerDragStart");

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.d("setOnMarkerDragListener", "onMarkerDrag");

                txtlat.setText(String.valueOf(marker.getPosition().latitude));
                txtlon.setText(String.valueOf(marker.getPosition().longitude));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("setOnMarkerDragListener", "onMarkerDragEnd");

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }

                    locationPermissionGranted = true;

                }
            }
        }
    }

    private void getLocationPermission() {

        Log.d("sdasd", "grtLocationPermission: getting LOcation permission");

        String[] permsission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationPermissionGranted = true;
                return;

            } else {
                ActivityCompat.requestPermissions(this, permsission, LOCATION_PERMISSION_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(cabanaLocation.this, permsission, LOCATION_PERMISSION_REQUEST);
        }
    }




    public void onclick(View view){
        Intent intent = new Intent(cabanaLocation.this,addPhotos.class);
        startActivity(intent);
    }
}
