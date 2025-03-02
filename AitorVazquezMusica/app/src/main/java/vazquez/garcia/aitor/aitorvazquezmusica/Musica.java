package vazquez.garcia.aitor.aitorvazquezmusica;



public class Musica {
    private int id;
    private String titulo;
    private String album;
    private String artista;
    private String ruta;
    private String imagen;

    public Musica(int id, String titulo, String album, String artista, String ruta, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.album = album;
        this.artista = artista;
        this.ruta = ruta;
        this.imagen = imagen;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAlbum() { return album; }
    public String getArtista() { return artista; }
    public String getRuta() { return ruta; }
    public String getImagen() { return imagen; }

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAlbum(String album) { this.album = album; }
    public void setArtista(String artista) { this.artista = artista; }
    public void setRuta(String ruta) { this.ruta = ruta; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    @Override
    public String toString() {
        return "Musica{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", album='" + album + '\'' +
                ", artista='" + artista + '\'' +
                ", ruta='" + ruta + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
