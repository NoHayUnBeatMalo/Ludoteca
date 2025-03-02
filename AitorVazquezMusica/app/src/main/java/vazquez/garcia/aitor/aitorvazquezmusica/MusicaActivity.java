package vazquez.garcia.aitor.aitorvazquezmusica;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MusicaActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMusica;
    private MusicaAdapter musicaAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_musica);

        recyclerViewMusica = findViewById(R.id.recyclerViewMusica);
        recyclerViewMusica.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        cargarMusica();
    }

    private void cargarMusica() {
        List<Musica> listaCanciones = dbHelper.obtenerTodasLasCanciones();

        if(listaCanciones.isEmpty()){
            Log.i("SQLite", "No hay artistas en la BD. Mostrando artistas por defecto.");
            listaCanciones.add(new Musica(1, "Cancion Desconocida", "Sin información", "Desconocido", "default_song.mp3","default_artista.jpg"));
            listaCanciones.add(new Musica(2, "Otra Cancion", "Sin información", "Desconocido","default_song.mp3", "default_artista.jpg"));
        }else {
            Log.i("SQLite", "Discos obtenidos de la BD:");
            for (Musica cancion : listaCanciones) {
                Log.i("SQLite", "ID: " + cancion.getId() + ", Título: " + cancion.getTitulo() + ", Artista: " + cancion.getArtista());
            }
        }
        musicaAdapter = new MusicaAdapter(this, listaCanciones);
        recyclerViewMusica.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMusica.setAdapter(musicaAdapter);
    }


}
