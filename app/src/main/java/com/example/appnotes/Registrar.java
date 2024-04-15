package com.example.appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnotes.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.appnotes.async.insertarUsuario;

public class Registrar extends AppCompatActivity {

    private TextInputEditText nombre, email, contrasenia;
    private Button registrar, login;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        login = findViewById(R.id.txtSignUp);
        nombre = findViewById(R.id.txtNombre);
        email = findViewById(R.id.txtemailRegistro);
        contrasenia = findViewById(R.id.txtcontraseniaRegistro);
        registrar = findViewById(R.id.btnRegistrar);

        firebaseAuth = FirebaseAuth.getInstance();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String contra = contrasenia.getText().toString().trim();
                String nom = nombre.getText().toString().trim();

                if(mail.isEmpty() || contra.isEmpty() || nom.isEmpty()){
                    Toast.makeText(getApplicationContext(), "LLene todos los campos", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(mail,contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                Usuario usuario = new Usuario();
                                usuario.setUsuarioId(usuarioId);
                                usuario.setNombre(nom);
                                usuario.setCorreo(correo);

                                new insertarUsuario(getApplicationContext(), usuario).execute();
                                enviarEmailVerificacion();

                            }else{
                                Toast.makeText(getApplicationContext(), "No se pudo registrar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(Registrar.this,MainActivity.class));
            }
        });
    }

    private void enviarEmailVerificacion(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "El correo de verificación fue enviado. Revisa tu correo y confirma", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(Registrar.this, MainActivity.class));
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "No se pudo enviar el correo de confirmación. Intenta de nuevo", Toast.LENGTH_SHORT).show();
        }
    }
}