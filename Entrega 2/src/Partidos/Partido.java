package Partidos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Usuarios.Socio;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;

public abstract class Partido {

	// atributos
    private Date fecha;
    private int marcadorLocal;
    private int marcadorVisitante;
    private DeporteConjunto deporte;
    private List<EstadisticaPartido> estadisticas;
    private List<Socio> participantes;
    protected List<Equipo> equiposParticipantes;

    // construtctor
    public Partido(Date fecha, DeporteConjunto deporte) {
        this.fecha = fecha;
        this.deporte = deporte;
        this.marcadorLocal = 0;
        this.marcadorVisitante = 0;
        this.estadisticas = new ArrayList<EstadisticaPartido>();
        this.participantes = new ArrayList<Socio>();
    }
    
    // metodos  
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getMarcadorLocal() {
        return marcadorLocal;
    }

    public int getMarcadorVisitante() {
        return marcadorVisitante;
    }
    
    public DeporteConjunto getDeporte() { 
        return deporte; 
    }
    
    public void setDeporte(DeporteConjunto deporte) { 
        this.deporte = deporte; 
    }
    
    public List<Socio> getParticipantes() { 
        return participantes; 
    }
    
    public void agregarParticipante(Socio socio) { 
        this.participantes.add(socio); 
    }

    	
    public boolean puedeJugarse(Equipo equipo) {
     /** Restricción de mínimo (7 fútbol, 5 baloncesto, 6 voleibol)  */
    	if (equiposParticipantes == null || equiposParticipantes.isEmpty()) {
            return false;
        }
        if (equipo != null && !equiposParticipantes.contains(equipo)) {
            return false;
        }
        for (Equipo e : equiposParticipantes) {
            if (e != null && !e.cumpleMinimoJugadores()) {
                return false;
            }
        }
        return true;}
   
        

    public void registrarMarcador(int local, int visitante) {
    	/** marcador final de cada partido y sin empate en baloncesto ni voleibol. En voleibol el marcador son sets */
        
    	this.marcadorLocal = local;
        this.marcadorVisitante = visitante;
    }

    public boolean hayEmpate() {
    	/** puntos de liga: en fútbol el empate da 1 punto */
    	
        return this.marcadorLocal == this.marcadorVisitante;
    }

    public EstadisticaPartido obtenerEstadistica(Socio socio) {
    	 /**Devuelve null si el jugador no tiene estadística */
    	for (EstadisticaPartido estadistica : estadisticas) {
            if (estadistica.getSocio().equals(socio)) 
   
            	return estadistica;
        }
        return null;
    }
    // agregar metodo
    public boolean corresponde(EstadisticaPartido estadistica) { 
        if (estadistica == null || this.deporte == null) {
            return false;
        }
        
        String nombreDeporte = DeporteConjunto.normalizar(this.deporte.getNombre());
        
        // Validar según el deporte del partido
        if (nombreDeporte.contains("futbol") && estadistica instanceof EstadisticaFutbol) {
            return true;
        }
        if (nombreDeporte.contains("baloncesto") && estadistica instanceof EstadisticaBaloncesto) {
            return true;
        }
        if (nombreDeporte.contains("voleibol") && estadistica instanceof EstadisticaVoleibol) {
            return true;
        }
        
        return false; 
    }

    public void agregarEstadistica(EstadisticaPartido estadistica) {
    	// pendiente descripcion
    	   if (estadistica == null)
               throw new IllegalArgumentException("La estadística es obligatoria");
           if (!participantes.contains(estadistica.getSocio()))
               throw new IllegalArgumentException("El jugador no participó en el partido");
           if (obtenerEstadistica(estadistica.getSocio()) != null)
               throw new IllegalArgumentException("El jugador ya tiene estadística en este partido");
           if (!corresponde(estadistica)) // REVISAR
               throw new IllegalArgumentException("La estadística no corresponde al deporte");
        this.estadisticas.add(estadistica);
    }
    
    
}