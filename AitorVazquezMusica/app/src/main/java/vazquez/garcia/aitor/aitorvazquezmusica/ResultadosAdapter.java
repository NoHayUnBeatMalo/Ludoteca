package vazquez.garcia.aitor.aitorvazquezmusica;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;


import java.util.List;


public class ResultadosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Object> listaResultados;


    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_MENSAJE = 1;


    public ResultadosAdapter(Context context, List<Object> listaResultados) {
        this.context = context;
        this.listaResultados = listaResultados;
    }


    @Override
    public int getItemViewType(int position) {
        if (listaResultados.get(position) instanceof MensajeResultados) {
            return TYPE_MENSAJE;
        }else{
            return TYPE_NORMAL;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);


        if (viewType == TYPE_MENSAJE) {
            View view = inflater.inflate(R.layout.item_mensaje, parent, false);
            return new MensajeViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_resultado, parent, false);
            return new ResultadoViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = listaResultados.get(position);


        if (holder instanceof MensajeViewHolder) {
            ((MensajeViewHolder) holder).textViewMensaje.setText(((MensajeResultados) item).getMensaje());
        } else if (holder instanceof ResultadoViewHolder) {
            ResultadoViewHolder resultadoHolder = (ResultadoViewHolder) holder;


            if (item instanceof Disco) {
                Disco disco = (Disco) item;
                resultadoHolder.textViewTitulo.setText(disco.getTitulo());
                resultadoHolder.textViewDescripcion.setText("Disco");


                String nombreImagen = disco.getImagen().replace(".jpg", "").replace("png", "");// Por ejemplo, "disco1"
                @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
                if (resId != 0) {
                    ((ResultadoViewHolder) holder).imageView.setImageResource(resId);
                } else {
                    ((ResultadoViewHolder) holder).imageView.setImageResource(R.drawable.default_img); // Imagen por defecto
                }
            } else if (item instanceof Musica) {
                Musica musica = (Musica) item;
                resultadoHolder.textViewTitulo.setText(musica.getTitulo());
                resultadoHolder.textViewDescripcion.setText("Canción");



                String nombreImagen = musica.getImagen().replace(".jpg", "").replace("png", "");// Por ejemplo, "disco1"
                @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
                if (resId != 0) {
                    ((ResultadoViewHolder) holder).imageView.setImageResource(resId);
                } else {
                    ((ResultadoViewHolder) holder).imageView.setImageResource(R.drawable.default_img); // Imagen por defecto
                }

            } else if (item instanceof Artista) {
                Artista artista = (Artista) item;
                resultadoHolder.textViewTitulo.setText(artista.getNombre());
                resultadoHolder.textViewDescripcion.setText("Artista");


                String nombreImagen = artista.getImagen().replace(".jpg", "").replace("png", "");// Por ejemplo, "disco1"
                @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
                if (resId != 0) {
                    ((ResultadoViewHolder) holder).imageView.setImageResource(resId);
                } else {
                    ((ResultadoViewHolder) holder).imageView.setImageResource(R.drawable.default_img); // Imagen por defecto
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return listaResultados.size();
    }


    // ViewHolder para resultados normales
    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, textViewDescripcion;
        ImageView imageView;


        public ResultadoViewHolder(View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            imageView = itemView.findViewById(R.id.imageViewResultado);
        }
    }


    // ViewHolder para mensajes de "No se han encontrado resultados"
    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMensaje;


        public MensajeViewHolder(View itemView) {
            super(itemView);
            textViewMensaje = itemView.findViewById(R.id.textViewMensaje);
        }
    }
}





