package com.example.agreducamandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, rutEditText, phoneEditText, emailEditText, passwordEditText;
    private Button registerButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar vistas
        fullNameEditText = findViewById(R.id.fullNameEditText);
        rutEditText = findViewById(R.id.rutEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        // Navegar a la pantalla de login
        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Configurar acción del botón de registro
        registerButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String rut = rutEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validar campos
            if (validateInputs(fullName, rut, phone, email, password)) {
                RegisterRequest registerRequest = new RegisterRequest(fullName, rut, phone, email, password);
                registerUser(registerRequest);
            }
        });
    }

    private boolean validateInputs(String fullName, String rut, String phone, String email, String password) {
        if (fullName.isEmpty() || rut.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() != 9) {
            Toast.makeText(this, "El teléfono debe tener exactamente 9 dígitos.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, ingrese un correo electrónico válido.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser(RegisterRequest registerRequest) {
        RetrofitClient.ApiService apiService = RetrofitClient.getRetrofitInstance().create(RetrofitClient.ApiService.class);

        Call<RegisterResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    if ("success".equals(registerResponse.getStatus())) {
                        String token = registerResponse.getData().getToken();
                        saveTokenToSharedPreferences(token);

                        Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 422) {
                    handleValidationErrors(response);
                } else {
                    Toast.makeText(RegisterActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("RetrofitError", "Error de conexión: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleValidationErrors(Response<RegisterResponse> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                JSONObject jsonObject = new JSONObject(errorBody);
                String message = jsonObject.getString("message");

                JSONObject errors = jsonObject.getJSONObject("errors");
                StringBuilder errorMessages = new StringBuilder();

                // Iterar sobre las claves de 'errors'
                Iterator<String> keys = errors.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    // Obtener el primer mensaje de error para cada campo
                    String errorMessage = errors.getJSONArray(key).getString(0);
                    errorMessages.append(key).append(": ").append(errorMessage).append("\n");
                }

                Toast.makeText(this, message + "\n" + errorMessages, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("ValidationError", "Error al procesar los errores: " + e.getMessage());
        }
    }


    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }
}
