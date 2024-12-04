package com.example.agreducamandroid;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class RetrofitClient {

    private static final String BASE_URL = "https://not-agr.bravoerp360.com/api/v1/";
    private static Retrofit retrofit;

    // Método para obtener la instancia de Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Crear el interceptor para el registro de logs
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Crear el cliente HTTP y agregar el interceptor de logs
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
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

            // Configuración de Retrofit con GsonConverter para manejar la deserialización de JSON
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())  // Gson para la conversión de JSON
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public interface ApiService {
        @GET("orders/{order_number}")
        Call<ApiResponse> getTaskDetails(@Path("order_number") String orderNumber);

        @POST("alertas")
        Call<Void> startTask(@Body TaskRequest taskRequest);

        @POST("users/register")
        Call<RegisterResponse> register(@Body RegisterRequest body);

        @POST("users/login")
        Call<LoginResponse> login(@Body LoginRequest body);
    }
}
