package com.example.appnotes.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "nota")
public class Nota implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    //ojo entrada nueva
    @ColumnInfo(name = "usuarioId")
    private String usuarioId;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "contenido")
    private String contenido;

    @ColumnInfo(name = "imagenRuta")
    private String imagenRuta;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "prioridad")
    private String prioridad;

    @ColumnInfo(name = "color")
    private String color;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getImagenRuta() {
        return imagenRuta;
    }

    public void setImagenRuta(String imagenRuta) {
        this.imagenRuta = imagenRuta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return titulo + " : "  + fecha;
    }
}
