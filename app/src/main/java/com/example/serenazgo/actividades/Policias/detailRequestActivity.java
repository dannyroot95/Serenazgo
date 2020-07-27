package com.example.serenazgo.actividades.Policias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.Utils.DecodePoints;
import com.example.serenazgo.actividades.Camionetas.RequestCamionetaActivity;
import com.example.serenazgo.proveedores.GoogleApiProveedor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class detailRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private double mExtraOrigenLat;
    private double mExtraOrigenLng;
    private double mExtraDestinoLat;
    private double mExtraDestinoLng;
    private String mExtraOrigen;
    private String mExtraDestino;
    private LatLng mOrigenLatLng;
    private LatLng mDestinoLatLng;
    private GoogleApiProveedor mGoogleApiProveedor;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;
    private TextView mTxtOrigen;
    private TextView mTxtDestino;
    private TextView mTxtTiempo;
    private TextView mTxtDistancia;
    private Button mButtonRequest;

    private LatLng mOrigenLatLng2;
    private LatLng mDestinoLatLng2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);
        Mitoolbar.mostrar(this, "Incidente", true);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapdetail);
        mapFragment.getMapAsync(this);

        mExtraOrigenLat = getIntent().getDoubleExtra("origen_lat",0);
        mExtraOrigenLng = getIntent().getDoubleExtra("origen_lng",0);
        mExtraDestinoLat = getIntent().getDoubleExtra("destino_lat",0);
        mExtraDestinoLng = getIntent().getDoubleExtra("destino_lng",0);
        mExtraOrigen = getIntent().getStringExtra("origen");
        mExtraDestino = getIntent().getStringExtra("destino");

        mOrigenLatLng = new LatLng(mExtraOrigenLat , mExtraOrigenLng);
        mDestinoLatLng = new LatLng(mExtraDestinoLat , mExtraDestinoLng);
        mOrigenLatLng2 = new LatLng(-12.586425,-69.189354);
        mDestinoLatLng2 = new LatLng(-12.5879997, -69.1930283);

        mGoogleApiProveedor = new GoogleApiProveedor(detailRequestActivity.this);

        mTxtOrigen =  findViewById(R.id.txtOrigen);
        mTxtDestino=  findViewById(R.id.txtDestino);
        mTxtTiempo =  findViewById(R.id.txtTiempo);
        mTxtDistancia = findViewById(R.id.txtDistancia);
        mButtonRequest = findViewById(R.id.btnRequestNow);

        mTxtOrigen.setText(mExtraOrigen);
        mTxtDestino.setText(mExtraDestino);

        mButtonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRequestCamioneta();
            }
        });


    }

    private void gotoRequestCamioneta() {

        Intent intent = new Intent(detailRequestActivity.this,RequestCamionetaActivity.class);
        intent.putExtra("origen_lat",mOrigenLatLng.latitude);
        intent.putExtra("origen_lng",mOrigenLatLng.longitude);
        intent.putExtra("origen",mExtraOrigen);
        intent.putExtra("destino",mExtraDestino);
        intent.putExtra("destino_lat",mDestinoLatLng.latitude);
        intent.putExtra("destino_lng",mDestinoLatLng.longitude);
        startActivity(intent);
        finish();

    }

    public void drawRoute(){

        Toast.makeText(this, "origen : "+mOrigenLatLng2, Toast.LENGTH_LONG).show();
        mGoogleApiProveedor.getDirections(mOrigenLatLng2 , mDestinoLatLng2).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    map.addPolyline(mPolylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String DistanceTxt = distance.getString("text");
                    String DurationTxt = duration.getString("text");

                    mTxtTiempo.setText(DurationTxt);
                    mTxtDistancia.setText(DistanceTxt);


                }catch (Exception e){
                    Log.d("Error","Error encontrado"+ e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMapType(googleMap.MAP_TYPE_NORMAL);
        map.addMarker(new MarkerOptions().position(mOrigenLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.origen)));
        map.addMarker(new MarkerOptions().position(mDestinoLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.destino)));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(

                new CameraPosition.Builder()
                        .target(mOrigenLatLng)
                        .zoom(16f)
                        .build()

        ));

        drawRoute();


    }
}