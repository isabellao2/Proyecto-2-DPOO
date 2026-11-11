package DeporteConjunto;

import Excepciones.ClubException;

public class DeporteConjunto {
	 public static final String FUTBOL = "Fútbol";
	    public static final String BALONCESTO = "Baloncesto";
	    public static final String VOLEIBOL = "Voleibol";

	    private final String nombre;
	    private final int numeroMinimoJugadores;
	    private final boolean permiteEmpate;

	    public DeporteConjunto(String nombre, int numeroMinimoJugadores, boolean permiteEmpate) throws ClubException {
	        if (nombre == null || nombre.isBlank()) {
	            throw new ClubException("Todo deporte necesita nombre.");
	        }
	        if (numeroMinimoJugadores <= 0) {
	            throw new ClubException("El mínimo de jugadores de " + nombre + " debe ser al menos 1.");
	        }
	        this.nombre = nombre.trim();
	        this.numeroMinimoJugadores = numeroMinimoJugadores;
	        this.permiteEmpate = permiteEmpate;
	    }

	    private static DeporteConjunto predefinido(String nombre, int minimo, boolean empate) {
	        try {
	            return new DeporteConjunto(nombre, minimo, empate);
	        } catch (ClubException e) {
	            throw new IllegalStateException(e.getMessage());
	        }
	    }

	    public static DeporteConjunto futbol() {
	        return predefinido(FUTBOL, 7, true);
	    }

	    public static DeporteConjunto baloncesto() {
	        return predefinido(BALONCESTO, 5, false);
	    }

	    public static DeporteConjunto voleibol() {
	        return predefinido(VOLEIBOL, 6, false);
	    }

	    public boolean cumpleMinimo(int jugadoresHabilitados) {
	        return jugadoresHabilitados >= numeroMinimoJugadores;
	    }

	    public boolean permiteEmpate() {
	        return permiteEmpate;
	    }

	    public String getNombre() {
	        return nombre;
	    }

	    public int getNumeroMinimoJugadores() {
	        return numeroMinimoJugadores;
	    }

	    @Override
	    public boolean equals(Object o) {
	        return o instanceof DeporteConjunto && nombre.equalsIgnoreCase(((DeporteConjunto) o).nombre);
	    }

	    @Override
	    public int hashCode() {
	        return nombre.toLowerCase().hashCode();
	    }

	    @Override
	    public String toString() {
	        return nombre;
	    }
		public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase().trim();
    	}

   		 public boolean esFutbol() {
        return normalizar(nombre).equals("futbol");
    	}
	}


