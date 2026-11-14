package Torneos;

import Usuarios.Usuario;
import Torneos.TipoPremio;
public class PremiosTorneo {

    private final Usuario ganador;
    private final TipoPremio tipo;
    private final double valor;

    public PremiosTorneo(Usuario ganador, TipoPremio tipo, double valor) {
        this.ganador = ganador;
        this.tipo = tipo;
        this.valor = valor;
    }

    public Usuario getGanador() {
        return ganador;
    }

    public TipoPremio getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        if (tipo == TipoPremio.BONO) {
            return ganador.getLogin() + ": bono de " + (int) Math.round(valor * 100) + "% de descuento";
        }
        return ganador.getLogin() + ": " + String.format("$%,.0f", valor) + " en efectivo";
    }
}