package vazquez.garcia.aitor.aitorvazquezmusica;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.lang.reflect.Field;

public class FullScreenActivity extends AppCompatActivity {
    private TextView textViewTitulo, textViewDescripcion, textViewArtista, textViewAlbum, textViewTiempoActual, textViewDuracion;
    private ImageView imageViewPrincipal;
    private LinearLayout layoutControles;
    private Button btnPlay, btnStop, btnRestart;
    private MediaPlayer mediaPlayer;
    private String rutaCancion;
    private SeekBar seekBarProgreso;
    private Handler handler = new Handler();
    private int audioResId = 0;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_full_screen);

            // Inicializar vistas
            textViewTitulo = findViewById(R.id.textViewTitulo);
            textViewDescripcion = findViewById(R.id.textViewDescripcion);
            textViewArtista = findViewById(R.id.textViewArtista);
            textViewAlbum = findViewById(R.id.textViewAlbum);
            textViewTiempoActual = findViewById(R.id.textViewTiempoActual);
            textViewDuracion = findViewById(R.id.textViewDuracion);
            imageViewPrincipal = findViewById(R.id.imageViewPrincipal);
            layoutControles = findViewById(R.id.layoutControles);
            btnPlay = findViewById(R.id.btnPlay);
            btnStop = findViewById(R.id.btnStop);
            btnRestart = findViewById(R.id.btnRestart);
            seekBarProgreso = findViewById(R.id.seekBarProgreso);

            // Recibir datos del Intent
            Intent intent = getIntent();
            if (intent != null) {
                String tipo = intent.getStringExtra("tipo");

                if ("cancion".equals(tipo)) {
                    mostrarCancion(intent);
                } else if ("disco".equals(tipo)) {
                    mostrarDisco(intent);
                } else if ("artista".equals(tipo)) {
                    mostrarArtista(intent);
                }
            }

            // Configurar botones de reproducción
            btnPlay.setOnClickListener(v -> reproducirCancion());
            btnRestart.setOnClickListener(v -> reiniciarCancion());
            btnStop.setOnClickListener(v -> pausarCancion());

            // Controlar SeekBar manualmente
            seekBarProgreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                        textViewTiempoActual.setText(formatearTiempo(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
    }

    @SuppressLint("DiscouragedApi")
    private void mostrarCancion(Intent intent) {
        textViewTitulo.setText(intent.getStringExtra("titulo"));
        textViewArtista.setText(intent.getStringExtra("artista"));
        textViewAlbum.setText(intent.getStringExtra("album"));
        rutaCancion = intent.getStringExtra("ruta");

        textViewArtista.setVisibility(View.VISIBLE);
        textViewAlbum.setVisibility(View.VISIBLE);
        layoutControles.setVisibility(View.VISIBLE);

        String nombreImagen = intent.getStringExtra("imagen").replace(".jpg", "").replace("png", "");
        @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
        if (resId != 0) {
            imageViewPrincipal.setImageResource(resId);
        } else {
            Log.d("FullScreenActivity", "No se encontro la imagen");
            imageViewPrincipal.setImageResource(R.drawable.default_img_disc);
        }

        Field[] campos = R.raw.class.getFields();
        for (Field campo: campos){
            Log.d("FullScreenActivity", "Archivo raw: " + campo.getName());
        }

// Obtener la canción desde res/raw
        audioResId = getResources().getIdentifier(rutaCancion, "raw", getPackageName());
        if (audioResId == 0) {
            Toast.makeText(this, "No se encontró la canción en res/raw: " + rutaCancion, Toast.LENGTH_LONG).show();
            mediaPlayer = MediaPlayer.create(this, R.raw.default_song);
            if (mediaPlayer != null) {
                seekBarProgreso.setMax(mediaPlayer.getDuration());
                textViewDuracion.setText(formatearTiempo(mediaPlayer.getDuration()));
            }
            Log.e("FullScreenActivity", "No se encontró la canción en res/raw: " + rutaCancion);
        } else {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            if (mediaPlayer != null) {
                seekBarProgreso.setMax(mediaPlayer.getDuration());
                textViewDuracion.setText(formatearTiempo(mediaPlayer.getDuration()));
            }
        }

    }

    private void mostrarDisco(Intent intent) {
        textViewTitulo.setText(intent.getStringExtra("titulo"));
        textViewDescripcion.setText(intent.getStringExtra("descripcion"));
        textViewDescripcion.setVisibility(View.VISIBLE);

        String nombreImagen = intent.getStringExtra("imagen").replace(".jpg", "").replace("png", "");
        @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
        if (resId != 0) {
            imageViewPrincipal.setImageResource(resId);
        } else {
            Log.d("FullScreenActivity", "No se encontro la imagen");
            imageViewPrincipal.setImageResource(R.drawable.default_img_disc);
        }
    }

    private void mostrarArtista(Intent intent) {
        textViewTitulo.setText(intent.getStringExtra("nombre"));
        textViewDescripcion.setText(intent.getStringExtra("descripcion"));
        textViewDescripcion.setVisibility(View.VISIBLE);

        String nombreImagen = intent.getStringExtra("imagen").replace(".jpg", "").replace("png", "");
        @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
        if (resId != 0) {
            imageViewPrincipal.setImageResource(resId);
        } else {
            Log.d("FullScreenActivity", "No se encontro la imagen");
            imageViewPrincipal.setImageResource(R.drawable.default_img_disc);
        }
    }

    private void reproducirCancion() {
        if (rutaCancion != null) {
            // Obtener el ID del recurso en res/raw usando el nombre del archivo sin extensión
            String nombreArchivo = rutaCancion.replace(".mp3", ""); // Quita la extensión si es necesario
            @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(nombreArchivo, "raw", getPackageName());

            if (resId == 0) {
                Log.e("FullScreenActivity", "❌ No se encontró la canción en res/raw: " + rutaCancion);
                return; // Si no encuentra el archivo, no intenta reproducir
            }

            // Liberar el MediaPlayer antes de crear uno nuevo
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Reproducir el audio desde res/raw
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.start();

            // Iniciar la actualización de la barra de progreso
            actualizarBarraProgreso();
        }
    }



    private void pausarCancion() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void reiniciarCancion() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            textViewTiempoActual.setText("0:00");
            seekBarProgreso.setProgress(0);
        }
    }

    private void actualizarBarraProgreso() {
        if (mediaPlayer != null) {
            // Obtener duración de la canción
            int duracionTotal = mediaPlayer.getDuration(); // En milisegundos
            seekBarProgreso.setMax(duracionTotal);

            // Mostrar duración total en formato mm:ss
            textViewDuracion.setText(convertirTiempo(duracionTotal));

            // Actualizar cada 500ms
            Runnable actualizar = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int tiempoActual = mediaPlayer.getCurrentPosition();
                        seekBarProgreso.setProgress(tiempoActual);
                        textViewTiempoActual.setText(convertirTiempo(tiempoActual));
                        seekBarProgreso.postDelayed(this, 500); // Repite cada 500ms
                    }
                }
            };
            seekBarProgreso.post(actualizar);
        }
    }

    // Método para convertir milisegundos a formato mm:ss
    private String convertirTiempo(int milisegundos) {
        int minutos = (milisegundos / 1000) / 60;
        int segundos = (milisegundos / 1000) % 60;
        return String.format("%d:%02d", minutos, segundos);
    }



    private String formatearTiempo(int tiempoMs) {
        int minutos = (tiempoMs / 1000) / 60;
        int segundos = (tiempoMs / 1000) % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
