package vazquez.garcia.aitor.aitorvazquezmusica;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class    DiscosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewDiscos;
    private DiscoAdapter discoAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discos); // Asegúrate de que el layout es correcto

        // Vincular RecyclerView con el XML
        recyclerViewDiscos = findViewById(R.id.recyclerViewDiscos);
        recyclerViewDiscos.setLayoutManager(new LinearLayoutManager(this));



        // Instanciar el DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Cargar datos desde la base de datos
        cargarDiscos();
    }

    private void cargarDiscos() {
        // Obtener los discos desde la base de datos
        List<Disco> listaDiscos = dbHelper.obtenerTodosLosDiscos();

        // Verificar si la lista tiene elementos
        if (listaDiscos.isEmpty()) {
            Log.i("SQLite", "No hay discos en la BD. Mostrando discos por defecto.");
            listaDiscos.add(new Disco(1, "Disco Desconocido", "Artista Desconocido", "Sin descripción", "disco1.jpg"));
            listaDiscos.add(new Disco(2, "Otro Disco", "Otro Artista", "Sin información", "disco2.jpg"));
        } else {
            Log.i("SQLite", "Discos obtenidos de la BD:");
            for (Disco disco : listaDiscos) {
                Log.i("SQLite", "ID: " + disco.getId() + ", Título: " + disco.getTitulo() + ", Artista: " + disco.getArtista());
            }
        }


        // Configurar el adaptador y asignarlo al RecyclerView
        discoAdapter = new DiscoAdapter(this, listaDiscos);
        recyclerViewDiscos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDiscos.setAdapter(discoAdapter);

    }



}
