package Partidos;

import Usuarios.Socio;

public abstract class EstadisticaPartido {
	
	//atributos
	protected Socio socio; 
	protected Partido partido;

	//constructor
    public EstadisticaPartido(Socio socio, Partido partido) {
        this.socio = socio;
        this.partido = partido;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }
    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }
}

