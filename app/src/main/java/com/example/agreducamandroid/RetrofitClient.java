package com.example.agreducamandroid;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
public class RetrofitClient {

    private static final String BASE_URL = "https://not-agr.bravoerp360.com/api/v1/";
    private static Retrofit retrofit;
    // Método para obtener la instancia de Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    public interface ApiService {

        @GET("orders/{order_number}")
        Call<ApiResponse> getTaskDetails(@Path("order_number") String orderNumber);

        @POST("tasks/start")
        Call<ApiResponse> startTask(@Body ApiRequest body);

        @POST("users/register")
        Call<RegisterResponse> register(@Body RegisterRequest body);

        @POST("users/login")
        Call<LoginResponse> login(@Body LoginRequest body);

    }
}
