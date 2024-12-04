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

    private TextView movementTypeTextView;
    private TextView orderNumberTextView;
    private TextView tractorTrailerTextView;
    private Button startTaskButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_display);

        movementTypeTextView = findViewById(R.id.movementTypeTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);
        tractorTrailerTextView = findViewById(R.id.tractorTrailerTextView);
        startTaskButton = findViewById(R.id.startTaskButton);
        cancelButton = findViewById(R.id.cancelButton);

        String scannedCode = getIntent().getStringExtra("SCANNED_CODE");

        if (scannedCode != null) {
            getTaskData(scannedCode);
        }

        startTaskButton.setOnClickListener(v -> {
            startTask(scannedCode);
            Intent intent = new Intent(CodeDisplayActivity.this, CountdownActivity.class);
            startActivity(intent);
        });

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
        String taskType = "Tipo de Movimiento";
        String driverName = "Nombre del Conductor";
        ApiRequest apiRequest = new ApiRequest(scannedCode, taskType, driverName);

        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);
        Call<ApiResponse> call = apiService.startTask(apiRequest);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CodeDisplayActivity.this, "Tarea iniciada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CodeDisplayActivity.this, "No se pudo iniciar la tarea.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CodeDisplayActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
