package vazquez.garcia.aitor.aitorvazquezmusica;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewDiscos, recyclerViewCanciones, recyclerViewArtistas, recyclerViewResultados;
    private TextView textViewMensaje;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        recyclerViewDiscos = findViewById(R.id.recyclerViewDiscos);
        recyclerViewCanciones = findViewById(R.id.recyclerViewCanciones);
        recyclerViewArtistas = findViewById(R.id.recyclerViewArtistas);
        recyclerViewResultados = findViewById(R.id.recyclerViewResultados);
        textViewMensaje = findViewById(R.id.textViewMensaje);

        Button btnDiscos = findViewById(R.id.btnVerMasDiscos);
        Button btnCanciones = findViewById(R.id.btnVerMasCanciones);
        Button btnArtistas = findViewById(R.id.btnVerMasArtistas);

        btnDiscos.setOnClickListener(this::abrirDiscosActivity);
        btnCanciones.setOnClickListener(this::abrirCancionesActivity);
        btnArtistas.setOnClickListener(this::abrirArtistasActivity);

        Button btnAddDiscos = findViewById(R.id.btnAddDisco);
        Button btnAddCanciones = findViewById(R.id.btnAddCancion);
        Button btnAddArtistas = findViewById(R.id.btnAddArtista);

        btnAddDiscos.setOnClickListener(this::addDisco);
        btnAddCanciones.setOnClickListener(this::addCancion);
        btnAddArtistas.setOnClickListener(this::addArtista);

        // Configuración de LayoutManager para listas horizontales
        recyclerViewDiscos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCanciones.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewArtistas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewResultados.setLayoutManager(new LinearLayoutManager(this));


        File dbFile = new File(getDatabasePath("Musica.db").getAbsolutePath());
        if (!dbFile.exists()) {
            copiarBaseDeDatosSiNoExiste();
        } else {
            Log.i("SQLite", "La base de datos ya existía, no se copió.");
            copiarBaseDeDatosSiNoExiste();
        }

