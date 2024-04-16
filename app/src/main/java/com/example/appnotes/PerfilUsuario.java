package com.example.appnotes;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.async.BitmapUtils;
import com.example.appnotes.async.InsertarNomUsuario;
import com.example.appnotes.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PerfilUsuario extends AppCompatActivity {

    private FirebaseUser usuarioActual;
    private TextInputEditText editContra;
    private ImageView btnEditContr, btnVolver, FotoPerfil, editFotoPerfil, editNomUsuario;
    private TextView numeroNotas, numeroFotos;
    private EditText cambiarUsuario;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);

        editContra = findViewById(R.id.editContra);
        btnEditContr = findViewById(R.id.btnEditContra);
        numeroNotas = findViewById(R.id.numeroNotas);
        numeroFotos = findViewById(R.id.numeroFotos);
        btnVolver = findViewById(R.id.btnVolver);
        FotoPerfil = findViewById(R.id.FotoPerfil);
        editFotoPerfil = findViewById(R.id.editFotoPerfil);
        editNomUsuario = findViewById(R.id.editNomUsuario);
        cambiarUsuario = findViewById(R.id.cambiarUsuario);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilUsuario.this, ListarNotas.class));
            }
        });



        btnEditContr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
                if (usuarioActual != null) {
                    if(editContra == null){
                        Log.e("PerfilUsuario", "editContra es nulo");
                        return;
                    }

                    String newPassword = editContra.getText().toString();
                    if (newPassword.isEmpty()) {

                        Toast.makeText(getApplicationContext(), "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usuarioActual.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Actualizar la contraseña exitosamente
                                        // Ahora ejecutar la actualización de la foto de usuario en un hilo separado
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... voids) {
                                                String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                Usuario usuario = NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().obtenerUsuarioPorId(usuarioId);

                                                NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().actualizarFotoUsuario(usuario);
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void aVoid) {

                                                Toast.makeText(getApplicationContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                            }
                                        }.execute();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No se actualizó la contraseña", Toast.LENGTH_SHORT).show();
                                        Log.e("PerfilUsuario", "Error al actualizar la contraseña", task.getException());
                                    }
                                }
                            });
                } else {
                    Log.e("PerfilUsuario", "Usuario actual es nulo");
                }
            }
        });


        editFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para abrir la galería
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        editNomUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarNombrePefil();
            }
        });


        cargarFoto();
        obtenerNumNotas();
        obtenerNumNotasconFoto();
    }

    private void obtenerNumNotas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Obtener el usuario actual de la base de datos
                String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                int numeroNotasInt = NotaDataBase.getDatabase(getApplicationContext()).notaDao().getNotasPorUsuario(usuarioId).size();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numeroNotas.setText(String.valueOf(numeroNotasInt));
                    }
                });
            }
        }).start();
    }

    private void obtenerNumNotasconFoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                int numeroFotosInt = NotaDataBase.getDatabase(getApplicationContext()).notaDao().getNotasConFoto(usuarioId).size();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numeroFotos.setText(String.valueOf(numeroFotosInt));
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // La imagen fue seleccionada exitosamente
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                FotoPerfil.setImageBitmap(bitmap);
                String bitmapString = BitmapUtils.bitmapToString(bitmap);
                // usuario actualñl
                FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
                if (usuarioActual != null) {
                    String usuarioId = usuarioActual.getUid();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Usuario usuario = NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().obtenerUsuarioPorId(usuarioId);
                            //String urlImagen = uri.toString();
                            //meti un bitmap a string
                            usuario.setFotoUsuario(bitmapString);
                            NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().actualizarFotoUsuario(usuario);
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void cargarFoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Usuario usuario = NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().obtenerUsuarioPorId(usuarioId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(usuario.getFotoUsuario())) {
                            // Intenta convertir la cadena de la foto a bitmap
                            Bitmap bitmap = BitmapUtils.stringToBitmap(usuario.getFotoUsuario());
                            if (bitmap != null) {
                                FotoPerfil.setImageBitmap(bitmap);
                            } else {
                                String url = "https://media.istockphoto.com/id/1464539429/es/foto/hombre-de-negocios-reflexivo-con-una-tableta-digital.jpg?s=2048x2048&w=is&k=20&c=0lvDwjF9yKpsZp_6LpEwsH9LWazV7fkKnUJWUB0k9sQ=";
                                Picasso.get().load(url).into(FotoPerfil);
                            }
                        } else {
                            // Si la URL de la foto está vacía, carga una imagen predeterminada
                            String url = "https://media.istockphoto.com/id/1464539429/es/foto/hombre-de-negocios-reflexivo-con-una-tableta-digital.jpg?s=2048x2048&w=is&k=20&c=0lvDwjF9yKpsZp_6LpEwsH9LWazV7fkKnUJWUB0k9sQ=";
                            Picasso.get().load(url).into(FotoPerfil);
                        }
                    }
                });
            }
        }).start();
    }

    private void cambiarNombrePefil() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String nombreNuevo = cambiarUsuario.getText().toString();
                if (!TextUtils.isEmpty(nombreNuevo)) {
                    String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Usuario usuario = NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().obtenerUsuarioPorId(usuarioId);

                    usuario.setNombre(nombreNuevo);

                    NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().actualizarFotoUsuario(usuario);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Nombre de Usuario ha sido cambiado por " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Ingrese un nombre", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }



}
