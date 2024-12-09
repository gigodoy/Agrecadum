package com.example.agreducamandroid;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class CountdownService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_TIME = 5000; // 5 segundos
    private static final long COUNTDOWN_INTERVAL = 1000; // 1 segundo

    // Variables para almacenar la información de la API
    private int event_id;
    private String code;
    private double latitude;
    private double longitude;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Crear la notificación para el servicio en primer plano
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Contador Activo")
                .setContentText("El contador está en ejecución...")
                .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un ícono en drawable
                .build();

        // Iniciar el servicio en primer plano con la notificación
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // Obtener los datos pasados al servicio
            event_id = intent.getIntExtra("event_id", 0);
            code = intent.getStringExtra("code");
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);

            // Iniciar el contador
            startCountdown();
        }

        return START_STICKY;
    }

    private void createNotificationChannel() {
        // Crear canal de notificación solo para Android O y versiones superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Contador Servicio",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startCountdown() {
        // Iniciar un contador con los parámetros definidos
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Este método se llama en cada intervalo de tiempo del contador
                // Puedes actualizar una notificación o hacer algo mientras el contador está activo
            }

            @Override
            public void onFinish() {
                // Cuando el contador termine, abrir la vista de alerta
                Intent intent = new Intent(CountdownService.this, AlertActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // Importante para iniciar actividad desde servicio

                // Pasar los datos a AlertActivity para que pueda modificarlos
                intent.putExtra("event_id", event_id);
                intent.putExtra("code", code);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        };
        countDownTimer.start();  // Iniciar el contador
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // Este servicio no se va a vincular con ninguna actividad
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancelar el contador cuando el servicio se destruya
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
