package com.example.android.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient ourInstance = new RetrofitClient();

    public static RetrofitClient getInstance() {
        return ourInstance;
    }

    private RetrofitClient() {
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://13.124.111.245/android/")
            .addConverterFactory(GsonConverterFactory.create()) // 파싱등록
            .build();

    RetroService service = retrofit.create(RetroService.class);

    public RetroService getService() {
        return service;
    }

}
