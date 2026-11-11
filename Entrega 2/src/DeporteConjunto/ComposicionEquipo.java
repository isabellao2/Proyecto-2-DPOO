package DeporteConjunto;

import Excepciones.ClubException;
import Partidos.EstadisticaFutbol;
import Partidos.EstadisticaPartido;
import Partidos.Partido;
import Usuarios.Socio;

public class ComposicionEquipo {
	private final String posicion;
	private final int numeroCamiseta;
    private final Socio jugador;
    private final Equipo equipo;

    public ComposicionEquipo(Socio jugador, Equipo equipo, String posicion, int numeroCamiseta) throws ClubException {
        if (jugador == null || equipo == null) {
            throw new ClubException("ingresa al jugador y al equipo.");
        }
        if (posicion == null || posicion.isBlank()) {
            throw new ClubException("Indica la posición en la que jugará " + jugador.getLogin() + ".");
        }
        this.jugador = jugador;
        this.equipo = equipo;
        this.posicion = posicion.trim();
        this.numeroCamiseta = numeroCamiseta;
    }
    public boolean estaHabilitado() {
        return jugador.isFisicoApto() && !estaSuspendido();
    }
    public boolean estaSuspendido() {
        Partido ultimo = equipo.getUltimoPartidoJugado();
        if (ultimo == null) {
            return false;
        }
        EstadisticaPartido e = ultimo.obtenerEstadistica(jugador);
        return e instanceof EstadisticaFutbol && ((EstadisticaFutbol) e).generaSuspension();
    }

    public int contarAmarillas() {
        int total = 0;
        for (Partido p : equipo.getPartidosJugados()) {
            EstadisticaPartido e = p.obtenerEstadistica(jugador);
            if (e instanceof EstadisticaFutbol) {
                total += ((EstadisticaFutbol) e).getTarjetasAmarillas();
            }
        }
        return total;
    }

    public int contarRojas() {
        int total = 0;
        for (Partido p : equipo.getPartidosJugados()) {
            EstadisticaPartido e = p.obtenerEstadistica(jugador);
            if (e instanceof EstadisticaFutbol && ((EstadisticaFutbol) e).isTarjetasRojas()) {
                total++;
            }
        }
        return total;
    }
    
    public String consultarEstado() {
        String tarjetas = " (amarillas acumuladas: " + contarAmarillas() + ", rojas: " + contarRojas() + ")";
        if (!jugador.isFisicoApto()) {
            return jugador.getLogin() + " está inhabilitado, no apto físicamente" + tarjetas + ".";
        }
        if (estaSuspendido()) {
            return jugador.getLogin() + " está suspendido para el próximo partido de " + equipo.getNombre()
                    + " por tarjetas del partido anterior" + tarjetas;
        }
        return jugador.getLogin() + " está habilitado para jugar con " + equipo.getNombre() + tarjetas + ".";
    }
    
    public String getPosicion() {
		return posicion;
	}
	public int getNumeroCamiseta() {
		return numeroCamiseta;
	}
	public Socio getJugador() {
		return jugador;
	}
	public Equipo getEquipo() {
		return equipo;
	}
	
	
	
    
    


}
