package com.example.serenazgo.retrofit;

import com.example.serenazgo.modelos.FCMBody;
import com.example.serenazgo.modelos.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMapi {

    @Headers({
            "Content-type:application/json",
            "Authorization:key=AAAARBDX224:APA91bFIBqqdixG8bjL2EYoctQNhdbA7GFLlUnUVEZOYtxgHJzER5YskcBYWVBZhzqJeR2L4u_RX3OA8N_jxNDpfcAav1VT2SeqpxkwYQk-5kmZ1zTkIDaBRL7zARmE80Frn_MWPej_P"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);

}
