package com.example.serenazgo.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.serenazgo.R;
import com.example.serenazgo.actividades.Camionetas.mapaVehiculo;
import com.example.serenazgo.actividades.Policias.mapaPolicia;
import com.google.firebase.auth.FirebaseAuth;

public class Inicio extends AppCompatActivity {

    private Button btnCamioneta,btnPolicia;
    SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamioneta = (Button) findViewById(R.id.btnCamioneta);
        btnPolicia = (Button) findViewById(R.id.btnPolicia);
        mPreference = getApplicationContext().getSharedPreferences("tipoUsuario",MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreference.edit();

        btnCamioneta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","camioneta");
                editor.apply();
                selectOptions();
            }
        });

        btnPolicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","policia");
                editor.apply();
                selectOptions();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String usuario = mPreference.getString("user","");
            if(usuario.equals("policia")){
                Intent intent = new Intent(Inicio.this, mapaPolicia.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(Inicio.this, mapaVehiculo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    public void selectOptions(){
        startActivity(new Intent(Inicio.this,Opciones.class));
    }

}
