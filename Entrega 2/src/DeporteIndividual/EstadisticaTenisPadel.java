package DeporteIndividual;

import Usuarios.Socio;

public class EstadisticaTenisPadel extends EstadisticaDisciplina{

    private int games;
    private int aces;
    private int erroresNoForzados;

    public EstadisticaTenisPadel (Socio jugador, int sets, int games, int aces, int erroresNoForzados){
        super (jugador,sets);
        this.games = games;
        this.aces = aces;
        this.erroresNoForzados = erroresNoForzados;

    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getAces() {
        return aces;
    }

    public void setAces(int aces) {
        this.aces = aces;
    }

    public int getErroresNoForzados() {
        return erroresNoForzados;
    }

    public void setErroresNoForzados(int erroresNoForzados) {
        this.erroresNoForzados = erroresNoForzados;

    
}