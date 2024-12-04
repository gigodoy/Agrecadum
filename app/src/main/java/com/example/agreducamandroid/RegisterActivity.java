package com.example.agreducamandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

        backToLoginButton = findViewById(R.id.backToLoginButton);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        rutEditText = findViewById(R.id.rutEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String rut = rutEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validaciones
            if (fullName.isEmpty() || fullName.length() < 10) {
                Toast.makeText(RegisterActivity.this, "Introducir nombre completo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidRut(rut)) {
                Toast.makeText(RegisterActivity.this, "El RUT debe contener el código verificador", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Número de teléfono válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(RegisterActivity.this, "Ingrese un email válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEmailAlreadyRegistered(email)) {
                Toast.makeText(RegisterActivity.this, "Email ya registrado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(RegisterActivity.this, "La contraseña debe contener solo 4 dígitos y no puede tener números consecutivos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el objeto RegisterRequest
            RegisterRequest registerRequest = new RegisterRequest(fullName, rut, phone, email, password);
            registerUser(registerRequest);
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

                    if ("success".equals(registerResponse.getStatus())) {
                        String token = registerResponse.getData().getUuid();
                        saveTokenToSharedPreferences(token);

                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish(); // Cerrar la actividad de registro
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error al registrar: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("RetrofitError", "Error de conexión: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);  // Guardamos el token con la clave "auth_token"
        editor.apply();  // Guardamos los cambios
    }

    // Validación para el RUT chileno
    private boolean isValidRut(String rut) {
        String rutPattern = "^([0-9]{7,8}-[0-9kK])$";
        Pattern pattern = Pattern.compile(rutPattern);
        Matcher matcher = pattern.matcher(rut);
        return matcher.matches();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isEmailAlreadyRegistered(String email) {
        return false;
    }

    // Validación para la contraseña (solo números, 4 dígitos y no consecutivos)
    private boolean isValidPassword(String password) {
        if (password.length() != 4 || !password.matches("[0-9]{4}")) {
            return false;
        }

        // Comprobar si los números son consecutivos
        for (int i = 0; i < 3; i++) {
            if (password.charAt(i) + 1 == password.charAt(i + 1) && password.charAt(i + 1) + 1 == password.charAt(i + 2) && password.charAt(i + 2) + 1 == password.charAt(i + 3)) {
                return false;
            }
        }

        return true;
    }
}
