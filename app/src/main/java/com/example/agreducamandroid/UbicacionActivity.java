package com.example.agreducamandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class UbicacionActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private Button testLocationButton;
    private Button backButton;
    private LocationManager locationManager;
    private String scannedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        testLocationButton = findViewById(R.id.testLocationButton);
        backButton = findViewById(R.id.backButton);
        scannedCode = getIntent().getStringExtra("SCANNED_CODE");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        testLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
                return;
            }
            obtenerUbicacion();
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UbicacionActivity.this, CodeDisplayActivity.class);
            intent.putExtra("SCANNED_CODE", scannedCode);
            startActivity(intent);
            finish();
        });
    }

    private void obtenerUbicacion() {
        try {
            locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            latitudeTextView.setText("Latitud: " + latitude);
                            longitudeTextView.setText("Longitud: " + longitude);
                        }
                    },
                    null
            );
        } catch (SecurityException e) {
            Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al obtener la ubicación.", Toast.LENGTH_SHORT).show();
        }
    }
}
