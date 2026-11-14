package Consolas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import club.Club;
import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import DeporteIndividual.Reserva;
import Entrenamiento.SesionesEntrenamiento;
import Partidos.Partido;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.Venta;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Socio;
import Usuarios.Turno;
import DeporteIndividual.DisciplinaIndividual;
import Torneos.InscripcionTorneo;
import Torneos.Modalidad;
import Torneos.TipoTorneo;
import Torneos.Torneo;


public class MenuAdministrador {

    private Club club;
    private Administrador admin;
    private Entrada entrada;

    public MenuAdministrador(Club club, Administrador admin, Entrada entrada) {
        this.club = club;
        this.admin = admin;
        this.entrada = entrada;
    }

    public void mostrar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            System.out.println("===== MENÚ ADMINISTRADOR: " + admin.getLogin() + " =====");
            System.out.println("1. Crear un jugador");
            System.out.println("2. Dar de baja un jugador");
            System.out.println("3. Crear un equipo");
            System.out.println("4. Dar de baja un equipo");
            System.out.println("5. Ver inventario por tienda");
            System.out.println("6. Ver productos agotados y tiendas por reabastecer");
            System.out.println("7. Reabastecer un artículo");
            System.out.println("8. Gestionar cambios de turno");
            System.out.println("9. Ver reporte de ventas");
            System.out.println("10. Ver historial de reservas");
            System.out.println("11. Ver historial de partidos");
            System.out.println("12. Ver historial de entrenamientos");
            System.out.println("13. Crear un torneo");
            System.out.println("14. Ver torneos");
            System.out.println("15. Inscribir un equipo en un torneo");
            System.out.println("16. Iniciar un torneo");
            System.out.println("17. Registrar un encuentro de un torneo");
            System.out.println("18. Finalizar un torneo");
            System.out.println("19. Ver historial de un torneo");
            System.out.println("0. Salir");
            opcion = entrada.leerEntero("Opción: ", 0, 19);

