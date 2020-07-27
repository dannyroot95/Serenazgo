package com.example.serenazgo.actividades.Camionetas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.serenazgo.Inclusiones.Mitoolbar;
import com.example.serenazgo.R;
import com.example.serenazgo.modelos.Camioneta;
import com.example.serenazgo.proveedores.authProveedores;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.serenazgo.proveedores.camionetaProveedor;


public class registerCamionetaActivity extends AppCompatActivity {

    private TextInputEditText edtCorreo , edtClave , edtcip , edtnombre , edtmarca , edtplaca;

    private ProgressDialog mDialog;
    Button mButtonReg;
    authProveedores mauthProveedores;
    camionetaProveedor mcamionetaProveedor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_camioneta);

        Mitoolbar.mostrar(this,"Registro",true);
        mauthProveedores = new authProveedores();
        mcamionetaProveedor = new camionetaProveedor();

        mDialog = new ProgressDialog(this);
        mButtonReg = (Button) findViewById(R.id.btnRegistrarse);

        edtnombre = (TextInputEditText) findViewById(R.id.nombres);
        edtCorreo = (TextInputEditText) findViewById(R.id.regcorreo);
        edtcip    = (TextInputEditText) findViewById(R.id.cip);
        edtClave = (TextInputEditText) findViewById(R.id.regcontrasena);
        edtmarca = (TextInputEditText) findViewById(R.id.marca);
        edtplaca = (TextInputEditText) findViewById(R.id.placa);

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
        final String marca =  edtmarca.getText().toString();
        final String placa =  edtplaca.getText().toString();
        final String clave = edtClave.getText().toString();

        if(!name.isEmpty() && !mail.isEmpty() && !clave.isEmpty() && !cip.isEmpty() && !marca.isEmpty() && !placa.isEmpty()){
            mDialog.show();
            mDialog.setMessage("Registrando usuario");
            if(clave.length()>=6){
                registro(name,cip,marca,placa,mail,clave);
            }
            else {
                Toast.makeText(registerCamionetaActivity.this,"La contraseña debe ser mayor a 6 caracteres",Toast.LENGTH_LONG).show();
            }

        }
        else {

            Toast.makeText(registerCamionetaActivity.this,"Debes completar todos los campos",Toast.LENGTH_LONG).show();
        }
    }

    void registro(final String nombre , final String cip , final String marca , final String placa ,final String mail , String clave){

        mauthProveedores.registro(mail,clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Camioneta camioneta = new Camioneta(id,nombre,cip,marca,placa,mail);
                    crear(camioneta);
                }
                else {
                    Toast.makeText(registerCamionetaActivity.this,"Error en el registro",Toast.LENGTH_LONG).show();}
            }
        });

    }

    void crear(Camioneta camioneta){
        mcamionetaProveedor.crear(camioneta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    Toast.makeText(registerCamionetaActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                }

                else {
                    mDialog.dismiss();
                    Toast.makeText(registerCamionetaActivity.this, "No se pudo crear un nuevo policía", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
