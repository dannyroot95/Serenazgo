package com.example.serenazgo.Inclusiones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.serenazgo.R;

public class Mitoolbar {

    public static void mostrar(AppCompatActivity activity , String titulo , boolean upBottom){

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(titulo);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upBottom);

    }


}
