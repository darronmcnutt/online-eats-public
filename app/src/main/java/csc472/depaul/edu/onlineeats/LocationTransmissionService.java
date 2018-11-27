package csc472.depaul.edu.onlineeats;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.ValueEventListener;

public class LocationTransmissionService extends Service {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private DatabaseReference mDriverRef;
    private DatabaseReference mCustomerLocRef;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private final String TAG = "LOCATION_TRANSMISSION";

    public LocationTransmissionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDriverRef = mDatabase.getReference("drivers/");
        mCustomerLocRef = mDatabase.getReference("customers/" + mUser.getUid() + "/location");

        // If current user is a driver, push location to drivers collection
        ValueEventListener driverListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mUser.getUid())) {
                    Log.d(TAG, "Driver exists. Setting mLocationCallback to push location to drivers collection");
                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            Location location = locationResult.getLastLocation();
                            if (location != null) {
                                mDriverRef.child(mUser.getUid()).setValue(location);
                            }
                        }
                    };
                } else {
                    // If current user is a customer, push location to customers collection
                    Log.d(TAG, "Driver does not exist. Setting mLocationCallback to push location to customers collection");
                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            Location location = locationResult.getLastLocation();
                            if (location != null) {
                                mCustomerLocRef.setValue(location);
                            }
                        }
                    };
                }

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getLocationTransmissionService());
                transmitLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDriverRef.addListenerForSingleValueEvent(driverListener);

    }

    private void transmitLocation() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,null);
        }
    }

    private LocationTransmissionService getLocationTransmissionService() { return this; }

    @Override
    public void onDestroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }
}
