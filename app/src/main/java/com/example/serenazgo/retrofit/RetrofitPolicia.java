package com.example.serenazgo.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitPolicia {

    public static Retrofit getPolicia(String url){

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        return retrofit;

    }

    public static Retrofit getPoliciaObject(String url){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofit;

    }

}
