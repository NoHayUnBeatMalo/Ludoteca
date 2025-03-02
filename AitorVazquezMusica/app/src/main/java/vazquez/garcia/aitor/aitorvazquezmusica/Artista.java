package vazquez.garcia.aitor.aitorvazquezmusica;

public class Artista {
    private int id;
    private String nombre;
    private String descripcion;
    private String imagen;

    public Artista(int id ,String nombre, String descripcion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getDescripcion() { return descripcion; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
}
