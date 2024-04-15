package com.example.appnotes.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    public static String bitmapToString(Bitmap bitmap) {
        if (bitmap == null) return null;

        // Convertir el bitmap a un array de bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Codificar el array de bytes a Base64
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String string) {
        if (string == null) return null;

        // Decodificar la cadena Base64 a un array de bytes
        byte[] byteArray = Base64.decode(string, Base64.DEFAULT);

        // Convertir el array de bytes a un bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
