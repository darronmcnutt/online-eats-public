package csc472.depaul.edu.onlineeats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LocationActivity extends AppCompatActivity {

    private final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Button startLocationButton = findViewById(R.id.start_location_button);
        Button stopLocationButton = findViewById(R.id.stop_location_button);
        Button openMapButton = findViewById(R.id.open_map_button);

        startLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int locationPermission = ActivityCompat.checkSelfPermission(getLocationActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                if (locationPermission == PackageManager.PERMISSION_GRANTED) {
                    startLocationTransmission();
                } else {

                    String[] PERMISSIONS_LOCATION = {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    };

                    ActivityCompat.requestPermissions(
                            getLocationActivity(),
                            PERMISSIONS_LOCATION,
                            REQUEST_LOCATION);
                }
            }
        });

        stopLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationTransmission();
            }
        });

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapsActivity();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       if (requestCode == REQUEST_LOCATION && grantResults.length == 1
               && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           startLocationTransmission();
       } else {
           Toast.makeText(this, "Location services must be enabled for location transmission", Toast.LENGTH_SHORT).show();
       }
    }

    private void startLocationTransmission() {
        Intent locationTransmissionIntent = new Intent(getLocationActivity(), LocationTransmissionService.class);
        startService(locationTransmissionIntent);
        Toast.makeText(this, "Location transmission in progress", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationTransmission() {
        Intent locationTransmissionIntent = new Intent(getLocationActivity(), LocationTransmissionService.class);
        stopService(locationTransmissionIntent);
        Toast.makeText(this, "Location transmission stopped", Toast.LENGTH_SHORT).show();
    }

    private void startMapsActivity() {
        Intent mapsIntent = new Intent(getLocationActivity(), MapsActivity.class);
        startActivity(mapsIntent);
    }

    private LocationActivity getLocationActivity() { return this; }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
