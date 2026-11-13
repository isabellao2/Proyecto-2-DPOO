package TiendaCafeteria;

import java.time.LocalDate;

import Excepciones.ClubException;
import Torneos.BonoDescuento;
import Usuarios.Usuario;

public class Venta {
    private LocalDate fecha;
    private String rubro;
    private double impuesto;
    private double propina;
    private double valor;
    private double descuento;
    private Usuario comprador;
    private BonoDescuento bonoAplicado;
    private boolean usaPuntos;
    private String codigoDescuentoEmpleado;
// nuevo atributo 
    public static final double TASA_PUNTOS = 0.02;

    	// constructor
    public Venta(LocalDate fecha, String rubro, double valor, double descuento, double impuesto, double propina, Usuario comprador) {
        this.fecha = fecha;
        this.rubro = rubro;
        this.valor = valor;
        this.descuento = descuento;
        this.impuesto = impuesto;
        this.propina = propina;
        this.comprador = comprador;
    }

     public double calcularTotal() {
        return (this.valor + this.impuesto + this.propina) - this.descuento;
    }

    public int calcularPuntosFidelidad() {
        return (int) Math.max(0, (valor - descuento) * TASA_PUNTOS);
    }

    public boolean esRubro(String rubroBuscado) {
        if (rubroBuscado == null || this.rubro == null) {
            return false;
        }
        return this.rubro.equalsIgnoreCase(rubroBuscado);
    }

    //Nuevos de Torneo

    public void aplicarBonoTorneo(BonoDescuento bono) throws ClubException {
        if (bono == null) {
            return;
        }
        if (bono.isUsado()) {
            throw new ClubException("Este bono de descuento ya fue utilizado anteriormente.");
        }
        // Restricción: No es acumulable con otros descuentos
        if (usaPuntos || (codigoDescuentoEmpleado != null && !codigoDescuentoEmpleado.isBlank())) {
            throw new ClubException("El bono de descuento de torneo recreativo no es acumulable con puntos ni con códigos de empleado.");
        }
        
        this.bonoAplicado = bono;
        bono.usarBono(); 
    }
    
    // getters y setters 

    public LocalDate getFecha() { 
    	return fecha; 
    	}
    public String getRubro() { 
    	return rubro; 
    	}
    public double getValor() { 
    	return valor; 
    	}
    public Usuario getComprador() {
    	return comprador; 
    	}
      public double getImpuesto() {
        return impuesto;
    }
      public double getPropina() {
        return propina;
    }
    public double getDescuento() {
        return descuento;
    }
    public void setComprador(Usuario comprador) {
    	this.comprador = comprador; 
    	}
    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
}