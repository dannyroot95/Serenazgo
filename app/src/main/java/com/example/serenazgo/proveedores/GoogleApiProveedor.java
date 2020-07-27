package com.example.serenazgo.proveedores;

import android.content.Context;

import com.example.serenazgo.R;
import com.example.serenazgo.retrofit.IGoogleApi;
import com.example.serenazgo.retrofit.RetrofitPolicia;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import retrofit2.Call;

public class GoogleApiProveedor {

    private Context context;

    public GoogleApiProveedor(Context context){
        this.context = context;
    }

    public Call<String> getDirections(LatLng origenLatLng , LatLng destinoLatLng){

        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + origenLatLng.latitude + "," + origenLatLng.longitude + "&"
                + "destination=" + destinoLatLng.latitude + "," + destinoLatLng.longitude + "&"
                + "departure_time=" + (new Date().getTime() + (60*60*1000)) + "&"
                + "traffic_model=best_guess&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);

        return RetrofitPolicia.getPolicia(baseUrl).create(IGoogleApi.class).getDirections(baseUrl+query);

    }

}