// 🔥 Reabrimos la BD y verificamos si tiene datos
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Discos", null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            Log.i("SQLite", "Cantidad de discos en la BD después de copiar: " + count);
        }
        cursor.close();
        db.close();
        // Cargar datos iniciales
        cargarDatosInicio();

        // Configurar la búsqueda
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);  // Desplegar la barra de búsqueda por defecto
        searchView.clearFocus();  // Evitar que el teclado se abra automáticamente
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarEnBD(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    cargarDatosInicio();
                } else {
                    buscarEnBD(newText);
                }
                return false;
            }
        });
    }

    private void buscarEnBD(String query) {
        db = dbHelper.getReadableDatabase();
        List<Object> resultados = new ArrayList<>();

        // Buscar en Discos
        Cursor cursor = db.rawQuery("SELECT * FROM Discos WHERE titulo LIKE ? OR artista LIKE ?", new String[]{"%" + query + "%", "%" + query + "%"});
        while (cursor.moveToNext()) {
            Log.i("SQLite", "dentro del bucle del select discos en busqueda");

            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String artista = cursor.getString(cursor.getColumnIndexOrThrow("artista"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
            resultados.add(new Disco(id, titulo, artista, descripcion, imagen));
        }
        cursor.close();

        // Buscar en Canciones
        cursor = db.rawQuery("SELECT * FROM Canciones WHERE titulo LIKE ? OR artista LIKE ?", new String[]{"%" + query + "%", "%" + query + "%"});
        while (cursor.moveToNext()) {
            Log.i("SQLite", "dentro del bucle del select canciones ne busqueda");
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
            String artista = cursor.getString(cursor.getColumnIndexOrThrow("artista"));
            String ruta = cursor.getString(cursor.getColumnIndexOrThrow("ruta"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
            resultados.add(new Musica(id, titulo, album, artista, ruta, imagen));
        }
        cursor.close();

        // Buscar en Artistas
        cursor = db.rawQuery("SELECT * FROM Artistas WHERE nombre LIKE ?", new String[]{"%" + query + "%"});
        while (cursor.moveToNext()) {
            Log.i("SQLite", "dentro del bucle del artistas en busquedas");
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
            String imagen = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
            resultados.add(new Artista(id, nombre, descripcion, imagen));
        }
        cursor.close();
        db.close();

        if (resultados.isEmpty()) {
            resultados.add(new MensajeResultados("No se han encontrado resultados"));
        }
            textViewMensaje.setVisibility(View.VISIBLE);
            recyclerViewResultados.setAdapter(new ResultadosAdapter(this, resultados));

    }


    private void cargarDatosInicio() {
        db = dbHelper.getReadableDatabase();

        if (db == null) {
            Log.e("SQLite", "No se pudo abrir la base de datos.");
            return;
        }

        // Cargar Discos
        ArrayList<Disco> listaDiscos = new ArrayList<>();
        Cursor cursorDiscos = db.rawQuery("SELECT * FROM Discos ORDER BY busqueda DESC LIMIT 2;", null);
        if (cursorDiscos.moveToFirst()) {
            Log.i("SQLite", "moveToFirst");
            do {
                int id = cursorDiscos.getInt(cursorDiscos.getColumnIndexOrThrow("id"));
                String titulo = cursorDiscos.getString(cursorDiscos.getColumnIndexOrThrow("titulo"));
                String artista = cursorDiscos.getString(cursorDiscos.getColumnIndexOrThrow("artista"));
                String descripcion = cursorDiscos.getString(cursorDiscos.getColumnIndexOrThrow("descripcion"));
                String imagen = cursorDiscos.getString(cursorDiscos.getColumnIndexOrThrow("imagen"));
                listaDiscos.add(new Disco(id, titulo, artista, descripcion, imagen));
                Log.i("SQLite", "Disco encontrado: ID=" + id + ", Título=" + titulo + ", Artista=" + artista);
            } while (cursorDiscos.moveToNext());
        }else {
            Log.e("SQLite", "No se encontraron discos en la BD.");
        }
        cursorDiscos.close();
        recyclerViewDiscos.setAdapter(new DiscoAdapter(this, listaDiscos));

        // Cargar Canciones
        ArrayList<Musica> listaCanciones = new ArrayList<>();
        Cursor cursorCanciones = db.rawQuery("SELECT * FROM Canciones ORDER BY busqueda DESC LIMIT 2;", null);
        if (cursorCanciones.moveToFirst()) {

            Log.i("SQLite", "moveToFirst");
            do {
                int id = cursorCanciones.getInt(cursorCanciones.getColumnIndexOrThrow("id"));
                String titulo = cursorCanciones.getString(cursorCanciones.getColumnIndexOrThrow("titulo"));
                String album = cursorCanciones.getString(cursorCanciones.getColumnIndexOrThrow("album"));
                String artista = cursorCanciones.getString(cursorCanciones.getColumnIndexOrThrow("artista"));
                String ruta = cursorCanciones.getString(cursorCanciones.getColumnIndexOrThrow("ruta"));
                String imagen = cursorCanciones.getString(cursorCanciones.getColumnIndexOrThrow("imagen"));
                listaCanciones.add(new Musica(id, titulo, album, artista, ruta, imagen));
                Log.i("SQLite", "Disco encontrado: ID=" + id + ", Título=" + titulo + ", Artista=" + artista);
            } while (cursorCanciones.moveToNext());
        }else {
            Log.e("SQLite", "No se encontraron canciones en la BD.");
        }
        cursorCanciones.close();
        recyclerViewCanciones.setAdapter(new MusicaAdapter(this, listaCanciones));

        // Cargar Artistas
        ArrayList<Artista> listaArtistas = new ArrayList<>();
        Cursor cursorArtistas = db.rawQuery("SELECT * FROM Artistas ORDER BY busqueda DESC LIMIT 2;", null);
        if (cursorArtistas.moveToFirst()) {
            Log.i("SQLite", "moveToFirst");
            do {

                int id = cursorArtistas.getInt(cursorArtistas.getColumnIndexOrThrow("id"));
                String nombre = cursorArtistas.getString(cursorArtistas.getColumnIndexOrThrow("nombre"));
                String descripcion = cursorArtistas.getString(cursorArtistas.getColumnIndexOrThrow("descripcion"));
                String imagen = cursorArtistas.getString(cursorArtistas.getColumnIndexOrThrow("imagen"));
                listaArtistas.add(new Artista(id, nombre, descripcion, imagen));
                Log.i("SQLite", "Disco encontrado: ID=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion);
            } while (cursorArtistas.moveToNext());
        }else {
            Log.e("SQLite", "No se encontraron artistas en la BD.");
        }
        cursorArtistas.close();
        recyclerViewArtistas.setAdapter(new ArtistaAdapter(this, listaArtistas));

        db.close();
    }

    private void copiarBaseDeDatos() {
        try {
            String destPath = getApplicationContext().getDatabasePath("Musica.db").getPath();
            File file = new File(destPath);

            InputStream inputStream = getApplicationContext().getAssets().open("Musica.db");
            OutputStream outputStream = new FileOutputStream(destPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Log.i("SQLite", "Base de datos copiada con éxito desde assets a: " + destPath);

        } catch (IOException e) {
            Log.e("SQLite", "Error al copiar la base de datos.", e);
        }
    }
    public void copiarBaseDeDatosSiNoExiste() {
        File dbFile = getApplicationContext().getDatabasePath("Musica.db");

        // 🔥 Eliminar la base de datos si existe para forzar la copia desde assets
        if (!dbFile.exists()) {
            dbFile.delete();
            Log.i("SQLite", "Base de datos eliminada para copiar desde assets.");
        }

        copiarBaseDeDatos(); // Copiar la base de datos desde assets
    }



    public void abrirDiscosActivity(View view) {
        Intent intent = new Intent(this, DiscosActivity.class);
        startActivity(intent);
    }

    public void abrirCancionesActivity(View view) {
        Intent intent = new Intent(this, MusicaActivity.class);
        startActivity(intent);
    }

    public void abrirArtistasActivity(View view) {
        Intent intent = new Intent(this, ArtistasActivity.class);
        startActivity(intent);
    }

    public void addDisco(View view) {
        Intent intent = new Intent(this, AddDiscoActivity.class);
        startActivity(intent);
    }

    public void addArtista(View view) {
        Intent intent = new Intent(this, AddArtistaActivity.class);
        startActivity(intent);
    }

    public void addCancion(View view) {
        Intent intent = new Intent(this, AddCancionActivity.class);
        startActivity(intent);
    }




}
