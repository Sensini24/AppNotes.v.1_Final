package com.example.appnotes.async;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.appnotes.Database.NotaDataBase;
import com.example.appnotes.entities.Usuario;

public class insertarUsuario extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Usuario usuario;

    public insertarUsuario(Context context, Usuario usuario) {
        this.context = context;
        this.usuario = usuario;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        NotaDataBase.getDatabase(context).usuarioDao().insertarUsuario(usuario);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "Registro Exitoso", Toast.LENGTH_SHORT).show();
    }

}
