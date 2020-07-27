package com.example.serenazgo.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.actividades.Camionetas.mapaVehiculo;
import com.example.serenazgo.actividades.Policias.mapaPolicia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.serenazgo.proveedores.authProveedores;

public class loginActivity extends AppCompatActivity {

    TextInputEditText edtcorreo , edtClave;
    Button btnlogin;
    private ProgressDialog mDialog;
    FirebaseAuth mAuth;
    DatabaseReference mReference;
    SharedPreferences mPreference;
    authProveedores auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Mitoolbar.mostrar(this,"Login",true);

        edtcorreo = (TextInputEditText) findViewById(R.id.logcorreo);
        edtClave = (TextInputEditText) findViewById(R.id.logclave);
        btnlogin = (Button) findViewById(R.id.btnLogin);
        mDialog = new ProgressDialog(this);
        auth = new authProveedores();
        mPreference = getApplicationContext().getSharedPreferences("tipoUsuario",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){

        String correo = edtcorreo.getText().toString();
        String pass = edtClave.getText().toString();

        if (!correo.isEmpty() && !pass.isEmpty()){
            mDialog.show();
            mDialog.setMessage("Iniciando sesión");
            if (pass.length()>=6)
            {
              mAuth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()) {
                               String usuario = mPreference.getString("user","");
                               if(usuario.equals("policia")){
                                   mReference.child("usuarios").child("policias").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.exists()){
                                               Intent intent = new Intent(loginActivity.this, mapaPolicia.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                               startActivity(intent);
                                           }
                                           else {
                                               Toast.makeText(loginActivity.this, "No es un usuario permitido", Toast.LENGTH_LONG).show();
                                               mAuth.signOut();
                                               startActivity(new Intent(loginActivity.this, Inicio.class));
                                               finish();
                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });

                               }

                               else {
                                   mReference.child("usuarios").child("camionetas").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.exists()){
                                               Intent intent = new Intent(loginActivity.this, mapaVehiculo.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                               startActivity(intent);
                                           }
                                           else {
                                               Toast.makeText(loginActivity.this, "No es un usuario permitido", Toast.LENGTH_LONG).show();
                                               mAuth.signOut();
                                               startActivity(new Intent(loginActivity.this, Inicio.class));
                                               finish();
                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });
                               }
                               mDialog.dismiss();
                               }
                      else {
                          Toast.makeText(loginActivity.this,"El correo o la contraseña son incorrectos",Toast.LENGTH_LONG).show();
                          mDialog.dismiss();
                      }
                  }
              });
            }

            else {
                Toast.makeText(loginActivity.this,"La contraseña debe tener mas de 6 caracteres",Toast.LENGTH_LONG).show();
                mDialog.dismiss();
            }

        }

        else {
            Toast.makeText(loginActivity.this,"Complete los campos",Toast.LENGTH_LONG).show();
            mDialog.dismiss();
        }

    }

}
