package com.example.serenazgo.proveedores;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class authProveedores {

    FirebaseAuth mAuth;

    public authProveedores(){
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> registro(String correo , String pass){

        return  mAuth.createUserWithEmailAndPassword(correo,pass);

    }

    public Task<AuthResult> login(String correo , String pass){

        return  mAuth.signInWithEmailAndPassword(correo,pass);

    }

    public void logout(){
        mAuth.signOut();
    }

    public String getId(){
        return mAuth.getCurrentUser().getUid();
    }

    public boolean existe_sesion(){
         boolean existe = false;
         if(mAuth.getCurrentUser() !=null){
             existe=true;
         }
         return existe;
    }

}
