package com.example.agreducamandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeDisplayActivity extends AppCompatActivity {
    private TextView movementTypeTextView, driverNameTextView, orderNumberTextView, tractorTrailerTextView;
    private Button startTaskButton, cancelButton;
    private String scannedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_display);

        // Inicialización de vistas
        driverNameTextView = findViewById(R.id.driverNameTextView);
        movementTypeTextView = findViewById(R.id.movementTypeTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);
        tractorTrailerTextView = findViewById(R.id.tractorTrailerTextView);
        startTaskButton = findViewById(R.id.startTaskButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Obtener el código escaneado de la intención
        scannedCode = getIntent().getStringExtra("SCANNED_CODE");

        // Llamar a la API para obtener detalles de la tarea si se pasó el código
        if (scannedCode != null) {
            fetchTaskDetails(scannedCode);
        }

        // Botón para iniciar tarea
        startTaskButton.setOnClickListener(v -> {
            if (scannedCode != null) {
                // Solicitar permisos y obtener ubicación antes de iniciar tarea
                requestLocationPermissionAndStartTask();
            } else {
                Toast.makeText(this, "Error: Código escaneado no disponible.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para cancelar y volver al dashboard
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(CodeDisplayActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Método para obtener detalles de la tarea
    private void fetchTaskDetails(String code) {
        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);
        Call<ApiResponse> call = apiService.getTaskDetails(code);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse.Data data = response.body().getData();
                    driverNameTextView.setText(data.getConductor());
                    movementTypeTextView.setText(data.getTipoMovimiento());
                    orderNumberTextView.setText(data.getNumeroOrden());
                    tractorTrailerTextView.setText(data.getPatenteTracto() + " " + data.getPatenteRemolque());
                } else {
                    Toast.makeText(CodeDisplayActivity.this, "No se encontraron detalles para este código.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CodeDisplayActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }

    // Método para solicitar permisos de ubicación y luego iniciar la tarea
    private void requestLocationPermissionAndStartTask() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocationAndStartTask(); // Llamar a la función para obtener la ubicación y continuar con la tarea
        }
    }

    // Método para obtener la ubicación y luego iniciar la tarea
    private void getLocationAndStartTask() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double latitude = location != null ? location.getLatitude() : 0.0;
        double longitude = location != null ? location.getLongitude() : 0.0;

        // Iniciar la tarea con la ubicación
        if (scannedCode != null) {
            startTask(scannedCode, latitude, longitude);
        }
    }

    // Método para iniciar la tarea
    private void startTask(String code, double latitude, double longitude) {
        int eventId = 0;  // Ajusta este valor según tu lógica

        // Crear el objeto TaskRequest con los datos necesarios
        TaskRequest taskRequest = new TaskRequest(
                eventId,        // event_id (Inicio de viaje)
                code,           // order_number (código de la orden)
                1,              // order_type (orden de carga)
                latitude,       // latitud obtenida
                longitude,      // longitud obtenida
                true            // status (estado, tarea activa)
        );

        // Llamada Retrofit
        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);
        Call<ApiResponse> call = apiService.startTask(taskRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("API_RESPONSE", "Código de estado: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CodeDisplayActivity.this, "Tarea iniciada correctamente", Toast.LENGTH_SHORT).show();

                    // Iniciar la actividad CountdownActivity
                    Intent activityIntent = new Intent(CodeDisplayActivity.this, CountdownActivity.class);
                    activityIntent.putExtra("SCANNED_CODE", scannedCode);
                    activityIntent.putExtra("EVENT_ID", eventId);
                    activityIntent.putExtra("LATITUDE", latitude);
                    activityIntent.putExtra("LONGITUDE", longitude);
                    startActivity(activityIntent); // Esto iniciará la nueva actividad
                    finish(); // Finalizar la actividad actual si no deseas que se pueda volver a ella
                } else {
                    Toast.makeText(CodeDisplayActivity.this, "No se pudo iniciar la tarea. Código de respuesta: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CodeDisplayActivity.this, "Error al iniciar la tarea: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }
}
