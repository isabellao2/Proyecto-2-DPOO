package Partidos;

import Usuarios.Socio; 

public class EstadisticaBaloncesto extends EstadisticaPartido {
   
	// atributos
    private int puntos;
    private int rebotes;

    public EstadisticaBaloncesto(Socio socio, Partido partido, int puntos, int rebotes) {
        super(socio, partido);
        this.puntos = puntos;
        this.rebotes = rebotes;
    }

    public void sumarPuntos(int cantidad) {
        if (cantidad > 0) {
            this.puntos += cantidad;
        }
    }

  // metodos getters y setters 
    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getRebotes() {
        return rebotes;
    }

    public void setRebotes(int rebotes) {
        this.rebotes = rebotes;
    }
}
