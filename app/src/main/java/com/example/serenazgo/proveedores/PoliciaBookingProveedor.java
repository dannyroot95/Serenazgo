package com.example.serenazgo.proveedores;

import com.example.serenazgo.modelos.PoliciaBooking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PoliciaBookingProveedor {

    private DatabaseReference mDatabase;

    public PoliciaBookingProveedor(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PoliciaBooking");
    }

    public Task<Void> create(PoliciaBooking policiaBooking){
         return mDatabase.child(policiaBooking.getIdPolicia()).setValue(policiaBooking);
    }

}
