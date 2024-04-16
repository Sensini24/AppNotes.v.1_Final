package com.example.appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    private TextInputEditText mEmailRecover;
    private Button btnenviar, btnbacklogin;

    private String email;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog anuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        mEmailRecover = findViewById(R.id.txtemailRecover);
        btnenviar = findViewById(R.id.btnEnviar);
        btnbacklogin = findViewById(R.id.btnBacktoLogin);

        anuncio = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        btnbacklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forgot_Password.this, MainActivity.class));
            }
        });

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmailRecover.getText().toString();

                if(!email.isEmpty()){
                    anuncio.setMessage("ENVIANDO. Espere, por favor...");
                    anuncio.setCanceledOnTouchOutside(false);
                    anuncio.show();
                    recobrarPassword();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Ingrese el email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void recobrarPassword(){
        firebaseAuth.setLanguageCode("es");
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Se envió el correo de restauración de Contraseña", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "No se envió el correo de restauración de Contraseña", Toast.LENGTH_SHORT).show();
                }
                anuncio.dismiss();
            }
        });
    }

}