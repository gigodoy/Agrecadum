package com.example.agreducamandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeDisplayActivity extends AppCompatActivity {

    private TextView movementTypeTextView, driverNameTextView;
    private TextView orderNumberTextView;
    private TextView tractorTrailerTextView;
    private Button startTaskButton;
    private Button cancelButton;

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
        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");

        if (scannedCode != null) {
            getTaskData(scannedCode);
        }

        // Acción al presionar el botón de inicio de tarea
        startTaskButton.setOnClickListener(v -> {
            if (scannedCode != null) {
                startTask(scannedCode);

                // Pasar el código a CountdownActivity
                Intent intent = new Intent(CodeDisplayActivity.this, CountdownActivity.class);
                intent.putExtra("SCANNED_CODE", scannedCode);  // Pasamos el código a CountdownActivity
                startActivity(intent);
            } else {
                Toast.makeText(CodeDisplayActivity.this, "Error: El código de orden no está disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción al presionar el botón de cancelar
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(CodeDisplayActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getTaskData(String scannedCode) {
        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);
        Call<ApiResponse> call = apiService.getTaskDetails(scannedCode);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    driverNameTextView.setText(apiResponse.getData().getConductor());
                    movementTypeTextView.setText(apiResponse.getData().getTipoMovimiento());
                    orderNumberTextView.setText(apiResponse.getData().getNumeroOrden());
                    tractorTrailerTextView.setText(apiResponse.getData().getPatenteTracto() + "   " + apiResponse.getData().getPatenteRemolque());
                } else {
                    Toast.makeText(CodeDisplayActivity.this, "No se pudieron obtener los detalles de la tarea.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CodeDisplayActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTask(String scannedCode) {
        // Datos de ejemplo (ajusta según tus requerimientos)
        int event_id = 123;                 // Identificador del evento
        String order_number = scannedCode;  // Usar el código escaneado como número de orden
        int order_type = 1;                 // Tipo de orden
        double lat = 10.12345;              // Latitud (puedes usar GPS para valores reales)
        double lng = -84.12345;             // Longitud
        boolean status = true;              // Estado inicial de la tarea

        // Crear el objeto de solicitud TaskRequest con los datos adecuados
        TaskRequest taskRequest = new TaskRequest(event_id, order_number, order_type, lat, lng, status);

        // Llamar a la API mediante Retrofit
        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);

        // Realizamos la llamada a la API pasando el objeto taskRequest
        Call<Void> call = apiService.startTask(taskRequest);

        call.enqueue(new Callback<Void>() {  // Usamos Callback<Void> porque no esperamos un cuerpo en la respuesta
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CodeDisplayActivity.this, "Tarea " + order_number + " iniciada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CodeDisplayActivity.this, "No se pudo iniciar la tarea. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CodeDisplayActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
