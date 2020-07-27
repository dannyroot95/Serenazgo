package com.example.serenazgo.proveedores;

import com.example.serenazgo.modelos.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class TokenProveedor {

    DatabaseReference mDatabase;

    public TokenProveedor(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }

    public void create(final String idPolicia){
        if(idPolicia == null) return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                Token token = new Token(instanceIdResult.getToken());
                mDatabase.child(idPolicia).setValue(token);

            }
        });
    }

    public DatabaseReference getToken(String idUsuario){
        return mDatabase.child(idUsuario);
    }

}
