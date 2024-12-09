package com.example.agreducamandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CountdownActivity extends AppCompatActivity {

    private TextView countdownTextView;
    private TextView orderNumberTextView;

    private String scannedCode;
    private int eventId;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        // Inicializar los TextViews
        countdownTextView = findViewById(R.id.countdownTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);

        // Obtener los datos desde el Intent
        scannedCode = getIntent().getStringExtra("SCANNED_CODE");
        eventId = getIntent().getIntExtra("EVENT_ID", 0);  // Obtener el eventId (por defecto 0)
        latitude = getIntent().getDoubleExtra("LATITUDE", 0.0);  // Obtener la latitud
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);  // Obtener la longitud

        // Mostrar el código de la tarea y el eventId si están disponibles
        if (scannedCode != null) {
            orderNumberTextView.setText("Número de Orden: " + scannedCode);
            orderNumberTextView.setVisibility(TextView.VISIBLE); // Asegurarse de que esté visible
        } else {
            orderNumberTextView.setVisibility(TextView.GONE);  // Si no hay código, ocultarlo
        }

        // Iniciar el contador con los nuevos valores (tiempo de cuenta regresiva 30 segundos)
        startCountdown(30000);  // 30 segundos
    }

    private void startCountdown(long millisInFuture) {
        new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Mostrar el tiempo restante en el TextView
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                countdownTextView.setText("Tiempo restante: " + secondsRemaining + " segundos");
            }

            @Override
            public void onFinish() {
                // Al finalizar la cuenta regresiva, iniciar la actividad AlertActivity
                Intent alertIntent = new Intent(CountdownActivity.this, AlertActivity.class);
                alertIntent.putExtra("SCANNED_CODE", scannedCode);
                alertIntent.putExtra("EVENT_ID", eventId);  // Pasar el nuevo eventId
                alertIntent.putExtra("LATITUDE", latitude);  // Pasar la nueva latitud
                alertIntent.putExtra("LONGITUDE", longitude);  // Pasar la nueva longitud
                startActivity(alertIntent);
                finish();  // Finalizar CountdownActivity para que no se quede en el stack
            }
        }.start();
    }
}
