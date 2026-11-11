package DeporteIndividual;

import Usuarios.Socio;

public class EstadisticaTenisMesa extends EstadisticaDisciplina{

    private int puntosTotales;

    public EstadisticaTenisMesa (Socio jugador, int sets, int puntosTotales){
        super (jugador,sets);
        this.puntosTotales = puntosTotales;

    }

    public int getPuntosTotales (){
        return puntosTotales;
    }
    public void setPuntosTotales (int puntosTotales){
        this.puntosTotales = puntosTotales;
    }
}