package com.example.agreducamandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertActivity extends AppCompatActivity {

    private TextView codeTextView;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;

    private double latitude;
    private double longitude;
    private int eventId = 0;
    private String scannedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        // Mantener la pantalla encendida, mostrarla incluso si está bloqueada
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Inicializar los componentes de la interfaz
        codeTextView = findViewById(R.id.codeTextView);
        Button correctButton = findViewById(R.id.correctButton);
        Button finishButton = findViewById(R.id.finishButton);
        Button emergencyButton = findViewById(R.id.reportButton);

        // Obtener el código escaneado desde el Intent
        scannedCode = getIntent().getStringExtra("SCANNED_CODE");
        Log.d("AlertActivity", "Código escaneado recibido: " + scannedCode);

        if (scannedCode != null) {
            codeTextView.setText("Número de Orden: " + scannedCode);
        }

        // Obtener la ubicación actual
        getLocation();

        // Activar la alarma
        activateAlarm();

        // Configuración de los botones
        correctButton.setOnClickListener(v -> handleCorrectButton());
        finishButton.setOnClickListener(v -> handleFinishButton());
        emergencyButton.setOnClickListener(v -> handleEmergencyButton());
    }

    private void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

        } catch (SecurityException e) {
            Log.e("AlertActivity", "Error al obtener la ubicación", e);
        }
    }

    private void activateAlarm() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "Error al cargar el sonido de alarma", Toast.LENGTH_SHORT).show();
            }

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000}, 0));
            }

            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlertActivity:WakeLock");
                wakeLock.acquire(10 * 60 * 1000L);
            }

        } catch (Exception e) {
            Log.e("AlertActivity", "Error al activar la alarma", e);
        }
    }

    private void handleCorrectButton() {
        stopAlarm();
        Toast.makeText(this, "Viaje Sin Complicaciones", Toast.LENGTH_SHORT).show();

        eventId = 2;
        getLocation();

        if (scannedCode == null || scannedCode.isEmpty()) {
            scannedCode = "Código no disponible";
        }

        startTask(eventId, scannedCode, latitude, longitude);

        Intent intent = new Intent(AlertActivity.this, CountdownActivity.class);
        intent.putExtra("SCANNED_CODE", scannedCode);
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        startActivity(intent);

        finish();
    }

    private void handleFinishButton() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Está seguro de que desea finalizar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    try {
                        stopAlarm(); // Detener la alarma
                        Toast.makeText(this, "Tarea Finalizada", Toast.LENGTH_SHORT).show();
                        eventId = 1; // Asignar el evento correspondiente

                        getLocation(); // Obtener la ubicación actual

                        // Actualizar en la API
                        startTask(eventId, scannedCode, latitude, longitude);

                        // Redirigir a LoginActivity
                        Intent intent = new Intent(AlertActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Finalizar la actividad actual

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ocurrió un error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void handleEmergencyButton() {
        stopAlarm(); // Detener la alarma
        Toast.makeText(this, "Emergencia activada para: " + scannedCode, Toast.LENGTH_SHORT).show();

        eventId = 3; // Asignar evento de emergencia

        getLocation(); // Obtener la ubicación actual

        // Actualizar la información en la API
        startTask(eventId, scannedCode, latitude, longitude);

        // Redirigir a LoginActivity antes de realizar la llamada
        redirectToLogin();

        // Solicitar permiso de llamada y realizar la llamada
        requestCallPermissionAndMakeCall();
    }

    private void requestCallPermissionAndMakeCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene el permiso, solicitarlo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2);
        } else {
            // Si ya se tiene el permiso, hacer la llamada
            makeEmergencyCall();
        }
    }

    private void makeEmergencyCall() {
        try {
            // Intent para realizar la llamada
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+56991733862")); // Número de emergencia
            startActivity(callIntent);
        } catch (Exception e) {
            // Manejo de excepción si la llamada no se puede realizar
            e.printStackTrace();
            Toast.makeText(this, "No se pudo realizar la llamada: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Método para redirigir a LoginActivity antes de realizar la llamada
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finaliza la actividad actual para evitar regresar a esta
    }



    private void startTask(int eventId, String scannedCode, double latitude, double longitude) {
        TaskRequest taskRequest = new TaskRequest(
                eventId,
                scannedCode,
                1,
                latitude,
                longitude,
                true
        );

        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);
        Call<ApiResponse> call = apiService.startTask(taskRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("AlertActivity", "Task started successfully");
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("AlertActivity", "Error starting task", t);
            }
        });
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}
