package com.example.agreducamandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        Log.d("AlertActivity", "Código escaneado recibido: " + scannedCode); // Agrega este log

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
            // Verificar permisos antes de obtener la ubicación
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            // Intentar obtener la ubicación actual
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
            // Reproducir sonido de alarma
            mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "Error al cargar el sonido de alarma", Toast.LENGTH_SHORT).show();
            }

            // Iniciar vibración
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000}, 0));
            }

            // Mantener la pantalla encendida
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlertActivity:WakeLock");
                wakeLock.acquire(10 * 60 * 1000L); // Mantener el dispositivo encendido por 10 minutos
            }

        } catch (Exception e) {
            Log.e("AlertActivity", "Error al activar la alarma", e);
        }
    }

    private void handleCorrectButton() {
        stopAlarm();  // Detener la alarma
        Toast.makeText(this, "Viaje Sin Complicaciones", Toast.LENGTH_SHORT).show();

        eventId = 2;  // Event ID para "Viaje Sin Complicaciones"

        // Obtener la ubicación actual
        getLocation();

        // Log para verificar el valor de scannedCode antes de validarlo
        Log.d("penedol", "scannedCode antes de validación: " + scannedCode);

        // Verificar si scannedCode tiene un valor válido
        if (scannedCode == null || scannedCode.isEmpty()) {
            Log.d("basurero", "scannedCode es nulo o vacío");
            scannedCode = "Código no disponible";  // Asigna un valor predeterminado si no hay código
        }

        // Llamar a startTask para actualizar la tarea con los nuevos datos
        startTask(eventId, scannedCode, latitude, longitude);

        // Pasar los datos a CountdownActivity y reiniciar el contador
        Intent intent = new Intent(AlertActivity.this, CountdownActivity.class);
        intent.putExtra("SCANNED_CODE", scannedCode);  // Enviamos el código escaneado
        intent.putExtra("EVENT_ID", eventId);  // Pasamos solo eventId, latitud y longitud
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        startActivity(intent);  // Iniciar CountdownActivity

        // Agregar log para verificar los datos antes de pasar a CountdownActivity
        Log.d("colipato", "Código de tarea: " + scannedCode + ", Event ID: " + eventId + ", Latitud: " + latitude + ", Longitud: " + longitude);

        finish();  // Finalizar AlertActivity
    }

    private void handleFinishButton() {
        stopAlarm();
        Toast.makeText(this, "Tarea Finalizada", Toast.LENGTH_SHORT).show();
        eventId = 1;  // Event ID para "Tarea Finalizada"

        getLocation();
        startTask(eventId, scannedCode, latitude, longitude);  // Llamar a startTask con eventId 1

        Intent intent = new Intent(AlertActivity.this, LoginActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        startActivity(intent);
        finish();
    }

    private void handleEmergencyButton() {
        stopAlarm();
        Toast.makeText(this, "Emergencia activada para: " + scannedCode, Toast.LENGTH_SHORT).show();
        eventId = 3;  // Event ID para "Emergencia activada"

        getLocation();
        startTask(eventId, scannedCode, latitude, longitude);  // Llamar a startTask con eventId 3

        Intent intent = new Intent(AlertActivity.this, SplashActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        startActivity(intent);
        finish();
    }

    private void startTask(int eventId, String scannedCode, double latitude, double longitude) {
        // Crear el objeto TaskRequest con los datos necesarios
        TaskRequest taskRequest = new TaskRequest(
                eventId,         // event_id (según el botón presionado)
                scannedCode,     // order_number (código de la orden escaneado)
                1,               // order_type (orden de carga, se puede ajustar si es necesario)
                latitude,        // latitud obtenida
                longitude,       // longitud obtenida
                true             // status (estado, tarea activa)
        );

        // Llamada Retrofit
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
