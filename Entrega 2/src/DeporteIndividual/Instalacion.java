package DeporteIndividual;

import Excepciones.ClubException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Instalacion {

    private final String nombre;
    private final String tipo;
    private final int capacidad;
    private final boolean techada;
    private final List<Reserva> reservas;
    private final List<LocalDateTime[]> bloqueos;

    public Instalacion(String nombre, String tipo, int capacidad, boolean techada) throws ClubException {
        if (nombre == null || nombre.isBlank()) {
            throw new ClubException("Toda instalación necesita un nombre.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new ClubException("Indica el tipo de instalación (por ejemplo: cancha de tenis, mesa de ping pong).");
        }
        if (capacidad <= 0) {
            throw new ClubException("La capacidad de la instalación debe ser de al menos 1 jugador.");
        }
        this.nombre = nombre.trim();
        this.tipo = tipo.trim();
        this.capacidad = capacidad;
        this.techada = techada;
        this.reservas = new ArrayList<>();
        this.bloqueos = new ArrayList<>();
    }

    public boolean estaOcupada(LocalDateTime inicio, LocalDateTime fin) {
        for (Reserva r : reservas) {
            if (r.coincideCon(inicio, fin)) {
                return true;
            }
        }
        for (LocalDateTime[] b : bloqueos) {
            if (b[0].isBefore(fin) && inicio.isBefore(b[1])) {
                return true;
            }
        }
        return false;
    }

    public void agregarReserva(Reserva r) throws ClubException {
        if (r == null || r.getInstalacion() != this) {
            throw new ClubException("Esa reserva no corresponde a la instalación " + nombre + ".");
        }
        if (!aceptaModalidad(r.getModalidad(), r.getNumeroJugadores())) {
            throw new ClubException(nombre + " no admite la modalidad " + r.getModalidad() + " con "
                    + r.getNumeroJugadores() + " jugadores.");
        }
        if (estaOcupada(r.getInicio(), r.getFin())) {
            throw new ClubException("¡Qué pena! " + nombre + " ya está reservada en ese horario.");
        }
        reservas.add(r);
    }

 
    public void bloquear(LocalDateTime inicio, LocalDateTime fin) throws ClubException {
        if (inicio == null || fin == null || !inicio.isBefore(fin)) {
            throw new ClubException("El horario a bloquear no es válido: el inicio debe ser antes del fin.");
        }
        if (estaOcupada(inicio, fin)) {
            throw new ClubException(nombre + " ya está ocupada en ese horario.");
        }
        bloqueos.add(new LocalDateTime[]{inicio, fin});
    }

    public boolean aceptaModalidad(String modalidad, int numeroJugadores) {
        if (modalidad == null || numeroJugadores <= 0 || numeroJugadores > capacidad) {
            return false;
        }
        String m = modalidad.trim().toUpperCase();
        if (m.equals("SENCILLOS")) {
            return numeroJugadores <= 2;
        }
        if (m.equals("DOBLES")) {
            return numeroJugadores <= 4;
        }
        return false;
    }

    //Nuevo metodo
   public boolean restringeBebidaCaliente() {
    String t = tipo.toLowerCase();
    if (t.contains("padel") || t.contains("pádel")) {
        return true;              // pádel: siempre restringido
    }
    if (t.contains("mesa")) {
        return techada;           // mesa de tenis de mesa: solo si es techada
    }
    return false;                 // tenis: nunca mencionan
}

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean isTechada() {
        return techada;
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }
}

