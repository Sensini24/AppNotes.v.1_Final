package com.example.appnotes.entities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Colors {

    private ArrayList<String> colores;

    // Constructor
    public Colors() {
        colores = new ArrayList<>();
        colores.add("#fe9b80");
        colores.add("#92d382");
        colores.add("#a5eddf");
        colores.add("#d9bc80");
        colores.add("#f7c2cc");
        colores.add("#89ea91");
        colores.add("#9780c4");
        colores.add("#97bcbd");
        colores.add("#f6c8a0");
        colores.add("#ee8098");
        colores.add("#fcd480");
        colores.add("#b1e3b8");
        colores.add("#fdfd96");
        colores.add("#ff6961");
        colores.add("#a18594");
    }

    public String obtenerColores(){
        Random random = new Random();
        int indice = random.nextInt(colores.size());
        return colores.get(indice);
    }

}
