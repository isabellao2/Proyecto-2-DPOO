package Competencias;

import DeporteConjunto.Equipo;
import Usuarios.Socio;
import java.util.Date;
import java.util.List;
import Partidos.PartidoCompetencia;

public class Competencia {
	private String nombre;
	private String entidad;
	private Date anioTemporada;
	private String formatoJuego;
	private List<EquipoExterno> equiposParticipantes;
	
	public Competencia(String nombre, String entidad, Date anioTemporada, String formatoJuego,
			List<EquipoExterno> equiposParticipantes) {
		super();
		this.nombre = nombre;
		this.entidad = entidad;
		this.anioTemporada = anioTemporada;
		this.formatoJuego = formatoJuego;
		this.equiposParticipantes = equiposParticipantes;
		
	
	}

	public String getNombre() {
		return nombre;
	}

	public String getEntidad() {
		return entidad;
	}

	public Date getAnioTemporada() {
		return anioTemporada;
	}

	public String getFormatoJuego() {
		return formatoJuego;
	}

	public List<EquipoExterno> getEquiposParticipantes() {
		return equiposParticipantes;
	}
	
	public InscripcionCompetencia inscribirEquipo(Equipo e, List<Socio> nomina) {
		InscripcionCompetencia inscripcion = new InscripcionCompetencia(nomina);
		return inscripcion;
	}
	
	public void registrarPartido(PartidoCompetencia p) {
		
		
	}
	public boolean esLiga() {
		return true;
	}
	
	public boolean cumpleRequisitos (Equipo equipo, List<Socio> nomina) {
		return true;
	}
	
	
	
	

}