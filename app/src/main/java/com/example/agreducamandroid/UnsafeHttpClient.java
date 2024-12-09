package com.example.agreducamandroid;

import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class UnsafeHttpClient {

    // Método para obtener un OkHttpClient inseguro
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Crea un TrustManager que no verifica certificados
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            // No hace nada
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            // No hace nada
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                        }
                    }
            };

            // Crea un contexto SSL con el TrustManager que no valida certificados
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Configura el OkHttpClient para usar SSL inseguro
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true) // Acepta cualquier hostname
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))  // Agrega logging para las peticiones
                    .build();

        } catch (Exception e) {
            // En caso de que haya un error en la configuración del cliente SSL
            throw new RuntimeException("Error al configurar el cliente HTTP inseguro", e);
        }
    }
}
