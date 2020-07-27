package com.example.serenazgo.proveedores;

import com.example.serenazgo.modelos.FCMBody;
import com.example.serenazgo.modelos.FCMResponse;
import com.example.serenazgo.retrofit.IFCMapi;
import com.example.serenazgo.retrofit.RetrofitPolicia;

import retrofit2.Call;

public class NotificacionProveedor {

    private  String url = "https://fcm.googleapis.com";

    public NotificacionProveedor(){

    }

    public Call<FCMResponse> sendNotificacion(FCMBody body){
        return RetrofitPolicia.getPoliciaObject(url).create(IFCMapi.class).send(body);
    }

}
