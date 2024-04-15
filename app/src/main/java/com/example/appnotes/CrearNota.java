package com.example.appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.entities.Colors;
import com.example.appnotes.entities.Nota;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.SimpleFormatter;

import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

public class CrearNota extends AppCompatActivity {

    Colors colores;
    private EditText titulo, contenido;
    private ImageView  guardar, btnimagen, foto;
    private LottieAnimationView home;
    private TextView txtFecha;
    private LinearLayout layoutNota;

    private Nota nota;

    private boolean deftipoguardado = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_nota);

        titulo = findViewById(R.id.txttítulo);
        contenido = findViewById(R.id.txtcontenido);
        home = findViewById(R.id.btnhome);
        guardar = findViewById(R.id.btnguardar);
        btnimagen = findViewById(R.id.btnimagen);
        foto = findViewById(R.id.fotoCamara);
        txtFecha = findViewById(R.id.txtFecha);


        colores = new Colors();

        //nota que viene desde lista con contenido
        nota = (Nota) getIntent().getSerializableExtra("nota");


        //cargar datoos de nota que se paso de la nota clicada
        if (nota != null) {
            deftipoguardado = true;
            cargarDatos();
        }else {
            deftipoguardado = false;
        }

        SimpleDateFormat fecha = new SimpleDateFormat("EEEE, dd MMMM HH:mm", new Locale("es", "PE"));
        fecha.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        String fechaNotas = fecha.format(new Date());
        txtFecha.setText(fechaNotas);


        //cuanddo se guarda, o cuando se edita
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si la nota clicada en lista ya tiene contenido se guarda cambios
                if(deftipoguardado){
                    guardarCambios();
                //sino se guarda la nueva nota
                }else {
                    guardarNota();
                    //se carga antes de aceptar si se toma o no la foto

                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.setSpeed(2);
                home.playAnimation();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(CrearNota.this, ListarNotas.class));
                    }
                }, 950);

            }
        });


        btnimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamara();
            }
        });

    }

    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }
    String rutaimagen = "";
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                // Obtener el Bitmap de la foto capturada
                Bitmap imgBitmap = (Bitmap) extras.get("data");
                Bitmap resizedBitmap = redimensionarBitmap(imgBitmap, 600);
                foto.setImageBitmap(resizedBitmap);
                rutaimagen = guardarImagenEnMemoriaInterna(resizedBitmap);
            }
        }
    }

    private String guardarImagenEnMemoriaInterna(Bitmap bitmap) {
        String directorio = getApplicationContext().getFilesDir().toString();
        String nombreArchivo = "imagen_" + System.currentTimeMillis() + ".jpg";

        String rutaArchivo = directorio + "/" + nombreArchivo;

        try {
            FileOutputStream fos = new FileOutputStream(new File(rutaArchivo));

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return rutaArchivo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //dentro del objeto nota se pasa la info de "nota" traido desde listado de notas
    //luego se pasa la info a cada text view
    private void cargarDatos() {
        titulo.setText(nota.getTitulo());
        contenido.setText(nota.getContenido());
        txtFecha.setText(nota.getFecha());
        if (nota.getImagenRuta() != null && !nota.getImagenRuta().isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(nota.getImagenRuta());
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, 600);
            foto.setImageBitmap(resizedBitmap);
        }

    }

    private void guardarNota() {
        if (titulo.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingrese el título", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (titulo.getText().toString().trim().isEmpty() && contenido.getText().toString()
                .trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "La nota está vacía", Toast.LENGTH_SHORT)
                    .show();
        } else if (contenido.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingrese el contenido de la nota",
                    Toast.LENGTH_SHORT).show();
        }


        if (rutaimagen.isEmpty()) {
            anuncioTomarFoto();
        }



        else {
            String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Nota nota = new Nota();
            nota.setTitulo(titulo.getText().toString());
            nota.setContenido(contenido.getText().toString());
            nota.setFecha(txtFecha.getText().toString());
            nota.setColor(colores.obtenerColores());
            nota.setUsuarioId(usuarioId);
            nota.setImagenRuta(rutaimagen);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                NotaDataBase.getDatabase(getApplicationContext()).notaDao().crearNota(nota);
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Nota guardada exitosamente",
                            Toast.LENGTH_SHORT).show();
                            guardarAnimation();
                });
            });
        }
    }

    //Parte de eeditar
    private void guardarCambios() {
        if (titulo.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingrese el título", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (contenido.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingrese el contenido de la nota", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Actualizar los campos de la nota existente
        nota.setTitulo(titulo.getText().toString());
        nota.setContenido(contenido.getText().toString());
        nota.setFecha(txtFecha.getText().toString());
        if (!rutaimagen.isEmpty()) {
            nota.setImagenRuta(rutaimagen);
        }

        // Guardar los cambios en la base de datos
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            NotaDataBase.getDatabase(getApplicationContext()).notaDao().updateNota(nota);
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ListarNotas.class);
                startActivity(intent);
                finish();
            });
        });
    }



    private void anuncioTomarFoto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alerta_guardado_foto, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView btnSi = view.findViewById(R.id.btnSi);
        TextView btnNo = view.findViewById(R.id.btnNo);

        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notasinFoto();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void notasinFoto(){
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Nota nota = new Nota();
        nota.setTitulo(titulo.getText().toString());
        nota.setContenido(contenido.getText().toString());
        nota.setFecha(txtFecha.getText().toString());
        nota.setColor(colores.obtenerColores());
        nota.setUsuarioId(usuarioId);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            NotaDataBase.getDatabase(getApplicationContext()).notaDao().crearNota(nota);
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Nota guardada exitosamente",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ListarNotas.class);
                startActivity(intent);
                finish();
            });
        });
    }

    private void eliminarNota() {
        if (nota != null) {
            // Eliminar la nota de la base de datos
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                NotaDataBase.getDatabase(getApplicationContext()).notaDao().deleteNote(nota);
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Nota eliminada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ListarNotas.class);
                    startActivity(intent);
                    finish();
                });
            });
        }
    }

    /*
    private void anuncioEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.alerta_eliminacion, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView btnSi = view.findViewById(R.id.btnSi);
        TextView btnNo = view.findViewById(R.id.btnNo);

        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarNota();
                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

     */


    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = Math.min(((float) maxSize / width), ((float) maxSize / height));
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public void guardarAnimation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.exito_guardado, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        LottieAnimationView exitoGuardado = view.findViewById(R.id.exito);

        exitoGuardado.setSpeed(1);
        exitoGuardado.setRepeatCount(-1);
        exitoGuardado.playAnimation();

        exitoGuardado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(CrearNota.this, ListarNotas.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {

        if (nota != null) {
            guardarCambios();
        } else {
            super.onBackPressed();
        }
    }
}