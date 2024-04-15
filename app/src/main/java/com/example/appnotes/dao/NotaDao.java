package com.example.appnotes.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appnotes.entities.Nota;

import java.util.List;
@Dao
public interface NotaDao {
    @Query("SELECT * FROM nota ORDER BY id DESC")
    List<Nota> getNotas ();

    @Query("SELECT * FROM nota WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    List<Nota> getNotasPorUsuario(String usuarioId);

    @Query("SELECT * FROM nota WHERE imagenRuta IS NOT NULL AND usuarioId = :usuarioId")
    List<Nota> getNotasConFoto(String usuarioId);

    @Query("SELECT * FROM nota WHERE usuarioId = :usuarioId AND (titulo LIKE '%' || :texto || '%' OR contenido LIKE '%' || :texto || '%')")
    List<Nota> buscarNotasPorUsuarioYTexto(String usuarioId, String texto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void crearNota(Nota nota);

    @Delete
    void deleteNote(Nota nota);

    @Update
    void updateNota(Nota nota);

}
