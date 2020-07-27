package com.example.serenazgo.actividades.Policias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.modelos.Policias;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.example.serenazgo.proveedores.authProveedores;
import com.example.serenazgo.proveedores.policiaProveedor;
import com.google.firebase.auth.FirebaseAuth;


public class registerPoliciaActivity extends AppCompatActivity {

    private TextInputEditText edtCorreo , edtClave , edtcip , edtnombre;
    private ProgressDialog mDialog;
    Button mButtonReg;
    authProveedores mauthProveedores;
    policiaProveedor mpoliciaProveedor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Mitoolbar.mostrar(this,"Registro",true);
        mauthProveedores = new authProveedores();
        mpoliciaProveedor = new policiaProveedor();

        mDialog = new ProgressDialog(this);
        mButtonReg = (Button) findViewById(R.id.btnRegistrarse);
        edtnombre = (TextInputEditText) findViewById(R.id.nombres);
        edtCorreo = (TextInputEditText) findViewById(R.id.regcorreo);
        edtcip = (TextInputEditText) findViewById(R.id.cip);
        edtClave = (TextInputEditText) findViewById(R.id.regcontrasena);

        mButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegistro();
            }
        });
    }

    void clickRegistro(){

        final String name = edtnombre.getText().toString();
        final String mail = edtCorreo.getText().toString();
        final String cip =  edtcip.getText().toString();
        final String clave = edtClave.getText().toString();

        if(!name.isEmpty() && !mail.isEmpty() && !clave.isEmpty() && !cip.isEmpty()){
            mDialog.show();
            mDialog.setMessage("Registrando usuario");
            if(clave.length()>=6){
                registro(name,cip,mail,clave);
            }
            else {
                Toast.makeText(registerPoliciaActivity.this,"La contraseña debe ser mayor a 6 caracteres",Toast.LENGTH_LONG).show();
            }

        }
        else {

            Toast.makeText(registerPoliciaActivity.this,"Debes completar todos los campos",Toast.LENGTH_LONG).show();
        }
    }

    void registro(final String nombre , final String cip , final String mail , String clave){

        mauthProveedores.registro(mail,clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Policias policias = new Policias(id,nombre,cip,mail);
                    crear(policias);
                }
                else {
                    Toast.makeText(registerPoliciaActivity.this,"Error en el registro",Toast.LENGTH_LONG).show();}
            }
        });

    }

    void crear(Policias policias){
        mpoliciaProveedor.crear(policias).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    Intent intent = new Intent(registerPoliciaActivity.this,mapaPolicia.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(registerPoliciaActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                }

                else {
                    mDialog.dismiss();
                    Toast.makeText(registerPoliciaActivity.this, "No se pudo crear un nuevo policía", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   /*
    void guardar(String id , String cip, String mail , String name  ){

        String selectUser = mPreference.getString("user","");
        Usuario usuario = new Usuario();
        usuario.setCip(cip);
        usuario.setMail(mail);
        usuario.setNombre(name);

        if (selectUser.equals("camioneta")){

            mReference.child("usuarios").child("camionetas").child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        mDialog.dismiss();
                        Toast.makeText(registerActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();

                    }

                    else {
                        mDialog.dismiss();
                        Toast.makeText(registerActivity.this,"Registro fallido",Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

        else if (selectUser.equals("policia")){

            mReference.child("usuarios").child("policias").child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mDialog.dismiss();
                        Toast.makeText(registerActivity.this,"Registro exitoso",Toast.LENGTH_LONG).show();
                    }
                    else {
                        mDialog.dismiss();
                        Toast.makeText(registerActivity.this,"Registro fallido",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    */



}
