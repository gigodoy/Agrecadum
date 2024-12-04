package com.example.agreducamandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    private EditText orderNumberEditText;
    private Button searchOrderButton, scanButton, logoutButton;
    private TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        orderNumberEditText = findViewById(R.id.orderNumberEditText);
        searchOrderButton = findViewById(R.id.searchOrderButton);
        scanButton = findViewById(R.id.scanButton);
        logoutButton = findViewById(R.id.logoutButton);
        versionTextView = findViewById(R.id.versionTextView);

        // Cuando se hace clic en el botón de búsqueda de orden
        searchOrderButton.setOnClickListener(v -> {
            String orderNumber = orderNumberEditText.getText().toString().trim();
            if (!orderNumber.isEmpty()) {
                getTaskData(orderNumber);
            } else {
                Toast.makeText(DashboardActivity.this, "Por favor ingrese un número de orden", Toast.LENGTH_SHORT).show();
            }
        });

        // Cuando se hace clic en el botón de escaneo
        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);  // Para escanear el código
            startActivity(intent);
        });

        // Cuando se hace clic en el botón de cerrar sesión
        logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("auth_token");
            editor.apply();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(DashboardActivity.this, "Sesión Cerrada Correctamente", Toast.LENGTH_SHORT).show();
        });
    }

    // Método para obtener los detalles de la tarea utilizando Retrofit
    private void getTaskData(String orderNumber) {
        RetrofitClient.ApiService apiService = ApiClient.getClient().create(RetrofitClient.ApiService.class);

        Call<ApiResponse> call = apiService.getTaskDetails(orderNumber);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    // Después de obtener los datos, pasamos a CodeDisplayActivity
                    Intent intent = new Intent(DashboardActivity.this, CodeDisplayActivity.class);
                    intent.putExtra("SCANNED_CODE", orderNumber);  // Pasamos el código a CodeDisplayActivity
                    startActivity(intent);
                } else {
                    Toast.makeText(DashboardActivity.this, "No se pudieron obtener los detalles de la tarea.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
