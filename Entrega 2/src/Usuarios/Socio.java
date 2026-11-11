package Usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DeporteConjunto.ComposicionEquipo;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Excepciones.ClubException;
import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.InscripcionIndividual;
import DeporteIndividual.Instalacion;
import DeporteIndividual.PartidoPractica;
import DeporteIndividual.PuntajeRanking;
import DeporteIndividual.Reserva;
import Partidos.EstadisticaPartido;
import Partidos.Partido;


public class Socio extends Usuario {

   private final int edad;
   private boolean fisicoApto;
   private boolean bebidaCalienteActiva;
   private final List<ComposicionEquipo> composicion;
   private final List<InscripcionIndividual> inscripciones;
   private final List<PuntajeRanking> rankings;
   private final List<Reserva> reservas;
   private final List<Partido> partidos;
   private final List<PartidoPractica> partidosPractica;

   public Socio(String login, String contrasena, int edad) throws ClubException {
	      super(login, contrasena);
	      if (edad <= 0 || edad > 120) {
	          throw new ClubException("La edad " + edad + " no esta en el rango . Escribe una edad entre 1 y 100, por favor.");
	        }
	        this.edad = edad;
	        this.fisicoApto = true;
	        this.bebidaCalienteActiva = false;
	        this.composicion = new ArrayList<>();
	        this.inscripciones = new ArrayList<>();
	        this.rankings = new ArrayList<>();
	        this.reservas = new ArrayList<>();
	        this.partidos = new ArrayList<>();
            this.partidosPractica = new ArrayList<>();
	    }

   public boolean isFisicoApto() {
	return fisicoApto;
   }

   public void setFisicoApto(boolean fisicoApto) {
	this.fisicoApto = fisicoApto;
   }

   public boolean isBebidaCalienteActiva() {
	return bebidaCalienteActiva;
   }

   public void setBebidaCalienteActiva(boolean bebidaCalienteActiva) {
	this.bebidaCalienteActiva = bebidaCalienteActiva;
   }

   public int getEdad() {
	return edad;
   }

   public List<ComposicionEquipo> getComposicion() {
	return composicion;
   }

   public List<InscripcionIndividual> getInscripciones() {
	return inscripciones;
   }

   public List<PuntajeRanking> getRankings() {
	return rankings;
   }

   public List<Reserva> getReservas() {
	return reservas;
   }

   public List<Partido> getPartidos() {
	return partidos;
   }
   
   public ComposicionEquipo inscribirseEnEquipo(Equipo equipo, String posicion, int camiseta) throws ClubException {
        if (equipo == null) {
            throw new ClubException("Elige el equipo en el que quieres estar.");
        }
        return equipo.agregarJugador(this, posicion, camiseta);
    }

   public boolean perteneceAEquipoDe(DeporteConjunto d) {
    for (ComposicionEquipo c : composicion) {
        if (c.getEquipo().getDeporte().equals(d)) {
            return true;
        }
    }
    return false;
}
   public boolean estaHabilitadoParaJugar(DeporteConjunto d) {
   ComposicionEquipo c = getComposicion(d);
   return c != null && c.estaHabilitado();
   }
   
   public EstadisticaPartido consultarEstadisticasPartido(Partido partido) throws ClubException {
       if (partido == null) {
           throw new ClubException("Elige el partido del que quieres ver estadísticas");
       }
       EstadisticaPartido estadistica = partido.getEstadistica(this);
       if (estadistica == null) {
           throw new ClubException("No encontramos estadísticas tuyas en ese partido");
       }
       return estadistica;
   }
   public InscripcionIndividual inscribirseEnDisciplina(DisciplinaIndividual d, String nivel) throws ClubException {
       if (d == null) {
           throw new ClubException("Elige la disciplina en la que quieres inscribirte.");
       }
       if (d.estaInscrito(this)) {
           throw new ClubException("¡Ya estás inscrito en " + d.getNombre() + " No puedes inscribirte otra vez ");
       }
       InscripcionIndividual inscripcion = new InscripcionIndividual(this, d, nivel);
       PuntajeRanking ranking = new PuntajeRanking(this, d, InscripcionIndividual.eloInicialPara(ins.getNivel()));
       d.registrarInscripcion(inscripcion);
       inscripciones.add(inscripcion);
       rankings.add(ranking);
       return inscripcion;
   }

   public InscripcionIndividual getInscripcion(DisciplinaIndividual d) {
	   for (InscripcionIndividual inscrip : inscripciones) {
       if (inscrip.getDisciplina() == d) {
            return inscrip;
        }
    }
    return null;
}

    public List<PartidoPractica> getPartidosPractica() {
       return partidosPractica;
   }

