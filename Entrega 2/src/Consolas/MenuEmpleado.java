package Consolas;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import club.Club;
import DeporteIndividual.Instalacion;
import Entrenamiento.Asistencia;
import Entrenamiento.SesionesEntrenamiento;
import Partidos.Partido;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.TiendaDeportiva;
import TiendaCafeteria.Venta;
import Torneos.EstadoTorneo;
import Torneos.Modalidad;
import Torneos.Torneo;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;
import Usuarios.Turno;
import Usuarios.Usuario;

public class MenuEmpleado {

    private Club club;
    private Empleado empleado;
    private Entrada entrada;

    public MenuEmpleado(Club club, Empleado empleado, Entrada entrada) {
        this.club = club;
        this.empleado = empleado;
        this.entrada = entrada;
    }

    public void mostrar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            System.out.println("===== MENÚ " + nombreRol().toUpperCase() + ": " + empleado.getLogin() + " =====");
            System.out.println("1. Ver mis turnos");
            System.out.println("2. Solicitar un cambio de turno");
            System.out.println("3. Sugerir un producto nuevo");
            System.out.println("4. Ver mis sugerencias");
            System.out.println("5. Comprar en la tienda");
            System.out.println("6. Inscribirme a un torneo");
            System.out.println("7. Retirarme de un torneo");
            int maximo = 9;
            if (empleado instanceof Entrenador) {
                System.out.println("8. Programar una sesión de entrenamiento");
                System.out.println("9. Ver mis sesiones");
                System.out.println("10. Registrar asistencia");
                System.out.println("11. Registrar evaluación de desempeño");
                System.out.println("12. Registrar tarjeta en un partido");
                maximo = 14;
            } else if (empleado instanceof Fisioterapeuta) {
                System.out.println("10. Registrar evaluación física de un jugador");
                System.out.println("11. Consultar historial médico de un jugador");
                maximo = 11;
            }
            System.out.println("0. Salir");
            opcion = entrada.leerEntero("Opción: ", 0, maximo);

