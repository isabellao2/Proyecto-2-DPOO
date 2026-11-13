package Usuarios;

import Excepciones.ClubException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Empleado extends Usuario{
	public static final double descuento_socio = 0.08;
    public static final double descuento_empleado= 0.15;

    private final String codigoDescuento;
    private final List<Turno> turnos;
    private final List<String> sugerenciasPendientes;
    private final List<String> sugerenciasAprobadas;
    
    
	public Empleado(String login, String contraseña, String codigoDescuento)throws ClubException {
		super(login, contraseña);
		this.codigoDescuento = codigoDescuento;
		this.turnos = new ArrayList<>();
		this.sugerenciasPendientes = new ArrayList<>();
		this.sugerenciasAprobadas = new ArrayList<>();
	}
    
    public boolean esCodigoEmpleado (String codigo) {
    	return codigo != null && codigoDescuento.equalsIgnoreCase(codigo.trim());
    }
	
    public void agregarTurno (Turno t) throws ClubException {
        if (t == null) {
            throw new ClubException("No se encontro el turno que quieres agregar");
        }
        if (t.getEmpleado() != this) {
            throw new ClubException("Ese turno pertenece a otro empleado, no podemos agregarlo aquí.");
        }
        if (estaDisponibleEn(t.getDia(), t.getHora())) {
            throw new ClubException("Ya tienes un turno el " + t.getDia() + " a las " + t.getHora() + ".");
        }
        turnos.add(t);}

    public void actualizarTurno (Turno actual, Turno nuevo) 
    	throws ClubException {
            if (actual == null || !turnos.contains(actual)) {
                throw new ClubException ("El turno que quieres cambiar no está entre tus turnos.");
            }
            if (nuevo == null) {
                throw new ClubException ("Indica el turno nuevo para poder actualizarlo.");
            }
            if (estaDisponibleEn(nuevo.getDia(), nuevo.getHora())) {
                throw new ClubException ("Ya tienes un turno el " + nuevo.getDia() + " a las " + nuevo.getHora()
                        + ", elige otro horario.");
            }
            actual.reprogramar(nuevo.getDia(), nuevo.getHora());}

	public boolean estaDisponibleEn (DayOfWeek dia, LocalTime hora) {
		for (Turno t : turnos) {
			if (t.coincideCon(dia, hora)) {
				return true;
            }
		}
		return false;  
    }

    public void solicitarCambioTurno(Turno actual, Turno nuevo) throws ClubException {
        if (actual == null || !turnos.contains(actual)) {
            throw new ClubException("El turno que quieres cambiar no está entre tus turnos.");
        }
        if (nuevo != null && estaDisponibleEn(nuevo.getDia(), nuevo.getHora())) {
            throw new ClubException("Ya tienes un turno el " + nuevo.getDia() + " a las " + nuevo.getHora()
                    + ", prueba con otro horario.");
        }
        actual.solicitarCambio(nuevo);
    }

    public String sugerirProducto(String descripcion) throws ClubException {
        if (descripcion == null || descripcion.isBlank()) {
            throw new ClubException("qué producto te gustaría sugerir para poder registrarlo.");
        }
        String limpia = descripcion.trim();
        sugerenciasPendientes.add(limpia);
        return limpia;
    }
    
    void resolverSugerencia(String sugerencia, boolean aprobar) throws ClubException {
        if (sugerencia == null || !sugerenciasPendientes.contains(sugerencia.trim())) {
            throw new ClubException("No encontramos esa sugerencia entre las pendientes de " + getLogin() + ".");
        }
        sugerenciasPendientes.remove(sugerencia.trim());
        if (aprobar) {
            sugerenciasAprobadas.add(sugerencia.trim());
        }
    }
    
    
    public String getCodigoDescuento() {
        return codigoDescuento;
    }

    //cambio 
    public double getPorcentajeDescuento(Usuario comprador) {
          if (comprador == this) {
            return descuento_empleado;   // el empleado usa su propio código: 15%
        }
        return descuento_socio;          // cualquier otro usa el código compartido: 8%
    }

    public List<Turno> getTurnos() {
        return Collections.unmodifiableList(turnos);
    }

    public List<String> getSugerenciasPendientes() {
        return Collections.unmodifiableList(sugerenciasPendientes);
    }

    public List<String> getSugerenciasAprobadas() {
        return Collections.unmodifiableList(sugerenciasAprobadas);
    }



}
    	
    
    
    

