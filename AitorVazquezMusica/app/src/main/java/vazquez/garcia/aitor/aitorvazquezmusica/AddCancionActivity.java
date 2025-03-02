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


public class AddCancionActivity extends AppCompatActivity {
    private static final int PICK_AUDIO_REQUEST = 1;
    private EditText etTitulo, etAlbum, etArtista;
    private Button btnGuardar, btnSeleccionarImagen, btnSeleccionarAudio;
    private ImageView imageViewPreview;
    private DatabaseHelper dbHelper;


    private Uri imageUri, audioUri;
    private String imageName = "", audioName = "";


    // Lanzador de actividad para seleccionar imagen
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageViewPreview.setImageURI(imageUri);
                    imageViewPreview.setVisibility(View.VISIBLE);
                    imageName = guardarImagenInternamente(imageUri);
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cancion);


        etTitulo = findViewById(R.id.etTituloCancion);
        etAlbum = findViewById(R.id.etAlbumCancion);
        etArtista = findViewById(R.id.etArtistaCancion);
        btnGuardar = findViewById(R.id.btnGuardarCancion);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagenCancion);
        btnSeleccionarAudio = findViewById(R.id.btnSeleccionarAudio);
        imageViewPreview = findViewById(R.id.imageViewPreviewCancion);


        dbHelper = new DatabaseHelper(this);


        // Ocultar la imagen hasta que el usuario seleccione una
        imageViewPreview.setVisibility(View.GONE);


        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
        btnSeleccionarAudio.setOnClickListener(v -> seleccionarAudio());
        btnGuardar.setOnClickListener(v -> guardarCancion());
    }


    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }


    private void seleccionarAudio() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }


    // Guardar imagen en almacenamiento interno
    private String guardarImagenInternamente(Uri uri) {
        String nombreArchivo = "cancion_" + System.currentTimeMillis() + ".jpg";
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


    private void guardarCancion() {
        String titulo = etTitulo.getText().toString().trim();
        String album = etAlbum.getText().toString().trim();
        String artista = etArtista.getText().toString().trim();


        if (titulo.isEmpty() || album.isEmpty() || artista.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        // Si el usuario no selecciona una imagen, se usa la imagen por defecto
        if (imageName.isEmpty()) {
            imageName = "default_img.jpg";
        }


        long resultado = dbHelper.insertarCancion(titulo, album, artista, audioName, imageName);


        if (resultado != -1) {
            Toast.makeText(this, "Canción guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la canción", Toast.LENGTH_SHORT).show();
        }
    }
}





