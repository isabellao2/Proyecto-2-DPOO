package Usuarios;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import club.Club;
import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import DeporteIndividual.Reserva;
import Entrenamiento.SesionesEntrenamiento;
import Excepciones.ClubException;
import Partidos.Partido;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.Cafeteria;
import TiendaCafeteria.TiendaDeportiva;
import TiendaCafeteria.Venta;
import Torneos.Modalidad;
import Torneos.TipoTorneo;
import Torneos.Torneo;
import Torneos.BonoDescuento;

public class Administrador extends Usuario {

    public static final int MIN_ENTRENADORES = 2;
    public static final int MIN_FISIOTERAPEUTAS = 1;

    public static final String RUBRO_TIENDA = "TIENDA";
    public static final String RUBRO_CAFETERIA = "CAFETERIA";
    public static final String RUBRO_MENSUALIDAD = "MENSUALIDAD";

    public enum Periodo { DIARIO, SEMANAL, MENSUAL }

    // Se asigna desde Club.agregarUsuario; el administrador no guarda copias de las listas
    private Club club;

    public Administrador(String login, String contrasena) throws ClubException {
        super(login, contrasena);
    }

    public void setClub(Club club) {
        this.club = club;
    }

    private Club clubConectado() {
        if (club == null) {
            throw new ClubException("El administrador no está conectado al club.");
        }
        return club;
    }

    //Jugadores y equipos 

    public Socio crearJugador(String login, String contrasena, int edad) throws ClubException {
        Club c = clubConectado();
        if (login == null || login.isBlank()) {
            throw new ClubException("Escribe el login del jugador.");
        }
        String l = login.trim();
        if (c.buscarUsuario(l) != null) {
            throw new ClubException("Ya existe un usuario con el login '" + l + "'.");
        }
        Socio nuevo = new Socio(l, contrasena, edad);
        c.agregarUsuario(nuevo);
        return nuevo;
    }

    public Equipo crearEquipo(String nombre, DeporteConjunto deporte, Categoria categoria, int cupoMaximo)
            throws ClubException {
        Club c = clubConectado();
        if (nombre == null || nombre.isBlank()) {
            throw new ClubException("Escribe el nombre del equipo.");
        }
        String n = nombre.trim();
        if (c.buscarEquipo(n) != null) {
            throw new ClubException("Ya existe un equipo llamado '" + n + "'.");
        }
        Equipo nuevo = new Equipo(n, deporte, categoria, cupoMaximo);
        c.agregarEquipo(nuevo);
        return nuevo;
    }

    public void darDeBajaEquipo(Equipo equipo) throws ClubException {
        Club c = clubConectado();
        if (equipo == null || c.buscarEquipo(equipo.getNombre()) == null) {
            throw new ClubException("No encontramos ese equipo entre los equipos del club.");
        }
        equipo.disolver();
        c.eliminarEquipo(equipo);
    }

    public void darDeBajaJugador(Socio jugador) throws ClubException {
        Club c = clubConectado();
        if (jugador == null || c.buscarSocio(jugador.getLogin()) == null) {
            throw new ClubException("No encontramos a ese jugador entre los jugadores del club.");
        }
        for (Equipo e : c.getEquipos()) {
            if (e.tieneJugador(jugador)) {
                e.retirarJugador(jugador);
            }
        }
        c.eliminarUsuario(jugador);
    }

    //Historial 

    public List<Reserva> consultarHistorialReservas() {
        List<Reserva> resultado = new ArrayList<>();
        for (Socio s : clubConectado().getSocios()) {
            resultado.addAll(s.getReservas());
        }
        return resultado;
    }

    public List<Partido> consultarHistorialPartidos() {
        Set<Partido> unicos = new LinkedHashSet<>();
        for (Socio s : clubConectado().getSocios()) {
            unicos.addAll(s.getPartidos());
        }
        return new ArrayList<>(unicos);
    }

