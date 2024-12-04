package com.example.agreducamandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {

    private TextView codeTextView; // Declaramos el TextView
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        // Recuperar el código de la tarea desde el Intent
        String scannedCode = getIntent().getStringExtra("SCANNED_CODE"); // Recibir el código escaneado del Intent

        // Inicializar el TextView y configurar el texto con el código escaneado
        codeTextView = findViewById(R.id.codeTextView); // Asegúrate de que el ID coincida con el de tu XML
        if (scannedCode != null) {
            codeTextView.setText("Número de Orden: " + scannedCode);  // Mostrar el código escaneado
        }

        acquireWakeLock();

        Button correctButton = findViewById(R.id.correctButton);
        Button finishButton = findViewById(R.id.finishButton);

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "Error al cargar el sonido de alarma", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar el MediaPlayer: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000}, 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar la vibración", Toast.LENGTH_SHORT).show();
        }

        correctButton.setOnClickListener(v -> handleCorrectButton(scannedCode));
        finishButton.setOnClickListener(v -> handleFinishButton(scannedCode));
    }

    private void handleCorrectButton(String scannedCode) {
        stopAlarm();
        Toast.makeText(this, "Contador Reiniciado", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(AlertActivity.this, CountdownActivity.class);
        intent.putExtra("SCANNED_CODE", scannedCode); // Pasar el código al CountdownActivity
        startActivity(intent);
        finish();
    }

    private void handleFinishButton(String scannedCode) {
        stopAlarm();
        cancelAllAlarms();
        Toast.makeText(this, "Tarea: " + scannedCode + " Finalizada", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(AlertActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void stopAlarm() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (vibrator != null) {
                vibrator.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseWakeLock();
        }
    }

    private void cancelAllAlarms() {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(alarmIntent); // Cancela la alarma pendiente
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acquireWakeLock() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AgreducamAndroid:WakeLock");
                wakeLock.acquire(10 * 60 * 1000L ); // Mantener el teléfono despierto por 10 minutos
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Alarma activada", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseWakeLock() {
        try {
            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
}
