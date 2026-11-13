package Torneos;
 
import DeporteConjunto.Equipo;
import Usuarios.Usuario;
 
 public class InscripcionTorneo {
    private final Usuario participante;
    private final Usuario compañero;
    private final Equipo equipo;
    private final TipoCupo tipoCupo;
    private final double tarifaPagada;

    public InscripcionTorneo (Usuario participante, Usuario compañero, Equipo equipo, TipoCupo tipoCupo, double tarifaPagada) {
        this.participante = participante;
        this.compañero = compañero;
        this.equipo = equipo;
        this.tipoCupo = tipoCupo;
        this.tarifaPagada = tarifaPagada;
    }
        // esDe de los usuarios 
        public boolean esDe (Usuario u) {
            return u != null && (u == participante || u == compañero);
        }
        // esDe de los equipos
        public boolean esDe (Equipo e) {
            return e != null && e == equipo;
        }

        public Usuario getParticipante() {
        return participante;
        }
 
        public Usuario getCompañero() {
            return compañero;
        }
    
        public Equipo getEquipo() {
            return equipo;
        }
    
        public TipoCupo getTipoCupo() {
            return tipoCupo;
        }
    
        public double getTarifaPagada() {
            return tarifaPagada;
        }

        // mostrar el quipo y los usuarios 

        public String getNombre() {
        if (equipo != null) {
            return equipo.getNombre();
        }
        return participante.getLogin() + (compañero != null ? " / " + compañero.getLogin() : "");
    }
    }
 