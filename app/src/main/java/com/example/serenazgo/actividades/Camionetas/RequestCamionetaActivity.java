package com.example.serenazgo.actividades.Camionetas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.serenazgo.R;
import com.example.serenazgo.Utils.DecodePoints;
import com.example.serenazgo.actividades.Policias.detailRequestActivity;
import com.example.serenazgo.modelos.FCMBody;
import com.example.serenazgo.modelos.FCMResponse;
import com.example.serenazgo.modelos.PoliciaBooking;
import com.example.serenazgo.proveedores.GoogleApiProveedor;
import com.example.serenazgo.proveedores.NotificacionProveedor;
import com.example.serenazgo.proveedores.PoliciaBookingProveedor;
import com.example.serenazgo.proveedores.TokenProveedor;
import com.example.serenazgo.proveedores.authProveedores;
import com.example.serenazgo.proveedores.geofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.AuthProvider;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestCamionetaActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTextviewCamioneta;
    private Button mButtonCancelRequest;
    private geofireProvider mGeofireProvider;
    private double mExtraOrigenLat , mExtraOrigenLng ;
    private double mExtraDestinoLat , mExtraDestinoLng ;
    private String mExtraOrigen , mExtraDestino;
    private LatLng mOrigenLatLng;
    private LatLng mDestinoLatLng;
    private double mRadius = 0.1;
    private boolean mPatrullaFound = false;
    private String mIDPatrullaFound = "";
    private LatLng mPatrullaFoundLatLng;
    private NotificacionProveedor mNotificacionProveedor;
    private TokenProveedor mTokenProveedor;
    private PoliciaBookingProveedor mPoliciaBookingProveedor;
    private authProveedores mAuthProveedores;
    private GoogleApiProveedor mGoogleApiProveedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_camioneta);

        mAnimation = findViewById(R.id.animation_patrulla);
        mTextviewCamioneta = findViewById(R.id.textviewLookingPatrulla);
        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);

        mExtraOrigenLat = getIntent().getDoubleExtra("origen_lat",0);
        mExtraOrigenLng = getIntent().getDoubleExtra("origen_lng",0);
        mExtraDestinoLat = getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLng = getIntent().getDoubleExtra("destino_lng",0);
        mExtraOrigen = getIntent().getStringExtra("origen");
        mExtraDestino = getIntent().getStringExtra("destino");
        mOrigenLatLng = new LatLng(mExtraOrigenLat,mExtraOrigenLng);
        mDestinoLatLng = new LatLng(mExtraDestinoLat,mExtraDestinoLng);
        mAnimation.playAnimation();
        mGeofireProvider = new geofireProvider();
        mNotificacionProveedor = new NotificacionProveedor();
        mTokenProveedor = new TokenProveedor();
        mPoliciaBookingProveedor = new PoliciaBookingProveedor();
        mAuthProveedores = new authProveedores();
        mGoogleApiProveedor = new GoogleApiProveedor(RequestCamionetaActivity.this);

        getClosestPatrulla();
    }

    private void getClosestPatrulla(){


        mGeofireProvider.getActiveCamioneta(mOrigenLatLng , mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!mPatrullaFound){

                    mPatrullaFound = true;
                    mIDPatrullaFound = key;
                    mPatrullaFoundLatLng = new LatLng(location.latitude,location.longitude);
                    mTextviewCamioneta.setText("PATRULLA ENCONTRADA!\nEESPERANDO RESPUESTA");
                    createPoliciaBooking();
                    Log.d("Patrulla","ID" + mIDPatrullaFound);

                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(!mPatrullaFound){
                    mRadius = mRadius + 0.1f;
                    if(mRadius > 3) {
                        mTextviewCamioneta.setText("No se encontró patrullas");
                        Toast.makeText(RequestCamionetaActivity.this, "No se encontro patrullas cercanas", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        getClosestPatrulla();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createPoliciaBooking(){

        mGoogleApiProveedor.getDirections(mOrigenLatLng , mPatrullaFoundLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String DistanceTxt = distance.getString("text");
                    String DurationTxt = duration.getString("text");
                    enviarNotificacion(DurationTxt,DistanceTxt);

                }catch (Exception e){
                    Log.d("Error","Error encontrado"+ e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



    }

    private void enviarNotificacion(final String tiempo , final String km) {
        mTokenProveedor.getToken(mIDPatrullaFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String , String> map = new HashMap<>();
                    map.put("title","SOLICITUD DE SERVICIO A "+tiempo+ " DE TU POSCICIÓN");
                    map.put("body","UN POLICIA SOLICITA UNA PATRULLA A UNA DISTANCIA DE "+km);
                    FCMBody fcmbody = new FCMBody(token,"high",map);
                    mNotificacionProveedor.sendNotificacion(fcmbody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null){
                                if(response.body().getSuccess()==1){
                                    PoliciaBooking policiaBooking = new PoliciaBooking(mAuthProveedores.getId(),
                                            mIDPatrullaFound,
                                            mExtraDestino,
                                            mExtraOrigen,
                                            tiempo,
                                            km,"creado",
                                            mExtraOrigenLat,mExtraOrigenLng,
                                            mExtraDestinoLat,mExtraDestinoLng
                                    );

                                    mPoliciaBookingProveedor.create(policiaBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(RequestCamionetaActivity.this, "La petición se creó correctamente", Toast.LENGTH_SHORT).show();

                                        }
                                    });


                                }
                                else {
                                    Toast.makeText(RequestCamionetaActivity.this, "No se pudo enviar la notificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(RequestCamionetaActivity.this, "No se pudo enviar la notificación ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {

                            Log.d("error","error"+t.getMessage());

                        }
                    });
                }
                else {
                    Toast.makeText(RequestCamionetaActivity.this, "No se pudo enviar la notificación porque el conductor no tiene token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}