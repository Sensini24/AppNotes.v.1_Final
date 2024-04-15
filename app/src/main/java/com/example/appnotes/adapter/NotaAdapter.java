package com.example.appnotes.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.ListarNotas;
import com.example.appnotes.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnotes.entities.Nota;
import com.example.appnotes.listener.NotaListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {
    private List<Nota> notas;

    private NotaListener notaListener;
    private static boolean isChecked = false;



    public NotaAdapter(List<Nota> notas, NotaListener notaListener)
            {
        this.notas = notas;
        this.notaListener = notaListener;
    }



    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotaViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.interfaz_notas,
                        parent, false)
        );
    }

    //aqui identifico la psociion de la nota clicada
    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        Nota nota = notas.get(position);
        holder.setNota(nota);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setFavorito(!isChecked);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notaListener.clickarNota(nota);
            }
        });
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.eliminar.setSpeed(2);
                holder.eliminar.playAnimation();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.anuncioEliminar(nota);
                    }
                }, 1000);

            }
        });

    }



    @Override
    public int getItemCount() {
        return notas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NotaViewHolder extends RecyclerView.ViewHolder{
        TextView notaTitulo, notaContenido, notaFecha;
        LinearLayout notaCuerpo;
        ImageView imagen;
        LottieAnimationView favorite, eliminar;


        NotaViewHolder(@NonNull View itemView){
            super(itemView);
            notaTitulo = itemView.findViewById(R.id.notaTitulo);
            notaContenido = itemView.findViewById(R.id.notaContenido);
            notaFecha = itemView.findViewById(R.id.notaFecha);
            notaCuerpo = itemView.findViewById(R.id.notaBox);
            imagen = itemView.findViewById(R.id.imagenFoto);
            favorite = itemView.findViewById(R.id.lotfavorito);
            eliminar = itemView.findViewById(R.id.eliminarNota);
        }

        void setNota(Nota nota){
            notaTitulo.setText(nota.getTitulo());
            if(nota.getContenido().isEmpty()){
                notaContenido.setVisibility(View.GONE);
            }else{
                notaContenido.setText(nota.getContenido());
            }

            if (nota.getColor() == null || nota.getColor().isEmpty()) {

                notaCuerpo.setBackgroundResource(R.drawable.redondear_notas);
            } else {
                //dar color a la nota
                int colorInt = Color.parseColor(nota.getColor());


                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setColor(colorInt);
                gradientDrawable.setCornerRadius(60);

                notaCuerpo.setBackground(gradientDrawable);
            }
            notaFecha.setText(nota.getFecha());
            if (nota.getImagenRuta() != null && !nota.getImagenRuta().isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(nota.getImagenRuta());


                Bitmap resizedBitmap = redimensionarBitmap(bitmap, 600);
                imagen.setImageBitmap(resizedBitmap);
            }
        }

        public void setFavorito(boolean checked) {
            isChecked = checked;
            if (isChecked) {
                    favorite.setSpeed(1f);
                    favorite.setMinAndMaxProgress(0.1f, 0.5f);
                    favorite.playAnimation();

            } else {
                    favorite.setSpeed(1f);
                    favorite.setMinAndMaxProgress(0.6f, 1f);
                    favorite.playAnimation();

            }
        }



        public void anuncioEliminar(Nota nota) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.alerta_eliminacion, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();

            TextView btnSi = view.findViewById(R.id.btnSi);
            TextView btnNo = view.findViewById(R.id.btnNo);

            btnSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarNota(nota);
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
        private void eliminarNota(Nota nota) {
            HandlerThread handlerThread = new HandlerThread("EliminarNotaThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                NotaDataBase.getDatabase(itemView.getContext()).notaDao().deleteNote(nota);
                handler.post(() -> {
                    Toast.makeText(itemView.getContext(), "Nota eliminada exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(itemView.getContext(), ListarNotas.class);
                    itemView.getContext().startActivity(intent);
                    // Si estás dentro de una actividad, puedes llamar a finish() directamente
                    // ((Activity) itemView.getContext()).finish();
                    handlerThread.quit();
                });
            });
            executor.shutdown();
        }




        private Bitmap redimensionarBitmap(Bitmap bitmap, int maxSize) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = Math.min(((float) maxSize / width), ((float) maxSize / height));
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
    }
}
