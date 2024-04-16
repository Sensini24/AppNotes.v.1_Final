package com.example.appnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.adapter.NotaAdapter;
import com.example.appnotes.async.BitmapUtils;
import com.example.appnotes.entities.Nota;
import com.example.appnotes.entities.Usuario;
import com.example.appnotes.listener.NotaListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class ListarNotas extends AppCompatActivity implements NotaListener {

    //nuevo codigo
    private static final int EDITAR_NOTA_REQUEST_CODE = 1;


    private FloatingActionButton agregarNota;
    private RecyclerView listaNotasRecycler;
    private LinearLayout salir;
    FirebaseAuth firebaseAuth;

    private NotaAdapter notaAdapter;
    private List<Nota> listadeNotas;
    private TextView nombreusuario, fechaAct;
    private SearchView searchView;
    private ImageView imagePerfil;

    private LottieAnimationView logoutAnim, perfilUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notas);

        salir = findViewById(R.id.btnSalir);
        logoutAnim = findViewById(R.id.logoutA);
        firebaseAuth = FirebaseAuth.getInstance();
        agregarNota = findViewById(R.id.btnAgregarNota);
        nombreusuario = findViewById(R.id.txtNomUsuario);
        searchView = findViewById(R.id.searchView);

        //RECYCLER VIEW Y NOTA ADAPTER, LISTA DE NOTAS
        listaNotasRecycler = findViewById(R.id.listaNotasRecycler);
        listaNotasRecycler.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        listadeNotas = new ArrayList<>();
        notaAdapter = new NotaAdapter(listadeNotas, this);
        listaNotasRecycler.setAdapter(notaAdapter);


        fechaAct = findViewById(R.id.fechaActual);
        imagePerfil = findViewById(R.id.imagenPerfil);
        perfilUsuario = findViewById(R.id.perfilUsuario);




        logoutAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                logoutAnim.setSpeed(1);
                logoutAnim.playAnimation();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(ListarNotas.this, MainActivity.class));
                    }
                }, 1200);
            }
        });



        perfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfilUsuario.setSpeed(1);
                perfilUsuario.playAnimation();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(ListarNotas.this, PerfilUsuario.class));
                    }
                }, 1200);

            }
        });


        agregarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListarNotas.this, CrearNota.class));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarNotas(newText);
                return true;
            }
        });


        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getNotas(usuarioId);
        cargarFotoYNombre();
    }



    private void getNotas(String usuarioId){
        @SuppressLint("StaticFieldLeak")
        class ObtenerNotasTask extends AsyncTask<Void, Void, List<Nota>> {
            @Override
            protected List<Nota> doInBackground(Void... voids) {
                return NotaDataBase.getDatabase(getApplicationContext()).notaDao().getNotasPorUsuario(usuarioId);
            }
            //se carga los datos a la lista y se comunica ala datapdatos
            //si hay datos se carga a la list a
            @Override
            protected void onPostExecute(List<Nota> notas) {
                super.onPostExecute(notas);
                if(listadeNotas.size() == 0){
                    listadeNotas.addAll(notas);
                    notaAdapter.notifyDataSetChanged();
                }else{
                    listadeNotas.add(0, notas.get(0));
                    notaAdapter.notifyItemInserted(0);
                }
                listaNotasRecycler.smoothScrollToPosition(0);
            }

        }
        new ObtenerNotasTask().execute();
    }

    //se byusca notas por texto y se comunica al adapter, luego se actualizar el recyvler
    private void buscarNotas(String texto) {
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        @SuppressLint("StaticFieldLeak")
        class BuscarNotasTask extends AsyncTask<Void, Void, List<Nota>> {
            @Override
            protected List<Nota> doInBackground(Void... voids) {
                return NotaDataBase.getDatabase(getApplicationContext()).notaDao().buscarNotasPorUsuarioYTexto(usuarioId, texto);
            }

            @Override
            protected void onPostExecute(List<Nota> notas) {
                super.onPostExecute(notas);
                listadeNotas.clear();
                listadeNotas.addAll(notas);
                notaAdapter.notifyDataSetChanged();
            }
        }
        new BuscarNotasTask().execute();
    }


    //cuando se hace click pasa a la interfaz de crear nota
    //dentro de "nota" esta la informacion de la nota clickada
    //para poder ser editado
    @Override
    public void clickarNota(Nota nota) {
        Intent intent = new Intent(ListarNotas.this, CrearNota.class);
        intent.putExtra("nota", nota);
        startActivityForResult(intent, EDITAR_NOTA_REQUEST_CODE);
    }



    private void cargarFotoYNombre(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Usuario usuario = NotaDataBase.getDatabase(getApplicationContext()).usuarioDao().obtenerUsuarioPorId(usuarioId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        SimpleDateFormat fechaH = new SimpleDateFormat("EEE dd MMM", new Locale("es", "PE"));
                        fechaH.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                        String fechaHeader = fechaH.format(new Date());
                        fechaAct.setText(fechaHeader);

                        if(usuario.getNombre().isEmpty() || usuario.getNombre() == null){
                            nombreusuario.setText("Nombre Random");
                        }else{
                            nombreusuario.setText(usuario.getNombre().trim());
                        }

                        if (!TextUtils.isEmpty(usuario.getFotoUsuario())) {

                            String stringtoBitmap = usuario.getFotoUsuario();
                            Bitmap bitmap = BitmapUtils.stringToBitmap(stringtoBitmap);
                            imagePerfil.setImageBitmap(bitmap);
                        } else {
                            String url = "https://media.istockphoto.com/id/1464539429/es/foto/hombre-de-negocios-reflexivo-con-una-tableta-digital.jpg?s=2048x2048&w=is&k=20&c=0lvDwjF9yKpsZp_6LpEwsH9LWazV7fkKnUJWUB0k9sQ=";
                            Picasso.get().load(url).into(imagePerfil);
                        }
                    }
                });
            }
        }).start();
    }
}