package com.example.appnotes.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "usuario")
public class Usuario implements Serializable {

    @NonNull
    @PrimaryKey
    private String usuarioId; // El UID del usuario en Firebase

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "correo")
    private String correo;


    @ColumnInfo(name= "fotoUsuario")
    private String fotoUsuario;


    @NonNull
    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(@NonNull String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }
}