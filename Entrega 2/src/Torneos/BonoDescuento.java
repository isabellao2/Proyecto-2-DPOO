package Torneos;

public class BonoDescuento {

	private final double valor;
    private boolean usado;
    private final String origenTorneo;

    public BonoDescuento(double valor, String origenTorneo) {
        this.valor = valor;
        this.usado = false;
        this.origenTorneo = origenTorneo;
    }

    public double getValor() {
        return valor;
    }

    public boolean isUsado() {
        return usado;
    }

    public void usarBono() {
        this.usado = true;
    }

    public String getOrigenTorneo() {
        return origenTorneo;
    }
}

