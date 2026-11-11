package Partidos;

import Usuarios.Socio; 

public class EstadisticaVoleibol extends EstadisticaPartido {
   // atributos
	
    private int aces;
    private int bloques;
    
    // constructor 
    public EstadisticaVoleibol(Socio socio, int aces, int bloques, Partido partido) {
        super(socio, partido);
        this.aces = aces;
        this.bloques = bloques;
    }

   // metodo getters y setters 
    public int getAces() {
        return aces;
    }

    public void setAces(int aces) {
        this.aces = aces;
    }

    public int getBloques() {
        return bloques;
    }

    public void setBloques(int bloques) {
        this.bloques = bloques;
    }
}