   public PartidoPractica programarPartidoPractica(Socio rival, DisciplinaIndividual d, Instalacion i,
           LocalDateTime inicio, LocalDateTime fin) throws ClubException {
       if (d == null || !d.estaInscrito(this)) {
           throw new ClubException("Primero debes inscribirte en la disciplina para programar una práctica.");
       }
       PartidoPractica partido = new PartidoPractica(d, this, rival, inicio.toLocalDate());
       reservarInstalacion(i, inicio, fin, 2, "SENCILLOS");
       partidosPractica.add(partido);
       return partido;
   }

   
   public PuntajeRanking obtenerRanking (DisciplinaIndividual d) throws ClubException {
	   for (PuntajeRanking r: rankings) {
		   if (r.getdisciplina()==d) {
			   return r;
		   }
       }
       throw new ClubException("Aún no tienes puntaje en  " + (d == null ? "esa disciplina" : d.getNombre())
               + ". Inscríbete.");
	   }
	   
   public List<EstadisticaPartido> consultarEstadisticasPorDeporte(DeporteConjunto deporte) throws ClubException {
       if (deporte == null) {
           throw new ClubException("Elige un deporte del que quieres ver estadistica acumulada.");
       }
       List<EstadisticaPartido> resultado = new ArrayList<>();
       for (Partido p : partidos) {
           if (p.getDeporte().equals(deporte)) {
               EstadisticaPartido e = p.getEstadistica(this);
               if (e != null) {
                   resultado.add(e);
               }
           }
       }
       return resultado;
   }
   
   public ComposicionEquipo getComposicion(DeporteConjunto d) {
       for (ComposicionEquipo c : composicion) {
           if (c.getEquipo().getDeporte().equals(d)) {
               return c;
           }
       }
       return null;
   }
   
   public Reserva reservarInstalacion(Instalacion i, LocalDateTime inicio, LocalDateTime fin,
           int numeroJugadores, String modalidad) throws ClubException {
	   if (i == null) {
		   throw new ClubException("Elige la instalación que quieres reservar.");
	   }
	   DisciplinaIndividual disciplina = buscarDisciplinaDe(i);
	   if (disciplina == null) {
		   throw new ClubException("Para reservar " + i.getNombre()
		   + " primero debes inscribirte en la disciplina");
	   }
	   if (inicio == null || fin == null || !inicio.isBefore(fin)) {
		   throw new ClubException("El horario de la reserva no es válido");
	   }
	   if (!i.aceptaModalidad(modalidad, numeroJugadores)) {
		   throw new ClubException("La instalación " + i.getNombre() + " no admite la modalidad "
				   + modalidad + " con " + numeroJugadores + " jugadores");
	   }
	   if (i.estaOcupada(inicio, fin)) {
		   throw new ClubException("Lo siento" + i.getNombre() + " ya está reservada en ese horario. "
				   + "Prueba con otro bloque.");
	   }
	   if (disciplina.alcanzoMaximoSimultaneas(inicio, fin)) {
		   throw new ClubException("En ese horario ya se están usando todas las instalaciones simultáneas permitidas para "
				   + disciplina.getNombre() + ". Intenta en otro bloque.");
	   }
	   for (Reserva r : reservas) {
		   if (r.coincideCon(inicio, fin)) {
			   throw new ClubException("Ya tienes otra reserva que se cruza con ese horario.");
		   }
	   }	
	   Reserva reserva = new Reserva(inicio, fin, numeroJugadores, modalidad, this, i);
	   i.agregarReserva(reserva);
	   reservas.add(reserva);
	   return reserva;
   	}
   
   private DisciplinaIndividual buscarDisciplinaDe(Instalacion i) {
	 for (InscripcionIndividual ins : inscripciones){
        if (ins.getDisciplina().tieneInstalacion(i)) {
               return ins.getDisciplina();
           }
       } 
	return null;
}

   public void agregarRegistroMedico(String descripcion, boolean apto, String fisio) throws ClubException {
	    if (descripcion == null || descripcion.isEmpty()) {
	        throw new ClubException("Escribe una descripción para el registro médico.");
	    }
	    if (fisio == null || fisio.isBlank()) {
	        throw new ClubException("Indica el nombre del fisioterapeuta que hizo la valoración.");
	    }
	    this.fisicoApto = apto;
	}

	public boolean estaEnInstalacionSinBebidaCaliente() {
	    LocalDateTime ahora = LocalDateTime.now();
	    for (Reserva r : reservas) {
	        if (!ahora.isBefore(r.getInicio()) && ahora.isBefore(r.getFin())) {
	            return !bebidaCalienteActiva;
	        }
	    }
	    return false;
	}
   

	public void registrarComposicion(ComposicionEquipo c) {
		if (c != null && !composicion.contains(c)) {
        composicion.add(c);
		}
	}

	public void retirarComposicion(ComposicionEquipo c) {
		composicion.remove(c);
	}

	public void registrarPartido(Partido p) {
		if (p != null && !partidos.contains(p)) {
			partidos.add(p);
    }
}
	}




