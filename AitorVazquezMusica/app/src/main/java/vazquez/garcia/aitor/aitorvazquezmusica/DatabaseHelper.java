package vazquez.garcia.aitor.aitorvazquezmusica;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Musica.db";
    private static final int DATABASE_VERSION = 3;


    private static final String TABLE_CANCION = "Canciones";
    private static final String TABLE_ARTISTA = "Artistas";
    private static final String TABLE_DISCOS = "Discos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_ALBUM = "album";
    private static final String COLUMN_ARTISTA = "artista";
    private static final String COLUMN_RUTA = "ruta";
    private static final String COLUMN_IMAGEN = "imagen";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_BUSQUEDA = "busqueda";

    private static final String CREATE_TABLE_CANCIONES = "CREATE TABLE IF NOT EXISTS " + TABLE_CANCION + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITULO + " TEXT NOT NULL, "
            + COLUMN_ALBUM + " TEXT DEFAULT 'Desconocido', "
            + COLUMN_ARTISTA + "TEXT DEFAULT 'Desconocido', "
            + COLUMN_RUTA + " TEXT DEFAULT 'default_song', "
            + COLUMN_IMAGEN + " TEXT DEFAULT 'default_img', "
            + COLUMN_BUSQUEDA + " INTEGER DEFAULT 0);";

    private static final String CREATE_TABLE_ARTISTAS= "CREATE TABLE IF NOT EXISTS " + TABLE_ARTISTA + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOMBRE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPCION + " TEXT DEFAULT 'Sin descripción', "
            + COLUMN_IMAGEN + " TEXT DEFAULT 'default_img_art', "
            + COLUMN_BUSQUEDA + " INTEGER DEFAULT 0);";

    private static final String CREATE_TABLE_DISCOS = "CREATE TABLE IF NOT EXISTS " + TABLE_DISCOS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITULO + " TEXT NOT NULL, "
            + COLUMN_ARTISTA + " TEXT DEFAULT 'Desconocido', "
            + COLUMN_DESCRIPCION + "TEXT DEFAULT 'Sin descripción', "
            + COLUMN_IMAGEN + " TEXT DEFAULT 'default_img_disc', "
            + COLUMN_BUSQUEDA + " INTEGER DEFAULT 0);";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CANCIONES);
        db.execSQL(CREATE_TABLE_ARTISTAS);
        db.execSQL(CREATE_TABLE_DISCOS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CANCION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTA);
        onCreate(db);
    }
    public boolean tablaExiste(String nombreTabla) {
        boolean existe = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{nombreTabla});
            existe = cursor.moveToFirst();
            Log.i("SQLite", "Verificando tabla " + nombreTabla + ": " + (existe ? "Existe" : "No existe"));
        } catch (Exception e) {
            Log.e("SQLite", "Error al verificar la tabla " + nombreTabla + ": " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return existe;
    }



    public void incrementarBusqueda(String tabla, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + tabla + " SET busqueda = busqueda + 1 WHERE id = ?", new Object[]{id});
        db.close();
    }


    public List<Musica> obtenerTodasLasCanciones() {
        List<Musica> listaCanciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CANCION, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITULO));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALBUM));
                String artista = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTISTA));
                String ruta = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RUTA));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN));

            } while (cursor.moveToNext());
        }

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Musica cancion = new Musica(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITULO)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ARTISTA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RUTA)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEN))
                );
                listaCanciones.add(cancion);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listaCanciones;
    }

    public int eliminarCancion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CANCION, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public List<Artista> obtenerTodosLosArtistas() {
        List<Artista> listaArtistas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARTISTA, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN));
                if (nombre == null) {
                    nombre = "Desconocido"; // Valor predeterminado
                }
                if (descripcion == null) {
                    descripcion = "Sin descripcion"; // Valor predeterminado
                }

                if (imagen == null) {
                    imagen = "../res/drawable/artista1"; // Valor predeterminado
                }
            } while (cursor.moveToNext());
        }

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Artista artista = new Artista(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPCION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEN))
                );
                listaArtistas.add(artista);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listaArtistas;
    }

    public int eliminarArtista(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ARTISTA, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
    public List<Disco> obtenerTodosLosDiscos() {
        List<Disco> listaDiscos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM discos ORDER BY id DESC LIMIT 10", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String artista = cursor.getString(cursor.getColumnIndexOrThrow("artista"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));

                listaDiscos.add(new Disco(id, titulo, artista, descripcion, imagen));
                Log.i("SQLite", "Cargado disco: " + titulo + " - " + artista);
            } while (cursor.moveToNext());
        } else {
            Log.e("SQLite", "No se encontraron discos en la BD.");
        }

        cursor.close();
        db.close();

        return listaDiscos;
    }


    public int eliminarDisco(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ARTISTA, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }


    //INSERTS
    public long insertarCancion(String titulo, String album, String artista, String ruta, String imagenNombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_ALBUM, album);
        values.put(COLUMN_ARTISTA, artista);
        values.put(COLUMN_RUTA, ruta);
        if(ruta == null || ruta.isEmpty()){
            ruta = "default_song";
        }
        values.put(COLUMN_RUTA, ruta);
        if(imagenNombre == null || imagenNombre.isEmpty()){
            imagenNombre = "default_img";
        }
        values.put(COLUMN_IMAGEN, imagenNombre);
        values.put(COLUMN_BUSQUEDA, 0);
        long result = db.insert(TABLE_CANCION, null, values);
        db.close();
        Log.d("SQLite", "resultado insert: "  + result);
        return result;
    }

    public long insertarArtista(String nombre, String descripcion, String imagenNombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_DESCRIPCION, descripcion);
        if(imagenNombre == null || imagenNombre.isEmpty()){
            imagenNombre = "default_img_art";
        }
        values.put(COLUMN_IMAGEN, imagenNombre);
        values.put(COLUMN_BUSQUEDA, 0);

        long result = db.insert(TABLE_ARTISTA, null, values);
        db.close();
        Log.d("SQLite", "resultado insert: "  + result);
        return result;
    }

    public long insertarDisco(String titulo, String nombre, String descripcion, String imagenNombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_ARTISTA, nombre);
        values.put(COLUMN_DESCRIPCION, descripcion);
        if(imagenNombre == null || imagenNombre.isEmpty()){
            imagenNombre = "default_img_disc";
        }
        values.put(COLUMN_IMAGEN, imagenNombre);
        values.put(COLUMN_BUSQUEDA, 0);

        long result = db.insert(TABLE_DISCOS, null, values);
        db.close();
        Log.d("SQLite", "resultado insert: "  + result);
        return result;
    }

    public List<Disco> obtenerDiscosLimit(int limite) {
        List<Disco> listaDiscos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Discos ORDER BY id DESC LIMIT ?", new String[]{String.valueOf(limite)});

        if (cursor.moveToFirst()) {
            do {
                listaDiscos.add(new Disco(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("artista")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaDiscos;
    }

    public List<Musica> obtenerCancionesLimit(int limite) {
        List<Musica> listaCanciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Canciones ORDER BY id DESC LIMIT ?", new String[]{String.valueOf(limite)});

        if (cursor.moveToFirst()) {
            do {
                listaCanciones.add(new Musica(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("album")),
                        cursor.getString(cursor.getColumnIndexOrThrow("artista")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ruta")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaCanciones;
    }

    public List<Artista> obtenerArtistasLimit(int limite) {
        List<Artista> listaArtistas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Artistas ORDER BY id DESC LIMIT ?", new String[]{String.valueOf(limite)});

        if (cursor.moveToFirst()) {
            do {
                listaArtistas.add(new Artista(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaArtistas;
    }

    void copiarBaseDeDatosSiNoExiste(Context context) {
        try {
            File dbFile = context.getDatabasePath(DATABASE_NAME);
            if (!dbFile.exists()) {  // Si la BD no existe, la copiamos desde assets
                InputStream inputStream = context.getAssets().open(DATABASE_NAME);
                OutputStream outputStream = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
                Log.i("SQLite", "Base de datos copiada desde assets.");
            } else {
                Log.i("SQLite", "La base de datos ya existía, no se copió.");
            }
        } catch (IOException e) {
            Log.e("SQLite", "Error copiando la base de datos: " + e.getMessage());
        }
    }


}