            try {
                if (opcion == 1) {
                    crearJugador();
                } else if (opcion == 2) {
                    darDeBajaJugador();
                } else if (opcion == 3) {
                    crearEquipo();
                } else if (opcion == 4) {
                    darDeBajaEquipo();
                } else if (opcion == 5) {
                    verInventario();
                } else if (opcion == 6) {
                    verAgotados();
                } else if (opcion == 7) {
                    reabastecer();
                } else if (opcion == 8) {
                    gestionarTurnos();
                } else if (opcion == 9) {
                    verReporteVentas();
                } else if (opcion == 10) {
                    verHistorialReservas();
                } else if (opcion == 11) {
                    verHistorialPartidos();
                }else if (opcion == 12) {
                    verHistorialEntrenamientos();
                } else if (opcion == 13) {
                    crearTorneo();
                } else if (opcion == 14) {
                    verTorneos();
                } else if (opcion == 15) {
                    inscribirEquipoEnTorneo();
                } else if (opcion == 16) {
                    iniciarTorneo();
                } else if (opcion == 17) {
                    registrarEncuentro();
                } else if (opcion == 18) {
                    finalizarTorneo();
                } else if (opcion == 19) {
                    verHistorialTorneo();
                }
            } catch (RuntimeException e) {
                System.out.println("No se pudo completar: " + e.getMessage());
            }
        }
    }

    // Jugadores

    private void crearJugador() {
        String login = entrada.leerTexto("Login del nuevo jugador: ");
        String contrasena = entrada.leerTexto("Contraseña: ");
        int edad = entrada.leerEntero("Edad: ", 1, 100);
        Socio nuevo = admin.crearJugador(login, contrasena, edad);
        System.out.println("Jugador creado: " + nuevo.getLogin());
    }

    private void darDeBajaJugador() {
        List<Socio> lista = new ArrayList<>(club.getSocios());
        if (lista.isEmpty()) {
            System.out.println("No hay jugadores.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i).getLogin() + " (" + lista.get(i).getEdad() + " años)");
        }
        int eleccion = entrada.leerEntero("Número de jugador (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return;
        }
        Socio socio = lista.get(eleccion - 1);


        if (!socio.getHistorialCompras().isEmpty()) {
            System.out.println("No se puede dar de baja a " + socio.getLogin()
                    + ": tiene compras registradas y quedarían sin comprador.");
            return;
        }
        if (entrada.leerSiNo("¿Seguro que quieres dar de baja a " + socio.getLogin() + "?")) {
            admin.darDeBajaJugador(socio);
            System.out.println("Jugador dado de baja.");
        }
    }

    // Equipos 

    private void crearEquipo() {
        List<DeporteConjunto> deportes = new ArrayList<>(club.getDeportes());
        List<Categoria> categorias = new ArrayList<>(club.getCategorias());
        if (deportes.isEmpty() || categorias.isEmpty()) {
            System.out.println("No hay deportes o categorías cargados.");
            return;
        }

        String nombre = entrada.leerTexto("Nombre del equipo: ");

        System.out.println("--- Deportes ---");
        for (int i = 0; i < deportes.size(); i++) {
            System.out.println((i + 1) + ". " + deportes.get(i).getNombre());
        }
        DeporteConjunto deporte = deportes.get(entrada.leerEntero("Deporte: ", 1, deportes.size()) - 1);

        System.out.println("--- Categorías ---");
        for (int i = 0; i < categorias.size(); i++) {
            Categoria c = categorias.get(i);
            System.out.println((i + 1) + ". " + c.getNombre() + " (" + c.getEdadMinima() + " a " + c.getEdadMaxima() + " años)");
        }
        Categoria categoria = categorias.get(entrada.leerEntero("Categoría: ", 1, categorias.size()) - 1);

        System.out.println("Mínimo de jugadores en " + deporte.getNombre() + ": " + deporte.getNumeroMinimoJugadores());
        int cupo = entrada.leerEntero("Cupo máximo: ", 1, 50);

        Equipo nuevo = admin.crearEquipo(nombre, deporte, categoria, cupo);
        System.out.println("Equipo creado: " + nuevo.getNombre());
    }

    private void darDeBajaEquipo() {
        List<Equipo> lista = new ArrayList<>(club.getEquipos());
        if (lista.isEmpty()) {
            System.out.println("No hay equipos.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Equipo e = lista.get(i);
            System.out.println((i + 1) + ". " + e.getNombre() + " (" + e.getDeporte().getNombre()
                    + ", " + e.getCategoria().getNombre() + ") - " + e.getComposicion().size() + " jugadores");
        }
        int eleccion = entrada.leerEntero("Número de equipo (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return;
        }
        Equipo equipo = lista.get(eleccion - 1);
        if (entrada.leerSiNo("¿Seguro que quieres dar de baja a " + equipo.getNombre() + "?")) {
            admin.darDeBajaEquipo(equipo);
            System.out.println("Equipo dado de baja.");
        }
    }

    // Inventario 

    private void verInventario() {
        Map<String, List<Articulo>> porUbicacion = admin.consultarInventarioPorUbicacion();
        if (porUbicacion.isEmpty()) {
            System.out.println("No hay artículos.");
            return;
        }
        for (Map.Entry<String, List<Articulo>> e : porUbicacion.entrySet()) {
            System.out.println("--- " + e.getKey() + " ---");
            for (Articulo a : e.getValue()) {
                System.out.println("  " + a.getNombre() + " | $" + a.getPrecio() + " | disponibles: " + a.getCantidadDisponible());
            }
        }
    }

    private void verAgotados() {
        List<String> agotados = admin.consultarProductosAgotados();
        System.out.println("--- Productos agotados en todas las tiendas ---");
        if (agotados.isEmpty()) {
            System.out.println("  (ninguno)");
        }
        for (String nombre : agotados) {
            System.out.println("  " + nombre);
        }

        Set<String> ubicaciones = admin.consultarUbicacionesParaReabastecer();
        System.out.println("--- Tiendas candidatas a reabastecimiento ---");
        if (ubicaciones.isEmpty()) {
            System.out.println("  (ninguna)");
        }
        for (String u : ubicaciones) {
            System.out.println("  " + u);
        }
    }

    private void reabastecer() {
        List<Articulo> lista = admin.consultarInventario();
        if (lista.isEmpty()) {
            System.out.println("No hay artículos.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Articulo a = lista.get(i);
            System.out.println((i + 1) + ". " + a.getNombre() + " | " + a.getUbicacion()
                    + " | disponibles: " + a.getCantidadDisponible());
        }
        int eleccion = entrada.leerEntero("Número de artículo (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return;
        }
        Articulo articulo = lista.get(eleccion - 1);
        int cantidad = entrada.leerEntero("Cantidad a agregar: ", 1, 10000);
        admin.restablecerArticulo(articulo, null, cantidad);
        System.out.println("Ahora hay " + articulo.getCantidadDisponible() + " de " + articulo.getNombre()
                + " en " + articulo.getUbicacion() + ".");
    }

    // Turnos 

    private void gestionarTurnos() {
        List<Empleado> duenos = new ArrayList<>();
        List<Turno> pendientes = new ArrayList<>();
        for (Empleado e : club.getEmpleados()) {
            for (Turno t : e.getTurnos()) {
                if (t.estaPendiente()) {
                    duenos.add(e);
                    pendientes.add(t);
                }
            }
        }
        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes de cambio de turno pendientes.");
            return;
        }
        for (int i = 0; i < pendientes.size(); i++) {
            System.out.println((i + 1) + ". " + duenos.get(i).getLogin() + " | " + pendientes.get(i));
        }
        int eleccion = entrada.leerEntero("Número de solicitud (0 para cancelar): ", 0, pendientes.size());
        if (eleccion == 0) {
            return;
        }
        boolean aprobar = entrada.leerSiNo("¿Aprobar el cambio?");
        admin.gestionarCambioTurno(duenos.get(eleccion - 1), pendientes.get(eleccion - 1), aprobar);
        System.out.println(aprobar ? "Cambio aprobado." : "Cambio rechazado.");
    }

    //Reportes

    private void verReporteVentas() {
        System.out.println("1. Tienda");
        System.out.println("2. Cafetería");
        System.out.println("3. Mensualidades");
        int r = entrada.leerEntero("Rubro: ", 1, 3);
        String rubro = Administrador.RUBRO_TIENDA;
        if (r == 2) {
            rubro = Administrador.RUBRO_CAFETERIA;
        } else if (r == 3) {
            rubro = Administrador.RUBRO_MENSUALIDAD;
        }

        System.out.println("1. Diario");
        System.out.println("2. Semanal");
        System.out.println("3. Mensual");
        int p = entrada.leerEntero("Periodo: ", 1, 3);
        Administrador.Periodo periodo = Administrador.Periodo.DIARIO;
        if (p == 2) {
            periodo = Administrador.Periodo.SEMANAL;
        } else if (p == 3) {
            periodo = Administrador.Periodo.MENSUAL;
        }

        LocalDate fecha = leerFecha("Fecha de referencia (AAAA-MM-DD): ");

        List<Venta> ventas = admin.reporteVentas(rubro, periodo, fecha);
        System.out.println("--- Ventas de " + rubro + " (" + periodo + ") ---");
        if (ventas.isEmpty()) {
            System.out.println("  (ninguna)");
        }
        for (Venta v : ventas) {
            System.out.println("  " + v.getFecha() + " | " + v.getComprador().getLogin()
                    + " | subtotal $" + v.getValor() + " | total $" + v.calcularTotal());
        }
        System.out.println("Total vendido: $" + Administrador.sumarTotales(ventas));
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = entrada.leerTexto(mensaje);
            try {
                return LocalDate.parse(texto.trim());
            } catch (Exception e) {
                System.out.println("Fecha incorrecta.");
            }
        }
    }

    //Historiales 

    private void verHistorialReservas() {
        List<Reserva> lista = admin.consultarHistorialReservas();
        if (lista.isEmpty()) {
            System.out.println("No hay reservas.");
        }
        for (Reserva r : lista) {
            System.out.println(r.getInstalacion().getNombre() + " | " + r.getInicio() + " a " + r.getFin());
        }
    }

    private void verHistorialPartidos() {
        List<Partido> lista = admin.consultarHistorialPartidos();
        if (lista.isEmpty()) {
            System.out.println("No hay partidos.");
        }
        for (Partido p : lista) {
            System.out.println(p.getFecha() + " | " + p.getDeporte().getNombre());
        }
    }

    private void verHistorialEntrenamientos() {
        List<SesionesEntrenamiento> lista = admin.consultarHistorialEntrenamiento();
        if (lista.isEmpty()) {
            System.out.println("No hay sesiones de entrenamiento.");
        }
        for (SesionesEntrenamiento s : lista) {
            System.out.println(s.getInicio() + " a " + s.getFin());
        }
    }

     //Torneos

    private void crearTorneo() {
        String nombre = entrada.leerTexto("Nombre del torneo: ");

        System.out.println("1. Recreativo (gratis, premio: bono)");
        System.out.println("2. Competitivo (con tarifa, premio en efectivo)");
        TipoTorneo tipo = TipoTorneo.RECREATIVO;
        if (entrada.leerEntero("Tipo: ", 1, 2) == 2) {
            tipo = TipoTorneo.COMPETITIVO;
        }

        System.out.println("1. Deporte de conjunto (equipos)");
        System.out.println("2. Individual");
        System.out.println("3. Dobles (parejas)");
        int m = entrada.leerEntero("Modalidad: ", 1, 3);
        Modalidad modalidad = Modalidad.CONJUNTO;
        if (m == 2) {
            modalidad = Modalidad.INDIVIDUAL;
        } else if (m == 3) {
            modalidad = Modalidad.DOBLES;
        }

        String deporte;
        String categoria;
        if (modalidad == Modalidad.CONJUNTO) {
            List<DeporteConjunto> deportes = new ArrayList<>(club.getDeportes());
            for (int i = 0; i < deportes.size(); i++) {
                System.out.println((i + 1) + ". " + deportes.get(i).getNombre());
            }
            deporte = deportes.get(entrada.leerEntero("Deporte: ", 1, deportes.size()) - 1).getNombre();

            List<Categoria> categorias = new ArrayList<>(club.getCategorias());
            for (int i = 0; i < categorias.size(); i++) {
                System.out.println((i + 1) + ". " + categorias.get(i).getNombre());
            }
            categoria = categorias.get(entrada.leerEntero("Categoría: ", 1, categorias.size()) - 1).getNombre();
        } else {
            List<DisciplinaIndividual> disciplinas = new ArrayList<>(club.getDisciplinas());
            for (int i = 0; i < disciplinas.size(); i++) {
                System.out.println((i + 1) + ". " + disciplinas.get(i).getNombre());
            }
            deporte = disciplinas.get(entrada.leerEntero("Disciplina: ", 1, disciplinas.size()) - 1).getNombre();

            System.out.println("1. Principiante");
            System.out.println("2. Intermedio");
            System.out.println("3. Avanzado");
            int n = entrada.leerEntero("Nivel: ", 1, 3);
            categoria = "Principiante";
            if (n == 2) {
                categoria = "Intermedio";
            } else if (n == 3) {
                categoria = "Avanzado";
            }
        }

        int cupos = entrada.leerEntero("Número de cupos (equipos o jugadores): ", 2, 200);
        double tarifa = 0;
        if (tipo == TipoTorneo.COMPETITIVO) {
            tarifa = entrada.leerDecimal("Tarifa de inscripción: ", 1);
        }
        LocalDate fecha = entrada.leerFecha("Fecha del torneo");

         double valorBono = 0;
        if (tipo == TipoTorneo.RECREATIVO) {
            valorBono = entrada.leerDecimal("Valor del bono para el ganador ($): ", 1);
        }
        
        Torneo t = admin.crearTorneo(nombre, tipo, modalidad, deporte, categoria, cupos, tarifa, fecha, valorBono);
        System.out.println("Torneo creado: " + t.getNombre() + ". Cupos preferenciales (20%): "
                + t.getCuposPreferenciales());
    }

    private void verTorneos() {
        List<Torneo> lista = admin.consultarTorneos();
        if (lista.isEmpty()) {
            System.out.println("No hay torneos.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Torneo t = lista.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " | " + t.getTipo() + " | "
                    + t.getModalidad() + " | " + t.getDeporte() + " " + t.getCategoria()
                    + " | " + t.getFecha() + " | " + t.getEstado()
                    + " | inscritos: " + t.getInscripciones().size() + "/" + t.getCupos());
        }
    }

    // Devuelve el torneo elegido, o null si cancela
    private Torneo elegirTorneo() {
        List<Torneo> lista = admin.consultarTorneos();
        if (lista.isEmpty()) {
            System.out.println("No hay torneos.");
            return null;
        }
        verTorneos();
        int eleccion = entrada.leerEntero("Número de torneo (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return null;
        }
        return lista.get(eleccion - 1);
    }

    private void inscribirEquipoEnTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        List<Equipo> equipos = new ArrayList<>(club.getEquipos());
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos.");
            return;
        }
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getNombre());
        }
        int eleccion = entrada.leerEntero("Número de equipo (0 para cancelar): ", 0, equipos.size());
        if (eleccion == 0) {
            return;
        }
        // Torneo valida deporte, categoría y mínimo de jugadores
        torneo.inscripcionEquipos(equipos.get(eleccion - 1), false);
        System.out.println("Equipo inscrito.");
    }

    private void iniciarTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        admin.iniciarTorneo(torneo);
        System.out.println("Torneo iniciado. Ya no admite inscripciones ni retiros.");
    }

    private void registrarEncuentro() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        String descripcion = entrada.leerTexto("Describe el encuentro y su resultado: ");
        torneo.registrarEncuentro(descripcion);
        System.out.println("Encuentro registrado.");
    }

    private void finalizarTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        List<InscripcionTorneo> inscritos = torneo.getInscripciones();
        if (inscritos.isEmpty()) {
            System.out.println("El torneo no tiene inscritos.");
            return;
        }
        for (int i = 0; i < inscritos.size(); i++) {
            System.out.println((i + 1) + ". " + inscritos.get(i).getNombre());
        }
        int eleccion = entrada.leerEntero("Número del ganador (0 para cancelar): ", 0, inscritos.size());
        if (eleccion == 0) {
            return;
        }
        InscripcionTorneo ganador = inscritos.get(eleccion - 1);
        admin.finalizarTorneo(torneo, ganador.getParticipante(), ganador.getEquipo());
        System.out.println("Torneo finalizado. Ganador: " + ganador.getNombre());
        if (torneo.getTipo() == TipoTorneo.COMPETITIVO) {
            System.out.println("Premio en efectivo: $" + torneo.calcularPremio());
        }
    }

    private void verHistorialTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        System.out.println("=== " + torneo.getNombre() + " (" + torneo.getEstado() + ") ===");
        System.out.println("--- Participantes ---");
        for (InscripcionTorneo i : torneo.getInscripciones()) {
            System.out.println("  " + i.getNombre() + " | cupo " + i.getTipoCupo()
                    + " | pagó $" + i.getTarifaPagada());
        }
        System.out.println("--- Encuentros ---");
        if (torneo.getHistorialEncuentros().isEmpty()) {
            System.out.println("  (ninguno)");
        }
        for (String e : torneo.getHistorialEncuentros()) {
            System.out.println("  " + e);
        }
        if (torneo.getGanador() != null) {
            System.out.println("Ganador: " + torneo.getGanador().getLogin());
        } else if (torneo.getEquipoGanador() != null) {
            System.out.println("Ganador: " + torneo.getEquipoGanador().getNombre());
        }
    }
}
