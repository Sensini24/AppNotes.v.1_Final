package com.example.appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.entities.Nota;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText email, contrasenia;
    private Button login;
    private TextView registrarse, olvideContrasenia;
    private CheckBox recordar;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main );


        email=findViewById(R.id.txtemail);
        contrasenia=findViewById(R.id.txtContrasenia);
        login=findViewById(R.id.btnlogin);
        registrarse=findViewById(R.id.txtRegistrarse);
        olvideContrasenia=findViewById(R.id.txtOlvideContrasenia);
        recordar = findViewById(R.id.recordarCuenta);

        firebaseAuth = FirebaseAuth.getInstance();


        //aqui se obtiene el estado de la cuenta, si no hay nignuna cuenta sin "recordar"
        //te pide que te logees, si hay alguno ingresa sin logearse



        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = sharedPreferences.getString("remember", "");
        if(checkbox.equals("true")){
            startActivity(new Intent(MainActivity.this, ListarNotas.class));
        } else if (checkbox.equals("false")) {
            Toast.makeText(this, "Ingrese tu correo y contraseña", Toast.LENGTH_SHORT).show();
        }







        registrarse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,Registrar.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String contra = contrasenia.getText().toString().trim();

                if(mail.isEmpty() || contra.isEmpty()){
                    Toast.makeText(getApplicationContext(), "LLene todos los campos", Toast.LENGTH_SHORT).show();
                }else{
                    ingresarUsuario(mail, contra);
                }
            }
        });

        olvideContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Forgot_Password.class));
            }
        });

        recordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Recordar Cuenta Activado", Toast.LENGTH_SHORT).show();
                } else if (!compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Recordar Cuenta Desactivado", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ingresarUsuario(String email, String contrasenia){
        firebaseAuth.signInWithEmailAndPassword(email, contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkEmailVerificacion();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailVerificacion(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified() == true){
            Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, ListarNotas.class));
        }
        else{
            Toast.makeText(getApplicationContext(), "Olvidaste verificar tu email", Toast.LENGTH_SHORT).show();
        }
    }


}