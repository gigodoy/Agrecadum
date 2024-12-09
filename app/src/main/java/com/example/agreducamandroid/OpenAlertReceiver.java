package com.example.agreducamandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OpenAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Verificar si el action coincide con el que se ha enviado
        if ("com.example.agreducamandroid.OPEN_ALERT_ACTIVITY".equals(intent.getAction())) {
            Log.d("OpenAlertReceiver", "Broadcast recibido para abrir AlertActivity");

            // Crear un intent para abrir la AlertActivity
            Intent alertIntent = new Intent(context, AlertActivity.class);
            alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario para iniciar la actividad desde un servicio
            context.startActivity(alertIntent); // Inicia la AlertActivity
        }
    }
}
