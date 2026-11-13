package Usuarios;


import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Entrenamiento.SesionesEntrenamiento;
import Excepciones.ClubException;
import DeporteIndividual.Reserva;
import Partidos.Partido;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.Cafeteria;
import TiendaCafeteria.TiendaDeportiva;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class Administrador extends Usuario {
	
	public static final int min_disponibles = 2;
    public static final int MIN_ENTRENADORES = 2;
    public static final int MIN_FISIOTERAPEUTAS = 1;
	
	private final List<Socio> socios;
    private final List<Equipo> equipos;
    private final List<Empleado> empleados;
    private final List<TiendaDeportiva> tiendas;
    private final List<Cafeteria> cafeterias;


    public Administrador(String login, String contrasena) throws ClubException {
        super(login, contrasena);
        this.socios = new ArrayList<>();
        this.equipos = new ArrayList<>();
        this.empleados = new ArrayList<>();
        this.tiendas = new ArrayList<>();
        this.cafeterias = new ArrayList<>();
    }
    
    public Socio crearJugador (String login, String contrasena, int edad) throws ClubException {
        if (login != null) {
            for (Socio s : socios) {
                if (s.getLogin().equalsIgnoreCase(login.trim())) {
                    throw new ClubException("Ya existe un jugador con el login '" + login.trim());
                }
            }
        }
        Socio nuevo = new Socio(login, contrasena, edad);
        socios.add(nuevo);
        return nuevo;
    }
    
    public Equipo crearEquipo(String nombre, DeporteConjunto deporte, Categoria categoria, int cupoMaximo)
            throws ClubException {
        if (nombre != null) {
            for (Equipo e : equipos) {
                if (e.getNombre().equalsIgnoreCase(nombre.trim())) {
                    throw new ClubException("Ya existe un equipo llamado '" + nombre.trim());
                }
            }
        }
        Equipo nuevo = new Equipo(nombre, deporte, categoria, cupoMaximo);
        equipos.add(nuevo);
        return nuevo;
    }

    public void darDeBajaEquipo(Equipo equipo) throws ClubException {
        if (equipo == null || !equipos.contains(equipo)) {
            throw new ClubException("No encontramos ese equipo entre los equipos del club.");
        }
        equipo.disolver();
        equipos.remove(equipo);
    }
    public void darDeBajaJugador(Socio jugador) throws ClubException {
        if (jugador == null || !socios.contains(jugador)) {
            throw new ClubException("No encontramos a ese jugador entre los jugadores del club.");
        }
        for (Equipo e : equipos) {
            if (e.tieneJugador(jugador)) {
                e.retirarJugador(jugador);
            }
        }
        socios.remove(jugador);
    }
    public List<Reserva> consultarHistorialReservas() {
        List<Reserva> resultado = new ArrayList<>();
        for (Socio s : socios) {
            resultado.addAll(s.getReservas());
        }
        return resultado;
    }

    public List<Partido> consultarHistorialPartidos() {
        Set<Partido> unicos = new LinkedHashSet<>();
        for (Socio s : socios) {
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
        for (Empleado e : empleados) {
            if (e instanceof Entrenador) {
                resultado.addAll(((Entrenador) e).getSesiones());
            }
        }
        return resultado;
    }
    
    public List<Articulo> consultarInventario() {
        List<Articulo> resultado = new ArrayList<>();
        for (TiendaDeportiva t : tiendas) {
            resultado.addAll(t.getArticulosDisponibles());
        }
        return resultado;
    }

    public List<Articulo> consultarArticulosAgotados() {
        List<Articulo> resultado = new ArrayList<>();
        for (Articulo a : consultarInventario()) {
            if (a.estaAgotado()) {
                resultado.add(a);
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
        articulo.abastecer(cantidad);}
        
        public void asignarTurno(Empleado empleado, Turno turno) throws ClubException {
            if (empleado == null) {
                throw new ClubException("Elige el empleado al que quieres asignar el turno.");
            }
            empleado.agregarTurno(turno);
        }

        public void gestionarCambioTurno(Empleado empleado, Turno turnoActual, boolean aprobar, List<Empleado> empleados)
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
            if (empleados == null) {
                throw new ClubException("Necesitamos la lista de empleados para verificar la disponibilidad.");
            }
            int entrenadores = contarEmpleadosEn(empleados, Entrenador.class, turnoActual.getDia(), turnoActual.getHora());
            int fisioterapeutas = contarEmpleadosEn(empleados, Fisioterapeuta.class, turnoActual.getDia(), turnoActual.getHora());
            if (empleado instanceof Entrenador) {
                entrenadores--;
            } else if (empleado instanceof Fisioterapeuta) {
                fisioterapeutas--;
            }
            if (entrenadores < MIN_ENTRENADORES || fisioterapeutas < MIN_FISIOTERAPEUTAS) {
                 throw new ClubException("No podemos aprobar el cambio: el " + turnoActual.getDia() + " a las "
                    + turnoActual.getHora() + " quedarían " + Math.max(entrenadores, 0) + " entrenadores y "
                + Math.max(fisioterapeutas, 0) + " fisioterapeutas, y necesitamos al menos "
                + MIN_ENTRENADORES + " y " + MIN_FISIOTERAPEUTAS + ".");
            }
            turnoActual.aprobarTurno();
            }

            private int contarEmpleadosEn(List<Empleado> lista, Class<? extends Empleado> tipo, DayOfWeek dia, LocalTime hora) {
                int total = 0;
                for (Empleado e : lista) {
                    if (tipo.isInstance(e) && e.estaDisponibleEn(dia, hora)) {
                        total++;
                }
            }
                return total;
        }

        public void gestionarSugerencia(Empleado empleado, String sugerencia, boolean aprobar) throws ClubException {
            if (empleado == null) {
                throw new ClubException("Elige el empleado que hizo la sugerencia.");
            }
            empleado.resolverSugerencia(sugerencia, aprobar);
        }


    	
    

        public void registrarEmpleado(Empleado e) throws ClubException {
        	if (e == null) {
        throw new ClubException("No encontramos al empleado que quieres registrar.");
        	}
        	if (!empleados.contains(e)) {
        empleados.add(e);
        	}
        }

        public void registrarTienda(TiendaDeportiva t) throws ClubException {
        	if (t == null) {
        throw new ClubException("No encontramos la tienda que quieres registrar.");
        	}
        	if (!tiendas.contains(t)) {
        		tiendas.add(t);
        	}
        }

        public void registrarCafeteria(Cafeteria c) throws ClubException {
        	if (c == null) {
        throw new ClubException("No encontramos la cafetería que quieres registrar.");
        	}
        	if (!cafeterias.contains(c)) {
        		cafeterias.add(c);
        	}
        } 
}




