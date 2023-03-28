package com.example.mobileyolox.api;

import static com.example.mobileyolox.ScanQrApiActivity.SCAN_DOMAIN;

import com.example.mobileyolox.model.APIOutput;
import com.example.mobileyolox.model.TestAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    // https://5e19-14-161-13-207.ap.ngrok.io/api/detectUpload


    public static final String DOMAIN = SCAN_DOMAIN;

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // Increase the timeout to 60 seconds
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("/")
    Call<TestAPI> testResponse();

    @Multipart
    @POST("detect")
    Call<APIOutput> sendImageFile(@Part MultipartBody.Part file);



}
