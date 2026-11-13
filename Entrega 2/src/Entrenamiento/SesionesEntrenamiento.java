package Entrenamiento;

import Excepciones.ClubException;
import DeporteIndividual.Instalacion;
import Usuarios.Entrenador;
import Usuarios.Socio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SesionesEntrenamiento {
	
	private final LocalDate fecha;
    private final LocalTime hora;
    private final int duracion;
    private final Instalacion instalacion;
    private final List<Socio> jugConvocados;
    private final List<Asistencia> asistencias;
    
    public SesionesEntrenamiento(LocalDate fecha, LocalTime hora, int duracion, Entrenador entrenador,
			DeporteIndividual.Instalacion instalacion) {
		super();
		this.fecha = fecha;
		this.hora = hora;
		this.duracion = duracion;
		this.instalacion = instalacion;
		this.jugConvocados = new ArrayList <> ();
		this.asistencias = new ArrayList <> ();
	}
    

    public LocalDateTime getInicio() {
        return fecha.atTime(hora);
    }

    public LocalDateTime getFin() {
        return getInicio().plusMinutes(duracion);
    }

    public boolean disponibleIns(LocalDateTime inicio, LocalDateTime fin) {
        return !instalacion.estaOcupada(inicio, fin);
    }

    public void convocar(Socio s) throws ClubException {
        if (s == null) {
            throw new ClubException("Elige al jugador que quieres convocar.");
        }
        if (s.getComposicion().isEmpty()) {
            throw new ClubException(s.getLogin() + " no pertenece a ningún equipo, así que no puede ser convocado.");
        }
        if (fueConvocado(s)) {
            throw new ClubException(s.getLogin() + " ya fue convocado a esta sesión.");
        }
        jugConvocados.add(s);
    }

    public boolean fueConvocado(Socio s) {
        return jugConvocados.contains(s);
    }

    public Asistencia registrarAsistencia(Socio s, String estado) throws ClubException {
        if (s == null || !fueConvocado(s)) {
            throw new ClubException((s == null ? "Ese jugador" : s.getLogin())
                    + " no fue convocado a esta sesión, así que no podemos registrar su asistencia.");
        }
        Asistencia existente = getAsistencia(s);
        if (existente != null) {
            existente.setEstado(estado);
            return existente;
        }
        Asistencia nueva = new Asistencia(s, estado);
        asistencias.add(nueva);
        return nueva;
    }

    public Asistencia registrarAsistencia(Socio s, String estado, int nota, String comentario) throws ClubException {
        Asistencia a = registrarAsistencia(s, estado);
        a.asignarEvaluacion(nota, comentario);
        return a;
    }

    public Asistencia getAsistencia(Socio s) {
        for (Asistencia a : asistencias) {
            if (a.getSocio() == s) {
                return a;
            }
        }
        return null;
    }


public List<Asistencia> getAsistencias() {
    return Collections.unmodifiableList(asistencias);
}
    
    public List<Socio> getJugConvocados() {
        return Collections.unmodifiableList(jugConvocados);
    }
}
