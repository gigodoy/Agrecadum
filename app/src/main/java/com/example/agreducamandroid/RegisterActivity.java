package com.example.agreducamandroid;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, rutEditText, phoneEditText, emailEditText, passwordEditText;
    private Button registerButton,backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backToLoginButton = findViewById(R.id.backToLoginButton);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        rutEditText = findViewById(R.id.rutEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);


        backToLoginButton.setOnClickListener(v -> {
            // Redirige a la pantalla de LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Finaliza la actividad de registro
        });

        registerButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String rut = rutEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (fullName.isEmpty() || rut.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Crear el objeto RegisterRequest
                RegisterRequest registerRequest = new RegisterRequest(fullName, rut, phone, email, password);
                registerUser(registerRequest);
            }
        });
    }

    private void registerUser(RegisterRequest registerRequest) {
        RetrofitClient.ApiService apiService = RetrofitClient.getRetrofitInstance().create(RetrofitClient.ApiService.class);

        Call<RegisterResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    // Verifica si el registro fue exitoso
                    if ("success".equals(registerResponse.getStatus())) {
                        String token = registerResponse.getData().getUuid();  // Usa el "uuid" como identificador único
                        // Guardar el token o continuar con el flujo de la aplicación
                        saveTokenToSharedPreferences(token);

                        // Redirigir a la pantalla de inicio de sesión
                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish(); // Cerrar la actividad de registro
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si la respuesta no es exitosa
                    Toast.makeText(RegisterActivity.this, "Error al registrar: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Manejar el error de conexión
                Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Guardar el token en SharedPreferences
    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);  // Guardamos el token con la clave "auth_token"
        editor.apply();  // Guardamos los cambios
    }
}
