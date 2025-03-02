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

public class DiscoAdapter extends RecyclerView.Adapter<DiscoAdapter.DiscoViewHolder> {
    private Context context;
    private List<Disco> listaDiscos;

    public DiscoAdapter(Context context, List<Disco> listaDiscos) {
        this.context = context;
        this.listaDiscos = listaDiscos;
    }

    @NonNull
    @Override
    public DiscoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disco, parent, false);
        return new DiscoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoViewHolder holder, int position) {
        Disco disco = listaDiscos.get(position);
        holder.textViewTitulo.setText(disco.getTitulo());
        holder.textViewArtista.setText(disco.getArtista());

        String nombreImagen = disco.getImagen().replace(".jpg", "").replace("png", "");
        @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
        if (resId != 0) {
            holder.imageViewDisco.setImageResource(resId);
        } else {
            holder.imageViewDisco.setImageResource(R.drawable.default_img); // Imagen por defecto
        }


        // Abrir FullScreenActivity al hacer clic
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenActivity.class);
            intent.putExtra("tipo", "disco");
            intent.putExtra("titulo", disco.getTitulo());
            intent.putExtra("descripcion", disco.getDescripcion());
            intent.putExtra("imagen", disco.getImagen());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaDiscos.size();
    }

    public static class DiscoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, textViewArtista;
        ImageView imageViewDisco;

        public DiscoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewArtista = itemView.findViewById(R.id.textViewArtista);
            imageViewDisco = itemView.findViewById(R.id.imageViewDisco);
        }
    }
}
