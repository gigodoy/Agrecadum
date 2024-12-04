package com.example.agreducamandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CountdownActivity extends AppCompatActivity {

    private TextView countdownTextView;
    private TextView orderNumberTextView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        // Obtener el código de tarea desde el Intent
        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");

        // Inicializar los TextViews
        countdownTextView = findViewById(R.id.countdownTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);

        // Mostrar el código de la tarea
        if (scannedCode != null) {
            orderNumberTextView.setText("Número de Orden: " + scannedCode);
            orderNumberTextView.setVisibility(TextView.VISIBLE); // Hacerlo visible
        }

        // Iniciar el contador
        startCountdown(scannedCode);
    }

    private void startCountdown(String scannedCode) {
        // Si el contador ya está en marcha, cancelarlo antes de reiniciarlo
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Configuración del contador de cuenta regresiva de 30 segundos
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTextView.setText("Tiempo restante: " + millisUntilFinished / 1000 + " segundos");
            }

            @Override
            public void onFinish() {
                // Al terminar el contador, pasa el código a AlertActivity
                Intent intent = new Intent(CountdownActivity.this, AlertActivity.class);
                intent.putExtra("SCANNED_CODE", scannedCode);  // Pasar el código de tarea a AlertActivity
                startActivity(intent);
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegurar que el contador se cancele si la actividad se destruye
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reiniciar el contador si la actividad vuelve a primer plano
        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");
        if (scannedCode != null) {
            startCountdown(scannedCode);
        }
    }
}
