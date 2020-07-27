package com.example.serenazgo.proveedores;

import com.example.serenazgo.modelos.Policias;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class policiaProveedor {

    DatabaseReference mReference;

    public policiaProveedor () {

        mReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child("policias");

    }

    public Task<Void> crear(Policias policias) {

        Map<String , Object> map = new HashMap<>();
        map.put("nombre",policias.getNombre());
        map.put("cip",policias.getCip());
        map.put("mail",policias.getMail());

        return mReference.child(policias.getId()).setValue(map);
    }

}
