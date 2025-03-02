package vazquez.garcia.aitor.aitorvazquezmusica;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder> {
    private Context context;
    private List<Musica> listaCanciones;

    public MusicaAdapter(Context context, List<Musica> listaCanciones) {
        this.context = context;
        this.listaCanciones = listaCanciones;
    }

    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musica, parent, false);
        return new MusicaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder holder, int position) {
        Musica musica = listaCanciones.get(position);
        holder.titulo.setText(musica.getTitulo());
        holder.album.setText(musica.getAlbum());

        String nombreImagen = musica.getImagen().replace(".jpg", "").replace("png", "");// Por ejemplo, "disco1"
        @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
        if (resId != 0) {
            holder.imagen.setImageResource(resId);
        } else {
            holder.imagen.setImageResource(R.drawable.default_img); // Imagen por defecto
        }



            // Abrir FullScreenActivity al hacer clic
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("tipo", "cancion");
                intent.putExtra("titulo", musica.getTitulo());
                intent.putExtra("artista", musica.getArtista());
                intent.putExtra("album", musica.getAlbum());
                intent.putExtra("ruta", musica.getRuta());
                intent.putExtra("imagen", musica.getImagen());
                context.startActivity(intent);
            });

    }


    @Override
    public int getItemCount() {
        return listaCanciones.size();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, album;
        ImageView imagen;

        public MusicaViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            album = itemView.findViewById(R.id.textViewAlbum);
            imagen = itemView.findViewById(R.id.imageViewMusica);
        }
    }
}
