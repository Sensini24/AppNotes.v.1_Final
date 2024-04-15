package com.example.appnotes.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appnotes.entities.Usuario;

@Dao
public interface UsuarioDao {

    @Query("SELECT nombre FROM usuario WHERE usuarioId = :idUsuario")
    String obtenerNombreUsuarioPorId(String idUsuario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarUsuario(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE usuarioId = :usuarioId")
    Usuario obtenerUsuarioPorId(String usuarioId);

    @Update
    void actualizarFotoUsuario(Usuario usuario);
}
