package com.example.serenazgo.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.actividades.Policias.registerPoliciaActivity;
import com.example.serenazgo.actividades.Camionetas.registerCamionetaActivity;


public class Opciones extends AppCompatActivity {

    private Button btnogin,btnRegistro;
    SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        Mitoolbar.mostrar(this,"Seleccione una opción" , true);

        btnogin = (Button) findViewById(R.id.btnIrLogin);
        btnRegistro = (Button) findViewById(R.id.btnIrRegistro);
        mPreference = getApplicationContext().getSharedPreferences("tipoUsuario",MODE_PRIVATE);
        btnogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irLogin();
            }
        });
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irRegistro();
            }
        });

    }

    private void irLogin(){
        startActivity(new Intent(Opciones.this,loginActivity.class));
    }

    private void irRegistro(){
        String tipoUsuario = mPreference.getString("user","");
        if(tipoUsuario.equals("policia")){
        startActivity(new Intent(Opciones.this, registerPoliciaActivity.class));
        }
        else {
            startActivity(new Intent(Opciones.this, registerCamionetaActivity.class));
        }
    }

}
