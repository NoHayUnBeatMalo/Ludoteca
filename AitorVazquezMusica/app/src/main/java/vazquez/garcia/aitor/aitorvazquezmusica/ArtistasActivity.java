package vazquez.garcia.aitor.aitorvazquezmusica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArtistasActivity extends AppCompatActivity {
    private RecyclerView recyclerViewArtistas;
    private ArtistaAdapter artistaAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artistas);

        // Vincular RecyclerView con el XML
        recyclerViewArtistas = findViewById(R.id.recyclerViewArtistas);
        recyclerViewArtistas.setLayoutManager(new LinearLayoutManager(this));


        // Instanciar el DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Cargar datos de la base de datos
        cargarArtistas();
    }

    private void cargarArtistas() {
        List<Artista> listaArtistas = dbHelper.obtenerTodosLosArtistas();

        // Si no hay artistas en la BD, mostramos artistas por defecto
        if (listaArtistas.isEmpty()) {
            Log.i("SQLite", "No hay artistas en la BD. Mostrando artistas por defecto.");
            listaArtistas.add(new Artista(1, "Artista Desconocido", "Sin descripción", "default_artista.jpg"));
            listaArtistas.add(new Artista(2, "Otro Artista", "Sin información", "default_artista.jpg"));
        }else {
            Log.i("SQLite", "Discos obtenidos de la BD:");
            for (Artista artista : listaArtistas) {
                Log.i("SQLite", "ID: " + artista.getId() + ", Título: " + artista.getNombre() + ", Artista: " + artista.getDescripcion());
            }
        }

        // Configurar el adaptador y asignarlo al RecyclerView
        artistaAdapter = new ArtistaAdapter(this, listaArtistas);
        recyclerViewArtistas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewArtistas.setAdapter(artistaAdapter);

    }


}
