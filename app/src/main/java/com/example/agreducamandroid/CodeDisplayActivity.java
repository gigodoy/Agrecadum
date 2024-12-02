package com.example.agreducamandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CodeDisplayActivity extends AppCompatActivity {
    private TextView codeTextView;
    private Button setAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_display);
        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");
        codeTextView = findViewById(R.id.codeTextView);
        setAlarmButton = findViewById(R.id.setAlarmButton);
        if (scannedCode != null) {
            codeTextView.setText("El Código es: " + scannedCode);
        } else {
            codeTextView.setText("No se encontró un código.");
        }
        setAlarmButton.setOnClickListener(v -> {
            setupAlarm();
            Toast.makeText(this, "Trabajo Iniciado Correctamente", Toast.LENGTH_SHORT).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            Intent intent = new Intent(CodeDisplayActivity.this, CountdownActivity.class);
            intent.putExtra("SCANNED_CODE", scannedCode);
            startActivity(intent);
        });
    }

    private void setupAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long interval = 30000;
        long triggerAtMillis = SystemClock.elapsedRealtime() + interval;

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis,
                    interval,
                    alarmIntent
            );
        }
    }
}
