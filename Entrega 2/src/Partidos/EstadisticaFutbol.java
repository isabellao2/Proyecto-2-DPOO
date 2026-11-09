package Partidos;

import Usuarios.Socio;

public class EstadisticaFutbol extends EstadisticaPartido {
	
	// atributos
	    private int goles;
	    private int asistencias;
	    private int tarjetasAmarillas;
	    private boolean tarjetasRojas;
	    
	    // constructor
	    public EstadisticaFutbol(Socio socio, int goles, int asistencias, int tarjetasAmarillas, boolean tarjetasRojas, Partido partido) {
	        super(socio, partido);
	        this.goles = goles;
	        this.asistencias = asistencias;
	        this.tarjetasAmarillas = tarjetasAmarillas;
	        this.tarjetasRojas = tarjetasRojas;
	    }
	    
	    //metodos
	    public void registrarAmarilla() {
	    	// Saber cuantas amarillas o contador de tarjetas ammarillas de un jugador 
	        this.tarjetasAmarillas++;
	    }

	    public void registrarRoja() {
	    	// una roja maximo para una suspencion de un jugador
	        this.tarjetasRojas = true;
	    }

	    public boolean generaSuspension() {
	    	/** el jugador queda suspendido (por tarjeta roja directa o por acumulación de 2 tarjetas amarillas */
	         return this.tarjetasRojas || (this.tarjetasAmarillas >= 2);
	    }
	    	 
	    // getters y setters 
	    public int getGoles() {
	        return goles;
	    }

	    public void setGoles(int goles) {
	        this.goles = goles;
	    }

	    public int getAsistencias() {
	        return asistencias;
	    }

	    public void setAsistencias(int asistencias) {
	        this.asistencias = asistencias;
	    }

	    public int getTarjetasAmarillas() {
	        return tarjetasAmarillas;
	    }

	    public void setTarjetasAmarillas(int tarjetasAmarillas) {
	        this.tarjetasAmarillas = tarjetasAmarillas;
	    }

	    public boolean isTarjetasRojas() {
	        return tarjetasRojas;
	    }

	    public void setTarjetasRojas(boolean tarjetasRojas) {
	        this.tarjetasRojas = tarjetasRojas;
	    }
}

