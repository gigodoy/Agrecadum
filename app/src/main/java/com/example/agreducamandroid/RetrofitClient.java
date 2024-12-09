package com.example.agreducamandroid;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;

public class RetrofitClient {

    private static final String BASE_URL = "https://not-agr.bravoerp360.com";
    private static Retrofit retrofit;

    // Método para obtener la instancia de Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Crear el interceptor para el registro de logs
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar un cliente HTTP con logging y captura de respuestas
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        // Interceptor para capturar la respuesta cruda del servidor
                        okhttp3.Response response = chain.proceed(chain.request());

                        // Leer la respuesta cruda y registrarla
                        String rawResponse = response.body().string();
                        System.out.println("Respuesta cruda del servidor: " + rawResponse);

                        // Reconstruir la respuesta con el cuerpo original
                        return response.newBuilder()
                                .body(ResponseBody.create(response.body().contentType(), rawResponse))
                                .build();
                    })
                    .addInterceptor(chain -> {
                        // Interceptor para agregar las cabeceras Content-Type y Accept
                        okhttp3.Request original = chain.request();
                        okhttp3.Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            // Configuración de Gson con leniency habilitado
            Gson gson = new GsonBuilder()
                    .setLenient() // Permite JSON mal formado, habilitado explícitamente
                    .create();

            // Configuración de Retrofit con Gson leniente
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Usa Gson personalizado
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // Interfaz de API para definir los endpoints
    public interface ApiService {

        @POST("/api/v1/users/register")
        Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

        @POST("/api/v1/users/login")
        Call<LoginResponse> login(@Body LoginRequest body);

        // Método para iniciar una tarea (POST)
        @POST("/api/v1/alertas")
        Call<ApiResponse> startTask(@Body TaskRequest taskRequest);

        // Método para actualizar una alerta (PUT)
        @PUT("/api/v1/alertas")
        Call<Void> updateTask(@Body TaskUpdateRequest request);

        @GET("/api/v1/orders/{order_number}")
        Call<ApiResponse> getTaskDetails(@Path("order_number") String orderNumber);
    }
}
