package vazquez.garcia.aitor.aitorvazquezmusica;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ArtistaAdapter extends RecyclerView.Adapter<ArtistaAdapter.ArtistaViewHolder> {
    private Context context;
    private List<Artista> listaArtistas;

    public ArtistaAdapter(Context context, List<Artista> listaArtistas) {
        this.context = context;
        this.listaArtistas = listaArtistas;
    }

    @NonNull
    @Override
    public ArtistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artista, parent, false);
        return new ArtistaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistaViewHolder holder, int position) {
        Artista artista = listaArtistas.get(position);

        holder.textViewNombre.setText(artista.getNombre());
        // Obtener el nombre de la imagen desde la base de datos
        String nombreImagen = artista.getImagen().replace(".jpg", "").replace("png", "");// Por ejemplo, "disco1"

        // Obtener el ID del recurso en drawable
        @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());

        // Si existe en drawable, se carga la imagen; si no, se usa una imagen por defecto
        if (resId != 0) {
            holder.imageViewArtista.setImageResource(resId);
        } else {
            holder.imageViewArtista.setImageResource(R.drawable.default_img); // Imagen por defecto
        }


        // Abrir FullScreenActivity al hacer clic
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenActivity.class);
            intent.putExtra("tipo", "artista");
            intent.putExtra("nombre", artista.getNombre());
            intent.putExtra("descripcion", artista.getDescripcion());
            intent.putExtra("imagen", artista.getImagen());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaArtistas.size();
    }

    public static class ArtistaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewDescripcion;
        ImageView imageViewArtista;

        public ArtistaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            imageViewArtista = itemView.findViewById(R.id.imageViewArtista);
        }
    }
}
