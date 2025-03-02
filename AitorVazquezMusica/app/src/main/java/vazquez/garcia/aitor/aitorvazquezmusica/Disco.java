package vazquez.garcia.aitor.aitorvazquezmusica;

public class Disco {
    private int id;
    private String titulo;
    private String artista;
    private String descripcion;
    private String imagen;



    public Disco(int id, String titulo, String artista, String descripcion, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }



    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getArtista() { return artista; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
}
