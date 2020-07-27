package com.example.serenazgo.actividades.Camionetas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.actividades.Inicio;
import com.example.serenazgo.proveedores.TokenProveedor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.example.serenazgo.proveedores.authProveedores;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.serenazgo.proveedores.geofireProvider;


public class mapaVehiculo extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private authProveedores mauthProveedores;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mLocationFused;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int SETTINGS_REQUEST_CODE = 2;
    private Marker marker;
    private Button btnConnectCamion;
    private boolean conexion;
    private LatLng mCurrentLatLong;
    private geofireProvider mgeoProvider;
    private TokenProveedor mTokenProveedor;


    LocationCallback mLocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatLong = new LatLng(location.getLatitude(), location.getLongitude());

                    if (marker != null) {
                        marker.remove();
                    }

                    marker = map.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Tu poscición")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.police))

                    );
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(17f)
                                    .build()
                    ));

                    updateLocation();

                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_vehiculo);
        mauthProveedores = new authProveedores();
        mgeoProvider = new geofireProvider();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa_camioneta);
        mapFragment.getMapAsync(this);
        mLocationFused = LocationServices.getFusedLocationProviderClient(this);
        btnConnectCamion = (Button) findViewById(R.id.btnConectarseCamioneta);
        mTokenProveedor = new TokenProveedor();
        Mitoolbar.mostrar(this, "Mapa de patrullero", true);

        btnConnectCamion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (conexion) {
                    desconectar();
                } else {
                    startLocation();
                }
            }
        });

        generarToken();

    }

    public void updateLocation() {
        if (mauthProveedores.existe_sesion() && mCurrentLatLong != null) {
            mgeoProvider.guardar_localizacion(mauthProveedores.getId(), mCurrentLatLong);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivo()) {
                        mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
                    } else {
                        alertDialogNoGPS();
                    }
                }
            } else {
                checkLocationPermission();
            }
        } else {
            checkLocationPermission();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivo()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
        } else {
            alertDialogNoGPS();
        }
    }

    private void alertDialogNoGPS() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();

    }

    private boolean gpsActivo() {
        boolean activo = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activo = true;
        }
        return activo;
    }

    private void desconectar() {
        if (mLocationFused != null) {
            btnConnectCamion.setText("Conectarse");
            conexion = false;
            mLocationFused.removeLocationUpdates(mLocationCallBack);
            if (mauthProveedores.existe_sesion()) {
                mgeoProvider.borrar_localizacion(mauthProveedores.getId());
            }

        } else {
            Toast.makeText(this, "No se pudo desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActivo()) {
                    btnConnectCamion.setText("Desconectarse");
                    conexion = true;
                    mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
                } else {
                    alertDialogNoGPS();
                }

            } else {
                checkLocationPermission();
            }

        } else {
            if (gpsActivo()) {
                mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
            } else {
                alertDialogNoGPS();
            }
        }

    }


    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de su ubicación para continuar")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(mapaVehiculo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(mapaVehiculo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMapType(googleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(false);
        /*map.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(-12.5938271,-69.1877897))
                        .zoom(14f)
                        .build()
        ));*/

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
        startLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camioneta,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.accion_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    void logout(){
        desconectar();
        mauthProveedores.logout();
        startActivity(new Intent(mapaVehiculo.this, Inicio.class));
        finish();
    }

    void generarToken(){
        mTokenProveedor.create(mauthProveedores.getId());
    }


}
