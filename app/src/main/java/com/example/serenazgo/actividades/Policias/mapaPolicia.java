package com.example.serenazgo.actividades.Policias;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.actividades.Inicio;
import com.example.serenazgo.proveedores.TokenProveedor;
import com.example.serenazgo.proveedores.authProveedores;
import com.example.serenazgo.proveedores.geofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mapaPolicia extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private authProveedores mauthProveedores;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mLocationFused;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int SETTINGS_REQUEST_CODE = 2;
    private Marker marker;
    private LatLng mCurrentLatLong;
    private geofireProvider mgeofireProvider;
    private List<Marker> mMarkerCamionetas = new ArrayList<>();
    private boolean miPrimeraVez = true;
    private AutocompleteSupportFragment mAutoComplete;
    private AutocompleteSupportFragment mAutoCompleteDestination;
    private PlacesClient mPlaces;
    private String mOrigen;
    private LatLng mOrigenLatLng;
    private String mDestino;
    private LatLng mDestinoLatLng;
    private GoogleMap.OnCameraIdleListener mCameraListener;
    private Button mRequestButton;
    private TokenProveedor mTokenProveedor;

    LocationCallback mLocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                    /*
                    if (marker != null) {
                        marker.remove();
                    }
                    marker = map.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Tu poscición")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.poli))

                    );*/

                    map.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));

                    if (miPrimeraVez) {
                        miPrimeraVez = false;
                        getActiveCamioneta();
                        limitarBusquedas();
                    }

                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_policia);
        mauthProveedores = new authProveedores();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa_policia);
        mapFragment.getMapAsync(this);
        mgeofireProvider = new geofireProvider();
        mLocationFused = LocationServices.getFusedLocationProviderClient(this);
        mRequestButton = (Button) findViewById(R.id.btnincidente);
        mTokenProveedor = new TokenProveedor();
        Mitoolbar.mostrar(this, "Mapa de Policia", true);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        instanceAutocompleteOrigen();
        instanceAutocompleteDestino();
        onCameraMove();

        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCamioneta();
            }
        });

        generarToken();

    }

    private void requestCamioneta() {

        if(mOrigenLatLng != null && mDestinoLatLng !=null){
            Intent intent = new Intent(mapaPolicia.this,detailRequestActivity.class);
            intent.putExtra("origen_lat",mOrigenLatLng.latitude);
            intent.putExtra("origen_lng",mOrigenLatLng.longitude);
            intent.putExtra("destino_lat",mDestinoLatLng.latitude);
            intent.putExtra("destino_lng",mDestinoLatLng.longitude);
            intent.putExtra("origen",mOrigen);
            intent.putExtra("destino",mDestino);
            startActivity(intent);

        }

        else {
            Toast.makeText(this, "Debe selecionar su ubicacion y el lugar del incidente", Toast.LENGTH_SHORT).show();
        }

    }

    private void limitarBusquedas(){

        LatLng northSide = SphericalUtil.computeOffset(mCurrentLatLong,5000,0);
        LatLng southSide = SphericalUtil.computeOffset(mCurrentLatLong,5000,180);
        mAutoComplete.setCountry("PER");
        mAutoComplete.setLocationBias(RectangularBounds.newInstance(southSide,northSide));
        mAutoCompleteDestination.setCountry("PER");
        mAutoCompleteDestination.setLocationBias(RectangularBounds.newInstance(southSide,northSide));

    }

    private void onCameraMove (){

        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                try {

                    Geocoder geocoder = new Geocoder(mapaPolicia.this);
                    mOrigenLatLng = map.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mOrigenLatLng.latitude, mOrigenLatLng.longitude, 1);
                    String ciudad = addressList.get(0).getLocality();
                    String pais = addressList.get(0).getCountryName();
                    String direccion = addressList.get(0).getAddressLine(0);
                    mOrigen = direccion + " " + ciudad;
                    mAutoComplete.setText(direccion + " " + ciudad + " " + pais);

                } catch (Exception e) {
                    Log.d("Error: ", "Mensaje error: " + e.getMessage());
                }

            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivo()) {
                        mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
                        map.setMyLocationEnabled(true);
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
                return;
            }
            mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
            map.setMyLocationEnabled(true);
        } else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActivo()) {
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


    private void startLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActivo()) {
                    mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
                    map.setMyLocationEnabled(true);
                } else {
                    alertDialogNoGPS();
                }

            } else {
                checkLocationPermission();
            }

        } else {
            if (gpsActivo()) {
                mLocationFused.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());
                map.setMyLocationEnabled(true);
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
                                ActivityCompat.requestPermissions(mapaPolicia.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(mapaPolicia.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

        }

    }

    private void instanceAutocompleteOrigen(){

        mAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placesAutoCompleteOrigin);
        mAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoComplete.setHint("Tu ubicación");
        mAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                mOrigen = place.getName();
                mOrigenLatLng = place.getLatLng();
                Log.d("PLACE", "Name" + mOrigen);
                Log.d("PLACE", "Lat" + mOrigenLatLng.latitude);
                Log.d("PLACE", "Lng" + mOrigenLatLng.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    private void instanceAutocompleteDestino(){

        mAutoCompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placesAutoCompleteDestination);
        mAutoCompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompleteDestination.setHint("Lugar de incidente");
        mAutoCompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                mDestino = place.getName();
                mDestinoLatLng = place.getLatLng();
                Log.d("PLACE", "Name" + mDestino);
                Log.d("PLACE", "Lat" + mDestinoLatLng.latitude);
                Log.d("PLACE", "Lng" + mDestinoLatLng.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    private void getActiveCamioneta() {
        mgeofireProvider.getActiveCamioneta(mCurrentLatLong,10).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for (Marker marker : mMarkerCamionetas) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            return;
                        }
                    }
                }

                LatLng camionetaLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = map.addMarker(new MarkerOptions().position(camionetaLatLng).title("Patrullero en linea")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.police)));
                marker.setTag(key);
                mMarkerCamionetas.add(marker);

            }

            @Override
            public void onKeyExited(String key) {

                for (Marker marker : mMarkerCamionetas) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.remove();
                            mMarkerCamionetas.remove(marker);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for (Marker marker : mMarkerCamionetas) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMapType(googleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraIdleListener(mCameraListener);

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
        mauthProveedores.logout();
        startActivity(new Intent(mapaPolicia.this, Inicio.class));
        finish();
    }

    void generarToken(){
       mTokenProveedor.create(mauthProveedores.getId());
    }

}
