package com.example.serenazgo.proveedores;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class geofireProvider {

    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public geofireProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("conductores_activos");
        mGeofire = new GeoFire(mDatabase);

    }

    public void guardar_localizacion(String idCamion , LatLng latLng){

        mGeofire.setLocation(idCamion,new GeoLocation(latLng.latitude,latLng.longitude));
    }

    public void borrar_localizacion(String idCamion){
        mGeofire.removeLocation(idCamion);
    }

    public GeoQuery getActiveCamioneta(LatLng latLng,double radius){
        GeoQuery geoQuery = mGeofire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),radius);
        geoQuery.removeAllListeners();
        return geoQuery;
    }

}
