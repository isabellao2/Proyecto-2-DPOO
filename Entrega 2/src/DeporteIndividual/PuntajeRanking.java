package DeporteIndividual;

import Excepciones.ClubException;
import Usuarios.Socio;


public class PuntajeRanking {

    public static final int cambio_por_partid = 15;

    private int elo;
    private final Socio socio;
    private final DisciplinaIndividual disciplina;

    public PuntajeRanking(Socio socio, DisciplinaIndividual disciplina, int elo) throws ClubException {
        if (socio == null || disciplina == null) {
            throw new ClubException("Para llevar un ranking necesitamos al socio y la disciplina.");
        }
        if (elo < 0) {
            throw new ClubException("El puntaje ELO no puede ser negativo.");
        }
        this.socio = socio;
        this.disciplina = disciplina;
        this.elo = elo;
    }

    public void actualizar(boolean gano) {
        elo = Math.max(0, elo + (gano ? cambio_por_partid : -cambio_por_partid));
        InscripcionIndividual ins = disciplina.getInscripcion(socio);
        if (ins != null) {
            ins.actualizarNivelPorElo(elo);
        }
    }

    public int getElo() {
        return elo;
    }

    public Socio getSocio() {
        return socio;
    }

    public DisciplinaIndividual getDisciplina() {
        return disciplina;
    }

}