    public List<Partido> consultarPartidosPorDeporte(DeporteConjunto deporte) throws ClubException {
        if (deporte == null) {
            throw new ClubException("Elige el deporte del que quieres ver los marcadores.");
        }
        List<Partido> resultado = new ArrayList<>();
        for (Partido p : consultarHistorialPartidos()) {
            if (p.getDeporte().equals(deporte)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<SesionesEntrenamiento> consultarHistorialEntrenamiento() {
        List<SesionesEntrenamiento> resultado = new ArrayList<>();
        for (Entrenador e : clubConectado().getEntrenadores()) {
            resultado.addAll(e.getSesiones());
        }
        return resultado;
    }

    // Inventario (RF13, HU-03) 

    public List<Articulo> consultarInventario() {
        List<Articulo> resultado = new ArrayList<>();
        TiendaDeportiva tienda = clubConectado().getTienda();
        if (tienda != null) {
            resultado.addAll(tienda.getArticulosDisponibles());
        }
        return resultado;
    }

  
    public Map<String, List<Articulo>> consultarInventarioPorUbicacion() {
        Map<String, List<Articulo>> resultado = new LinkedHashMap<>();
        for (Articulo a : consultarInventario()) {
            String ubicacion = a.getUbicacion();
            if (!resultado.containsKey(ubicacion)) {
                resultado.put(ubicacion, new ArrayList<Articulo>());
            }
            resultado.get(ubicacion).add(a);
        }
        return resultado;
    }

    //HU-03
    public List<String> consultarProductosAgotados() {
        Map<String, Integer> total = new LinkedHashMap<>();
        for (Articulo a : consultarInventario()) {
            int actual = 0;
            if (total.containsKey(a.getNombre())) {
                actual = total.get(a.getNombre());
            }
            total.put(a.getNombre(), actual + a.getCantidadDisponible());
        }
        List<String> agotados = new ArrayList<>();
        for (Map.Entry<String, Integer> e : total.entrySet()) {
            if (e.getValue() == 0) {
                agotados.add(e.getKey());
            }
        }
        return agotados;
    }

    // HU-03
    public Set<String> consultarUbicacionesParaReabastecer() {
        Set<String> resultado = new LinkedHashSet<>();
        for (Articulo a : consultarInventario()) {
            if (a.getCantidadDisponible() == 0) {
                resultado.add(a.getUbicacion());
            }
        }
        return resultado;
    }

    public void restablecerArticulo(Articulo articulo, String ubicacion, int cantidad) throws ClubException {
        if (articulo == null) {
            throw new ClubException("Elige el artículo que quieres restablecer.");
        }
        if (ubicacion != null && !ubicacion.isBlank() && !ubicacion.trim().equalsIgnoreCase(articulo.getUbicacion())) {
            articulo.mover(ubicacion);
        }
        articulo.abastecer(cantidad);
    }

    //Turnos (HU-01) 

    public void asignarTurno(Empleado empleado, Turno turno) throws ClubException {
        if (empleado == null) {
            throw new ClubException("Elige el empleado al que quieres asignar el turno.");
        }
        empleado.agregarTurno(turno);
    }

     
    public void gestionarCambioTurno(Empleado empleado, Turno turnoActual, boolean aprobar)
            throws ClubException {
        if (empleado == null || turnoActual == null || turnoActual.getEmpleado() != empleado) {
            throw new ClubException("Ese turno no pertenece al empleado indicado.");
        }
        if (!turnoActual.estaPendiente()) {
            throw new ClubException("Ese turno no tiene una solicitud de cambio pendiente.");
        }
        if (!aprobar) {
            turnoActual.rechazarTurno();
            return;
        }

        boolean esEntrenador = empleado instanceof Entrenador;
        int minimo = esEntrenador ? MIN_ENTRENADORES : MIN_FISIOTERAPEUTAS;
        String rol = esEntrenador ? "entrenadores" : "fisioterapeutas";

        int quedarian = contarDisponibles(turnoActual, esEntrenador) - 1;
        if (quedarian < minimo) {
            throw new ClubException("No podemos aprobar el cambio: el " + turnoActual.getDia() + " a las "
                    + turnoActual.getHora() + " quedarían " + Math.max(quedarian, 0) + " " + rol
                    + " y necesitamos al menos " + minimo + ".");
        }
        turnoActual.aprobarTurno();
    }

    private int contarDisponibles(Turno turno, boolean entrenadores) {
        int total = 0;
        for (Empleado e : clubConectado().getEmpleados()) {
            boolean delRol = entrenadores ? (e instanceof Entrenador) : (e instanceof Fisioterapeuta);
            if (delRol && e.estaDisponibleEn(turno.getDia(), turno.getHora())) {
                total++;
            }
        }
        return total;
    }

    //Sugerencias (HU-02)

    public void gestionarSugerencia(Empleado empleado, String sugerencia, boolean aprobar) throws ClubException {
        if (empleado == null) {
            throw new ClubException("Elige el empleado que hizo la sugerencia.");
        }
        empleado.resolverSugerencia(sugerencia, aprobar);
    }

    //Reportes de ventas (RF17)

    /** Ventas de un rubro (TIENDA, CAFETERIA o MENSUALIDAD) en el día, la semana o el mes de la fecha dada. */
    public List<Venta> reporteVentas(String rubro, Periodo periodo, LocalDate referencia) throws ClubException {
        if (rubro == null || periodo == null || referencia == null) {
            throw new ClubException("Indica el rubro, el periodo y la fecha de referencia.");
        }
        if (!rubro.equalsIgnoreCase(RUBRO_TIENDA) && !rubro.equalsIgnoreCase(RUBRO_CAFETERIA)
                && !rubro.equalsIgnoreCase(RUBRO_MENSUALIDAD)) {
            throw new ClubException("El rubro debe ser TIENDA, CAFETERIA o MENSUALIDAD.");
        }

        LocalDate desde;
        LocalDate hasta;
        if (periodo == Periodo.DIARIO) {
            desde = referencia;
            hasta = referencia;
        } else if (periodo == Periodo.SEMANAL) {
            desde = referencia.with(DayOfWeek.MONDAY);
            hasta = desde.plusDays(6);
        } else {
            desde = referencia.withDayOfMonth(1);
            hasta = referencia.withDayOfMonth(referencia.lengthOfMonth());
        }

        List<Venta> todas = new ArrayList<>();
        TiendaDeportiva tienda = clubConectado().getTienda();
        Cafeteria cafeteria = clubConectado().getCafeteria();
        if (tienda != null) {
            todas.addAll(tienda.getVentas());
        }
        if (cafeteria != null) {
            todas.addAll(cafeteria.getVentas());
        }

        List<Venta> resultado = new ArrayList<>();
        for (Venta v : todas) {
            if (v.esRubro(rubro) && !v.getFecha().isBefore(desde) && !v.getFecha().isAfter(hasta)) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public static double sumarTotales(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) {
            total += v.calcularTotal();
        }
        return total;
    }

    // Torneos

    public Torneo crearTorneo(String nombre, TipoTorneo tipo, Modalidad modalidad, String deporte, String categoria, int cupos, double tarifa, LocalDate fecha, double valorBono) throws ClubException {
        Club c = clubConectado();
        if (nombre == null || nombre.isBlank()) {
            throw new ClubException("Escribe el nombre del torneo.");
        }
        String n = nombre.trim();
        
        // Verificamos si ya existe un torneo con el mismo nombre en el club
        if (c.buscarTorneo(n) != null) {
            throw new ClubException("Ya existe un torneo llamado '" + n + "'.");
        }
        
       Torneo nuevoTorneo = new Torneo(n, tipo, modalidad, deporte, categoria, cupos, tarifa, fecha, valorBono);
        c.agregarTorneo(nuevoTorneo);
        return nuevoTorneo;
    }

    public void iniciarTorneo(Torneo torneo) throws ClubException {
        if (torneo == null) {
            throw new ClubException("Selecciona un torneo válido para iniciar.");
        }
        torneo.iniciar();
    }

    public void finalizarTorneo(Torneo torneo, Usuario ganadorUsuario, Equipo ganadorEquipo) throws ClubException {
        if (torneo == null) {
            throw new ClubException("Selecciona un torneo válido para finalizar.");
        }
        torneo.finalizarTorneo(ganadorUsuario, ganadorEquipo);
    }

     public List<Torneo> consultarTorneos() {
        return new ArrayList<>(clubConectado().getTorneos());
    }
    

}