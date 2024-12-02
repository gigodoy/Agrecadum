package com.example.agreducamandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.os.PowerManager;
import androidx.appcompat.app.AppCompatActivity;

public class CountdownActivity extends AppCompatActivity {

    private TextView countdownTextView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);
        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");

        countdownTextView = findViewById(R.id.countdownTextView);
        startAlarmService();
        startCountdown(scannedCode);
    }

    private void startCountdown(String scannedCode) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

            countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTextView.setText("Tiempo restante: " + millisUntilFinished / 1000 + " segundos");
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(CountdownActivity.this, AlertActivity.class);
                intent.putExtra("SCANNED_CODE", scannedCode);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void startAlarmService() {
        Intent serviceIntent = new Intent(this, AlarmService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
