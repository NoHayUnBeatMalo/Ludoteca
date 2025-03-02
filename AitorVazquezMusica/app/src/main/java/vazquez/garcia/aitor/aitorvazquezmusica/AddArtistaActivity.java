package vazquez.garcia.aitor.aitorvazquezmusica;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddArtistaActivity extends AppCompatActivity {
    private EditText etNombre, etAlbum;
    private Button btnGuardar, btnSeleccionarImagen;
    private ImageView imageViewPreview;
    private DatabaseHelper dbHelper;

    private Uri imageUri;
    private String nombreImagen = "";

    // Lanzador de actividad para seleccionar imagen
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageViewPreview.setImageURI(imageUri);
                    nombreImagen = guardarImagenInternamente(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artista);

        etNombre = findViewById(R.id.etNombreArtista);
        etAlbum = findViewById(R.id.etAlbumArtista);
        btnGuardar = findViewById(R.id.btnGuardarArtista);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagenArtista);
        imageViewPreview = findViewById(R.id.imageViewPreviewArtista);

        dbHelper = new DatabaseHelper(this);

        // Ocultar la imagen por defecto hasta que el usuario seleccione una
        imageViewPreview.setVisibility(View.GONE);

        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
        btnGuardar.setOnClickListener(v -> guardarArtista());
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String guardarImagenInternamente(Uri uri) {
        String nombreArchivo = "artista_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), nombreArchivo);
        copiarArchivo(uri, file);
        return nombreArchivo;
    }

    private void copiarArchivo(Uri uri, File destino) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(destino)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", "Error al copiar archivo: " + e.getMessage());
        }
    }

    private void guardarArtista() {
        String nombre = etNombre.getText().toString().trim();
        String album = etAlbum.getText().toString().trim();

        if (nombre.isEmpty() || album.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nombreImagen.isEmpty()) {
            nombreImagen = "default_img_disc.jpg";
        }

        long result = dbHelper.insertarArtista(nombre, album, nombreImagen);
        if (result != -1) {
            Toast.makeText(this, "Artista guardado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar el artista", Toast.LENGTH_SHORT).show();
        }
    }
}


