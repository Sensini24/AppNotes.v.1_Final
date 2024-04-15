package com.example.appnotes.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.appnotes.dao.NotaDao;
import com.example.appnotes.dao.UsuarioDao;
import com.example.appnotes.entities.Nota;
import com.example.appnotes.entities.Usuario;

@Database(entities = {Nota.class, Usuario.class}, version = 4, exportSchema = false)
public abstract class NotaDataBase extends RoomDatabase{
    private  static NotaDataBase notaDataBase;
    public static synchronized NotaDataBase getDatabase(Context context){
        if(notaDataBase==null){
            notaDataBase = Room.databaseBuilder(context, NotaDataBase.class,
                            "nota_db")
                    .fallbackToDestructiveMigration() // Agregar migración aquí
                    .build();
        }
        return notaDataBase;
    }


    public abstract NotaDao notaDao();

    public abstract UsuarioDao usuarioDao();

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Crear la nueva tabla con la nueva columna
            database.execSQL("CREATE TABLE IF NOT EXISTS `usuario_new` (`usuarioId` TEXT PRIMARY KEY, `nombre` TEXT, `correo` TEXT, `fotoUsuario` TEXT)");

            // Copiar los datos de la tabla existente a la nueva tabla
            database.execSQL("INSERT INTO `usuario_new` (`usuarioId`, `nombre`, `correo`) SELECT `usuarioId`, `nombre`, `correo` FROM `usuario`");

            // Eliminar la tabla antigua
            database.execSQL("DROP TABLE `usuario`");

            // Renombrar la nueva tabla con el nombre original
            database.execSQL("ALTER TABLE `usuario_new` RENAME TO `usuario`");
        }
    };


}
