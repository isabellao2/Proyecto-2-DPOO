package Torneos;

public class PosicionesTorneo {

    private final InscripcionTorneo inscripcion;
    private int jugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private int puntosFavor;
    private int puntosContra;

    public PosicionesTorneo(InscripcionTorneo inscripcion) {
        this.inscripcion = inscripcion;
    }


    public int getPuntos() {
        return ganados * 3 + empatados;
    }

    public int getDiferencia() {
        return puntosFavor - puntosContra;
    }

    public InscripcionTorneo getInscripcion() {
        return inscripcion;
    }

    public int getJugados() {
        return jugados;
    }

    public int getGanados() {
        return ganados;
    }

    public int getEmpatados() {
        return empatados;
    }

    public int getPerdidos() {
        return perdidos;
    }

    public int getPuntosFavor() {
        return puntosFavor;
    }

    public int getPuntosContra() {
        return puntosContra;
    }

    public void registrar(int favor, int contra) {
        jugados++;
        puntosFavor += favor;
        puntosContra += contra;
        if (favor > contra) {
            ganados++;
        } else if (favor == contra) {
            empatados++;
        } else {
            perdidos++;
        }
    }

}