            try {
                ejecutar(opcion);
            } catch (RuntimeException e) {
                System.out.println("No se pudo completar: " + e.getMessage());
            }
        }
    }

    private void ejecutar(int opcion) {
        if (opcion == 1) {
            verTurnos();
        } else if (opcion == 2) {
            solicitarCambioTurno();
        } else if (opcion == 3) {
            sugerirProducto();
        } else if (opcion == 4) {
            verSugerencias();
        } else if (opcion == 5) {
            comprarEnTienda();
        } else if (opcion == 6) {
            verHistorial();
        } else if (opcion == 7) {
            verTorneos();
        } else if (opcion == 8) {
            inscribirseEnTorneo();
        } else if (opcion == 9) {
            retirarseDeTorneo();
        } else if (empleado instanceof Entrenador) {
            Entrenador entrenador = (Entrenador) empleado;
            if (opcion == 10) {
                programarSesion(entrenador);
            } else if (opcion == 11) {
                verSesiones(entrenador);
            } else if (opcion == 12) {
                registrarAsistencia(entrenador);
            } else if (opcion == 13) {
                registrarEvaluacion(entrenador);
            } else if (opcion == 14) {
                registrarTarjeta(entrenador);
            }
        } else if (empleado instanceof Fisioterapeuta) {
            Fisioterapeuta fisio = (Fisioterapeuta) empleado;
            if (opcion == 10) {
                registrarEvaluacionFisica(fisio);
            } else if (opcion == 11) {
                verHistorialMedico(fisio);
            }
        }
    }

    private String nombreRol() {
        if (empleado instanceof Entrenador) {
            return "Entrenador";
        }
        if (empleado instanceof Fisioterapeuta) {
            return "Fisioterapeuta";
        }
        return "Empleado";
    }


    // Turnos

    private String describirTurno(Turno t) {
        String texto = t.getDia() + " " + t.getHora();
        if (t.estaPendiente() && t.getTurnoNuevo() != null) {
            texto += " (cambio pendiente a " + t.getTurnoNuevo().getDia() + " " + t.getTurnoNuevo().getHora() + ")";
        } else if (t.getEstado() != null) {
            texto += " (última solicitud: " + t.getEstado() + ")";
        }
        return texto;
    }

    private void verTurnos() {
        List<Turno> turnos = empleado.getTurnos();
        if (turnos.isEmpty()) {
            System.out.println("No tienes turnos asignados.");
            return;
        }
        for (int i = 0; i < turnos.size(); i++) {
            System.out.println((i + 1) + ". " + describirTurno(turnos.get(i)));
        }
    }

    private void solicitarCambioTurno() {
        List<Turno> turnos = empleado.getTurnos();
        if (turnos.isEmpty()) {
            System.out.println("No tienes turnos asignados.");
            return;
        }
        verTurnos();
        int eleccion = entrada.leerEntero("Número del turno a cambiar (0 para cancelar): ", 0, turnos.size());
        if (eleccion == 0) {
            return;
        }
        Turno actual = turnos.get(eleccion - 1);

        DayOfWeek dia = leerDia();
        LocalTime hora = leerHora("Hora del nuevo turno");
        Turno nuevo = new Turno(empleado, dia, hora);

        empleado.solicitarCambioTurno(actual, nuevo);
        System.out.println("Solicitud enviada. Queda pendiente hasta que el administrador la gestione.");
    }

    private DayOfWeek leerDia() {
        String[] nombres = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (int i = 0; i < nombres.length; i++) {
            System.out.println((i + 1) + ". " + nombres[i]);
        }
        int d = entrada.leerEntero("Día del nuevo turno: ", 1, 7);
        return DayOfWeek.of(d);
    }

    private LocalTime leerHora(String mensaje) {
        while (true) {
            String texto = entrada.leerTexto(mensaje + " (HH:MM): ");
            try {
                return LocalTime.parse(texto);
            } catch (Exception e) {
                System.out.println("Hora no válida. Ejemplo: 15:30");
            }
        }
    }

    // Sugerencias
    private void sugerirProducto() {
        String descripcion = entrada.leerTexto("Describe el producto que quieres sugerir: ");
        empleado.sugerirProducto(descripcion);
        System.out.println("Sugerencia registrada. Queda pendiente de aprobación del administrador.");
    }

    private void verSugerencias() {
        System.out.println("--- Pendientes ---");
        if (empleado.getSugerenciasPendientes().isEmpty()) {
            System.out.println("  (ninguna)");
        }
        for (String s : empleado.getSugerenciasPendientes()) {
            System.out.println("  " + s);
        }
        System.out.println("--- Aprobadas ---");
        if (empleado.getSugerenciasAprobadas().isEmpty()) {
            System.out.println("  (ninguna)");
        }
        for (String s : empleado.getSugerenciasAprobadas()) {
            System.out.println("  " + s);
        }
    }

   
    // Tienda

    private void comprarEnTienda() {
        TiendaDeportiva tienda = club.getTienda();
        List<Articulo> articulos = tienda.getArticulosDisponibles();

        System.out.println("--- Artículos ---");
        for (int i = 0; i < articulos.size(); i++) {
            Articulo a = articulos.get(i);
            System.out.println((i + 1) + ". " + a.getNombre() + " | $" + a.getPrecio()
                    + " | disponibles: " + a.getCantidadDisponible() + " | " + a.getUbicacion());
        }

        Map<Articulo, Integer> carrito = new HashMap<>();
        int eleccion = -1;
        while (eleccion != 0) {
            eleccion = entrada.leerEntero("Número de artículo (0 para terminar): ", 0, articulos.size());
            if (eleccion != 0) {
                Articulo a = articulos.get(eleccion - 1);
                int cantidad = entrada.leerEntero("Cantidad: ", 1, 1000);
                int yaEnCarrito = 0;
                if (carrito.containsKey(a)) {
                    yaEnCarrito = carrito.get(a);
                }
                carrito.put(a, yaEnCarrito + cantidad);
            }
        }
        if (carrito.isEmpty()) {
            System.out.println("No elegiste nada.");
            return;
        }

        String codigo = null;
        int puntos = 0;
        if (entrada.leerSiNo("¿Quieres usar un código de descuento?")) {
            if (entrada.leerSiNo("¿Usar tu propio código?")) {
                codigo = empleado.getCodigoDescuento();
            } else {
                codigo = entrada.leerTexto("Código: ");
            }
        } else if (empleado.getPuntosFidelidad() > 0
                && entrada.leerSiNo("Tienes " + empleado.getPuntosFidelidad() + " puntos. ¿Quieres usarlos?")) {
            puntos = entrada.leerEntero("Puntos a usar: ", 1, empleado.getPuntosFidelidad());
        }

        Venta venta = tienda.vender(empleado, carrito, codigo, puntos, club.getEmpleados(), LocalDate.now());
        System.out.println("Compra realizada.");
        System.out.println("Subtotal: $" + venta.getValor() + " | Descuento: $" + venta.getDescuento()
                + " | IVA: $" + venta.getImpuesto());
        System.out.println("Total: $" + venta.calcularTotal());
        System.out.println("Puntos actuales: " + empleado.getPuntosFidelidad());
    }

    private void verHistorial() {
        System.out.println("Tienes " + empleado.getPuntosFidelidad() + " puntos.");
        if (empleado.getHistorialCompras().isEmpty()) {
            System.out.println("Aún no tienes compras.");
            return;
        }
        for (Venta v : empleado.getHistorialCompras()) {
            System.out.println(v.getFecha() + " | " + v.getRubro() + " | $" + v.calcularTotal());
        }
    }


    // Torneos (los empleados solo entran a torneos individuales o de dobles)

    private void verTorneos() {
        List<Torneo> lista = new ArrayList<>(club.getTorneos());
        if (lista.isEmpty()) {
            System.out.println("No hay torneos creados.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Torneo t = lista.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " | " + t.getTipo() + " | "
                    + t.getModalidad() + " | " + t.getDeporte() + " " + t.getCategoria()
                    + " | " + t.getFecha() + " | " + t.getEstado()
                    + " | cupos libres: " + t.getCuposLibres() + "/" + t.getCupos());
        }
    }

    private Torneo elegirTorneo() {
        List<Torneo> lista = new ArrayList<>(club.getTorneos());
        if (lista.isEmpty()) {
            System.out.println("No hay torneos creados.");
            return null;
        }
        verTorneos();
        int eleccion = entrada.leerEntero("Número de torneo (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return null;
        }
        return lista.get(eleccion - 1);
    }

    private void inscribirseEnTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        if (torneo.getEstado() != EstadoTorneo.EN_INSCRIPCION) {
            System.out.println("Ese torneo ya no admite inscripciones.");
            return;
        }
        if (torneo.getModalidad() == Modalidad.CONJUNTO) {
            System.out.println("Los empleados solo pueden inscribirse a torneos individuales.");
            return;
        }

        Usuario companero = null;
        if (torneo.getModalidad() == Modalidad.DOBLES) {
            String login = entrada.leerTexto("Login de tu pareja: ");
            companero = club.buscarUsuario(login);
            if (companero == null) {
                System.out.println("No existe un usuario con ese login.");
                return;
            }
        }

        // La validación de turno el día del torneo y la tarifa gratuita las hace Torneo
        torneo.inscribirElJugador(empleado, companero, false);
        System.out.println("Inscripción realizada.");
    }

    private void retirarseDeTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        torneo.retirarJugador(empleado);
        System.out.println("Te retiraste de " + torneo.getNombre() + ". Se liberó tu cupo.");
    }

    // Entrenador
    private void programarSesion(Entrenador entrenador) {
        List<Instalacion> instalaciones = new ArrayList<>(club.getInstalaciones());
        if (instalaciones.isEmpty()) {
            System.out.println("No hay instalaciones cargadas.");
            return;
        }
        System.out.println("--- Instalaciones ---");
        for (int i = 0; i < instalaciones.size(); i++) {
            Instalacion ins = instalaciones.get(i);
            System.out.println((i + 1) + ". " + ins.getNombre() + " (" + ins.getTipo() + ")");
        }
        int eleccion = entrada.leerEntero("Número de instalación (0 para cancelar): ", 0, instalaciones.size());
        if (eleccion == 0) {
            return;
        }
        Instalacion instalacion = instalaciones.get(eleccion - 1);

        LocalDate fecha = entrada.leerFecha("Fecha de la sesión");
        LocalTime hora = leerHora("Hora de inicio");
        int duracion = entrada.leerEntero("Duración en minutos: ", 1, 480);

        SesionesEntrenamiento sesion = entrenador.programarSesion(fecha, hora, duracion, instalacion);
        System.out.println("Sesión programada en " + instalacion.getNombre() + ".");

        convocarJugadores(sesion);
    }

    private void convocarJugadores(SesionesEntrenamiento sesion) {
        List<Socio> candidatos = new ArrayList<>();
        for (Socio s : club.getSocios()) {
            if (!s.getComposicion().isEmpty()) {
                candidatos.add(s);
            }
        }
        if (candidatos.isEmpty()) {
            System.out.println("No hay jugadores inscritos en equipos para convocar.");
            return;
        }
        System.out.println("--- Jugadores que pueden ser convocados ---");
        for (int i = 0; i < candidatos.size(); i++) {
            System.out.println((i + 1) + ". " + candidatos.get(i).getLogin());
        }
        int eleccion = -1;
        while (eleccion != 0) {
            eleccion = entrada.leerEntero("Número de jugador a convocar (0 para terminar): ", 0, candidatos.size());
            if (eleccion != 0) {
                try {
                    sesion.convocar(candidatos.get(eleccion - 1));
                    System.out.println("Convocado: " + candidatos.get(eleccion - 1).getLogin());
                } catch (RuntimeException e) {
                    System.out.println("No se pudo convocar: " + e.getMessage());
                }
            }
        }
    }

    private SesionesEntrenamiento elegirSesion(Entrenador entrenador) {
        List<SesionesEntrenamiento> lista = entrenador.getSesiones();
        if (lista.isEmpty()) {
            System.out.println("No tienes sesiones programadas.");
            return null;
        }
        for (int i = 0; i < lista.size(); i++) {
            SesionesEntrenamiento s = lista.get(i);
            System.out.println((i + 1) + ". " + s.getInicio() + " a " + s.getFin()
                    + " | convocados: " + s.getJugConvocados().size());
        }
        int eleccion = entrada.leerEntero("Número de sesión (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return null;
        }
        return lista.get(eleccion - 1);
    }

    private Socio elegirConvocado(SesionesEntrenamiento sesion) {
        List<Socio> convocados = sesion.getJugConvocados();
        if (convocados.isEmpty()) {
            System.out.println("Esa sesión no tiene jugadores convocados.");
            return null;
        }
        for (int i = 0; i < convocados.size(); i++) {
            System.out.println((i + 1) + ". " + convocados.get(i).getLogin());
        }
        int eleccion = entrada.leerEntero("Número de jugador (0 para cancelar): ", 0, convocados.size());
        if (eleccion == 0) {
            return null;
        }
        return convocados.get(eleccion - 1);
    }

    private void verSesiones(Entrenador entrenador) {
        List<SesionesEntrenamiento> lista = entrenador.getSesiones();
        if (lista.isEmpty()) {
            System.out.println("No tienes sesiones programadas.");
            return;
        }
        for (SesionesEntrenamiento s : lista) {
            System.out.println(s.getInicio() + " a " + s.getFin());
            for (Socio j : s.getJugConvocados()) {
                Asistencia a = s.getAsistencia(j);
                String estado = "sin registrar";
                if (a != null) {
                    estado = a.getEstado();
                    if (a.tieneEvaluacion()) {
                        estado += ", nota " + a.getNota() + " (" + a.getComentario() + ")";
                    }
                }
                System.out.println("   - " + j.getLogin() + ": " + estado);
            }
        }
    }

    private void registrarAsistencia(Entrenador entrenador) {
        SesionesEntrenamiento sesion = elegirSesion(entrenador);
        if (sesion == null) {
            return;
        }
        Socio jugador = elegirConvocado(sesion);
        if (jugador == null) {
            return;
        }
        boolean asistio = entrada.leerSiNo("¿" + jugador.getLogin() + " asistió?");
        String estado = Asistencia.fallo;
        if (asistio) {
            estado = Asistencia.asistio;
        }
        entrenador.registrarAsistencia(sesion, jugador, estado);
        System.out.println("Asistencia registrada: " + jugador.getLogin() + " -> " + estado);
    }

    private void registrarEvaluacion(Entrenador entrenador) {
        SesionesEntrenamiento sesion = elegirSesion(entrenador);
        if (sesion == null) {
            return;
        }
        Socio jugador = elegirConvocado(sesion);
        if (jugador == null) {
            return;
        }
        int nota = entrada.leerEntero("Nota de desempeño (0 a 10): ", 0, 10);
        String comentario = entrada.leerTexto("Comentario: ");
        entrenador.registrarEvaluacion(sesion, jugador, nota, comentario);
        System.out.println("Evaluación registrada para " + jugador.getLogin() + ".");
    }

    private void registrarTarjeta(Entrenador entrenador) {
        Set<Partido> unicos = new LinkedHashSet<>(club.getPartidos());
        for (Socio s : club.getSocios()) {
            unicos.addAll(s.getPartidos());
        }
        List<Partido> partidos = new ArrayList<>(unicos);
        if (partidos.isEmpty()) {
            System.out.println("No hay partidos registrados.");
            return;
        }
        for (int i = 0; i < partidos.size(); i++) {
            Partido p = partidos.get(i);
            System.out.println((i + 1) + ". " + p.getFecha() + " | " + p.getDeporte().getNombre());
        }
        int eleccion = entrada.leerEntero("Número de partido (0 para cancelar): ", 0, partidos.size());
        if (eleccion == 0) {
            return;
        }
        Partido partido = partidos.get(eleccion - 1);

        List<Socio> participantes = partido.getParticipantes();
        if (participantes.isEmpty()) {
            System.out.println("Ese partido no tiene participantes.");
            return;
        }
        for (int i = 0; i < participantes.size(); i++) {
            System.out.println((i + 1) + ". " + participantes.get(i).getLogin());
        }
        int j = entrada.leerEntero("Número de jugador (0 para cancelar): ", 0, participantes.size());
        if (j == 0) {
            return;
        }
        Socio jugador = participantes.get(j - 1);

        System.out.println("1. AMARILLA");
        System.out.println("2. ROJA");
        int t = entrada.leerEntero("Tipo de tarjeta: ", 1, 2);
        String tipo = "AMARILLA";
        if (t == 2) {
            tipo = "ROJA";
        }
        entrenador.registrarTarjeta(partido, jugador, tipo);
        System.out.println("Tarjeta " + tipo + " registrada a " + jugador.getLogin() + ".");
    }


    // Fisioterapeuta

    private Socio elegirJugador() {
        List<Socio> lista = new ArrayList<>(club.getSocios());
        if (lista.isEmpty()) {
            System.out.println("No hay jugadores.");
            return null;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i).getLogin());
        }
        int eleccion = entrada.leerEntero("Número de jugador (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return null;
        }
        return lista.get(eleccion - 1);
    }

    private void registrarEvaluacionFisica(Fisioterapeuta fisio) {
        Socio jugador = elegirJugador();
        if (jugador == null) {
            return;
        }
        String descripcion = entrada.leerTexto("Observaciones de la evaluación: ");
        boolean apto = entrada.leerSiNo("¿El jugador queda apto para jugar?");
        fisio.registrarEvaluacionFisica(jugador, descripcion, apto);
        System.out.println("Evaluación registrada. " + jugador.getLogin() + " queda "
                + (apto ? "APTO" : "NO APTO") + ".");
    }

    private void verHistorialMedico(Fisioterapeuta fisio) {
        Socio jugador = elegirJugador();
        if (jugador == null) {
            return;
        }
        List<String> historial = fisio.consultarHistorialMedico(jugador);
        if (historial.isEmpty()) {
            System.out.println("Sin registros médicos para " + jugador.getLogin() + ".");
            return;
        }
        for (String r : historial) {
            System.out.println("  " + r);
        }
    }
}