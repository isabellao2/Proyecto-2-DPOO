package TiendaCafeteria;

public class Articulo {
    private String nombre;
    private String categoria;
    private double precio;
    private int cantidadDisponible;
     private String ubicacion = "Principal";

    public Articulo(String nombre, String categoria, double precio, int cantidadDisponible) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
    }

    public boolean estaAgotado() {
        return this.cantidadDisponible <= 0;
    }

    public void mover(String nuevaUbicacion) {
        if (nuevaUbicacion == null || nuevaUbicacion.isBlank()) {
            throw new IllegalArgumentException("Indica la nueva ubicación del artículo");
        }
        this.ubicacion = nuevaUbicacion.trim();
    }

    public void abastecer(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a abastecer debe ser mayor a cero");
        }
        this.cantidadDisponible += cantidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
}
