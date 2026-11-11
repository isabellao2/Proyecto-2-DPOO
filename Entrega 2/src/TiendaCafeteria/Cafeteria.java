package TiendaCafeteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Usuarios.Socio;
import Usuarios.Usuario;
import Usuarios.Administrador;
import DeporteIndividual.Reserva;
import DeporteIndividual.Instalacion;

public class Cafeteria {
	
	// Atributos 
	
    private List<Bebida> bebidas;
    private List<Snack> snacks;
    private List<Venta> ventas;
    
    public static final double IMPUESTO_CONSUMO = 0.08; 
    public static final double PROPINA_SUGERIDA = 0.10;  

    	// constructor 
    public Cafeteria() {
        this.bebidas = new ArrayList<>();
        this.snacks = new ArrayList<>();
        this.ventas = new ArrayList<>();
    }

    //Metodos
    
    public Venta vender(Socio comprador, List<Bebida> bebidas, List<Snack> snacks, double propina) {
        if (comprador == null)
            throw new IllegalArgumentException("El comprador es obligatorio");
        boolean sinBebidas = bebidas == null || bebidas.isEmpty();
        boolean sinSnacks = snacks == null || snacks.isEmpty();
        if (sinBebidas && sinSnacks)
            throw new IllegalArgumentException("Debe incluir al menos un producto");
        if (propina < 0)
            throw new IllegalArgumentException("La propina no puede ser negativa");

        
        boolean hayCaliente = false;
        if (!sinBebidas) {
            for (Bebida b : bebidas) {
                if (!puedeDespachar(b, comprador))
                    throw new IllegalArgumentException(
                        "No se puede despachar bebida caliente dentro de esta instalación");
                if (b.isCaliente()) hayCaliente = true;
            }
        }

        double subtotal = 0.0;
        if (!sinBebidas) for (Bebida b : bebidas) subtotal += b.getPrecio();
        if (!sinSnacks)  for (Snack s : snacks)   subtotal += s.getPrecio();

        double impuesto = calcularImpuestoConsumo(subtotal);

        Venta venta = new Venta(LocalDate.now(), "CAFETERIA", subtotal, 0.0, impuesto, propina, comprador);

        if (hayCaliente) comprador.setBebidaCalienteActiva(true); // revisar como solucionar
        comprador.registrarCompra(venta);   
        ventas.add(venta); 
        return venta;
    }

    public boolean puedeDespachar(Bebida b, Socio s) {
        return !(b.isCaliente() && s.estaEnInstalacionSinBebidaCaliente());
    }

       
    public double calcularImpuestoConsumo(double subtotal) {
        return subtotal * IMPUESTO_CONSUMO;
    }

    public double calcularPropinaSugerida(double subtotal) {
        return subtotal * PROPINA_SUGERIDA;
    }
    
    public void agregarBebida(Bebida b) {
    	if (b != null) {
            this.bebidas.add(b);
        }    
    }

    public void agregarSnack(Snack s) {
    	if (s != null) {
            this.snacks.add(s);
        }
    }

    public List<String> informarAlergenos(List<Snack> snacksAComprar) {
    	List<String> reporteAlergenos = new ArrayList<>();
        
        if (snacksAComprar != null) {
            for (Snack s : snacksAComprar) {
                if (s != null && s.tieneAlergenos()) {
                    reporteAlergenos.add("El snack " + s.getNombre() + " contiene alérgenos: " + s.getAlergenos());
                }
            }
        }
        
        return reporteAlergenos;
    }   
    
    public boolean estaEnInstalacionSinBebidaCaliente( Socio comprador, List<Bebida> bebidasAComprar) {
    	if (comprador == null) {
            return false;
        }
        if (comprador.isBebidaCalienteActiva()) {
            return false; 
        }

        boolean incluyeBebidaCaliente = false;
        if (bebidasAComprar != null) {
            for (Bebida b : bebidasAComprar) {
                if (b != null && b.isCaliente()) { 
                    incluyeBebidaCaliente = true;
                    break;
                }
            }
        }
        if (incluyeBebidaCaliente) {
  
            for (Reserva r : comprador.getReservas()) { 
                if (r != null && r.getInstalacion() != null) {
                    String nombreInstalacion = r.getInstalacion().getNombre().toLowerCase();
                   
                    if (nombreInstalacion.contains("padel") || nombreInstalacion.contains("tenis de mesa")) {
                    	return false; 
                    }
                }
            }
        }

        return true; 
    }

    public List<Bebida> getBebidas() {
        return bebidas;
    }

    public List<Snack> getSnacks() {
        return snacks;
    }
    
}