package Torneos;
public class EncuentroTorneo {

    private final int numero;
    private final InscripcionTorneo uno;
    private final InscripcionTorneo dos;
    private int puntos1;
    private int puntos2;
    private boolean jugado;

     public InscripcionTorneo getGanador() {
        if (!jugado || puntos1 == puntos2) {
            return null;
        }
        return puntos1 > puntos2 ? uno : dos;
    }

    public int getNumero() {
        return numero;
    }

    public InscripcionTorneo getuno() {
        return uno;
    }

    public InscripcionTorneo getdos() {
        return dos;
    }

    public int getPuntos1() {
        return puntos1;
    }

    public int getPuntos2() {
        return puntos2;
    }

    public boolean isJugado() {
        return jugado;
    }

    public EncuentroTorneo(int numero, InscripcionTorneo uno, InscripcionTorneo dos) {
        this.numero = numero;
        this.uno = uno;
        this.dos = dos;
    }

    public void registrar(int puntos1, int puntos2) {
        this.puntos1 = puntos1;
        this.puntos2 = puntos2;
        this.jugado = true;
    }

    public boolean esEmpate() {
        return jugado && puntos1 == puntos2;
    }

    
   

    @Override
    public String toString() {
        return numero + ". " + uno.getNombre() + " vs " + dos.getNombre()
                + (jugado ? ": " + puntos1 + " - " + puntos2 : ": pendiente");
    }
}