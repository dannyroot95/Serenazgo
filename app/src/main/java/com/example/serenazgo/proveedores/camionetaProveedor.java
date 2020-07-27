package com.example.serenazgo.proveedores;

import com.example.serenazgo.modelos.Camioneta;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class camionetaProveedor {

    DatabaseReference mReference;

    public camionetaProveedor () {

        mReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child("camionetas");

    }

    public Task<Void> crear(Camioneta camioneta) {
        Map<String , Object> map = new HashMap<>();
        map.put("nombre",camioneta.getNombre());
        map.put("cip",camioneta.getCip());
        map.put("marca",camioneta.getMarca());
        map.put("placa",camioneta.getPlaca());
        map.put("mail",camioneta.getMail());
        return mReference.child(camioneta.getId()).setValue(map);
    }

}
