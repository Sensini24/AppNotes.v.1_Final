package com.example.appnotes.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.entities.Usuario;

public class InsertarNomUsuario extends AsyncTask<Void, Void, String> {

    private Context context;
    private String usuarioId;
    private TextView nombreUsuarioTextView;

    public InsertarNomUsuario(Context context, String usuarioId, TextView nombreUsuarioTextView) {
        this.context = context;
        this.usuarioId = usuarioId;
        this.nombreUsuarioTextView = nombreUsuarioTextView;
    }

    @Override
    protected String doInBackground(Void... voids) {
        // Obtener el nombre del usuario de la base de datos
        return NotaDataBase.getDatabase(context).usuarioDao().obtenerNombreUsuarioPorId(usuarioId);
    }

    @Override
    protected void onPostExecute(String nombreUsuario) {
        super.onPostExecute(nombreUsuario);
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            nombreUsuarioTextView.setText(nombreUsuario);
        } else {
            nombreUsuarioTextView.setText("Nombre de usuario no disponible");
        }
    }
}
