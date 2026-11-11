package DeporteIndividual;

import Usuarios.Socio;

public abstract class EstadisticaDisciplina {

    protected final Socio jugador;
    protected int sets;

    public EstadisticaDisciplina(Socio jugador, int sets) {
        this.jugador = jugador;
        this.sets = sets;
    }

    public Socio getJugador() {
        return jugador;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }
}