package network;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpsManager {
    private static String accessToken;
    private static String refreshToken;
    private static Retrofit retrofit;

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        HttpsManager.accessToken = accessToken;
    }

    public static void setRefreshToken(String refreshToken) {
        HttpsManager.refreshToken = refreshToken;
    }

    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            retrofitInitialize(context);
        }
        return retrofit;
    }

    public static void retrofitInitialize(Context context) {
        try {
            OkHttpClient mOkhttpClient = getOkhttpManager(context);
            // 实例化Retrofit对象
            retrofit = new Retrofit.Builder()
                    .client(mOkhttpClient)
                    .baseUrl("https://39.107.92.179/")
                    // 使用Gson进行（反）序列化
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static OkHttpClient getOkhttpManager(Context context) throws IOException {
        InputStream inputStream = context.getAssets().open("certificate");
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        X509Certificate certificate = Certificates.decodeCertificatePem(result);
        HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                .addTrustedCertificate(certificate)
                .build();
        return new OkHttpClient.Builder()
                .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                .build();
    }
}
