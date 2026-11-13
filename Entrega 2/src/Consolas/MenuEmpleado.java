package Consolas;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import club.Club;
import Torneos.EstadoTorneo;
import Torneos.Modalidad;
import Torneos.Torneo;
import Usuarios.Empleado;
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
        boolean esFisio = empleado instanceof Fisioterapeuta;
        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            String rol = esFisio ? "FISIOTERAPEUTA" : "ENTRENADOR";
            System.out.println("===== MENÚ " + rol + ": " + empleado.getLogin() + " =====");
            System.out.println("1. Ver mis turnos");
            System.out.println("2. Solicitar cambio de turno");
            System.out.println("3. Sugerir un producto");
            System.out.println("4. Ver mis sugerencias");
            System.out.println("5. Ver torneos");
            System.out.println("6. Inscribirme a un torneo");
            System.out.println("7. Retirarme de un torneo");
            int maximo = 7;
            if (esFisio) {
                System.out.println("8. Registrar estado físico de un jugador");
                System.out.println("9. Consultar estado físico de un jugador");
                maximo = 9;
            }
            System.out.println("0. Salir");
            opcion = entrada.leerEntero("Opción: ", 0, maximo);

            try {
                if (opcion == 1) {
                    verTurnos();
                } else if (opcion == 2) {
                    solicitarCambioTurno();
                } else if (opcion == 3) {
                    sugerirProducto();
                } else if (opcion == 4) {
                    verSugerencias();
                } else if (opcion == 5) {
                    verTorneos();
                } else if (opcion == 6) {
                    inscribirseEnTorneo();
                } else if (opcion == 7) {
                    retirarseDeTorneo();
                } else if (opcion == 8) {
                    registrarEstadoFisico();
                } else if (opcion == 9) {
                    consultarEstadoFisico();
                }
            } catch (RuntimeException e) {
                System.out.println("No se pudo completar: " + e.getMessage());
            }
        }
    }

    // Turnos

    private void verTurnos() {
        if (empleado.getTurnos().isEmpty()) {
            System.out.println("No tienes turnos asignados.");
            return;
        }
        for (Turno t : empleado.getTurnos()) {
            System.out.println(t.getDia() + " " + t.getHora());
        }
    }

    private void solicitarCambioTurno() {
        List<Turno> turnos = empleado.getTurnos();
        if (turnos.isEmpty()) {
            System.out.println("No tienes turnos para cambiar.");
            return;
        }
        for (int i = 0; i < turnos.size(); i++) {
            System.out.println((i + 1) + ". " + turnos.get(i).getDia() + " " + turnos.get(i).getHora());
        }
        int eleccion = entrada.leerEntero("Turno a cambiar (0 para cancelar): ", 0, turnos.size());
        if (eleccion == 0) {
            return;
        }
        Turno actual = turnos.get(eleccion - 1);

        System.out.println("Día de la semana: 1=Lunes ... 7=Domingo");
        int dia = entrada.leerEntero("Nuevo día: ", 1, 7);
        int hora = entrada.leerEntero("Nueva hora (0-23): ", 0, 23);

        // Supuesto: Turno(Empleado, DayOfWeek, LocalTime)
        Turno nuevo = new Turno(empleado, DayOfWeek.of(dia), LocalTime.of(hora, 0));
        empleado.solicitarCambioTurno(actual, nuevo);
        System.out.println("Solicitud enviada. El administrador la revisará.");
    }

    //Sugerencia

    private void sugerirProducto() {
        String descripcion = entrada.leerTexto("Describe el producto o snack que sugieres: ");
        empleado.sugerirProducto(descripcion);
        System.out.println("Sugerencia registrada.");
    }

    private void verSugerencias() {
        System.out.println("--- Pendientes ---");
        for (String s : empleado.getSugerenciasPendientes()) {
            System.out.println("  " + s);
        }
        System.out.println("--- Aprobadas ---");
        for (String s : empleado.getSugerenciasAprobadas()) {
            System.out.println("  " + s);
        }
    }

    // Torneos 

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

        torneo.inscribirElJugador(empleado, companero, false);
        System.out.println("Inscripción realizada (sin costo para empleados).");
    }

    private void retirarseDeTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        torneo.retirarJugador(empleado);
        System.out.println("Te retiraste de " + torneo.getNombre() + ".");
    }

    //FISIOTERAPEUTA 

    private Socio elegirSocio() {
        String login = entrada.leerTexto("Login del jugador: ");
        Socio socio = club.buscarSocio(login);
        if (socio == null) {
            System.out.println("No existe un socio con ese login.");
        }
        return socio;
    }

    private void registrarEstadoFisico() {
        Socio socio = elegirSocio();
        if (socio == null) {
            return;
        }
        String descripcion = entrada.leerTexto("Observaciones de la valoración: ");
        boolean apto = entrada.leerSiNo("¿El jugador está apto para jugar?");
        socio.agregarRegistroMedico(descripcion, apto, empleado.getLogin());
        System.out.println("Registro guardado. " + socio.getLogin() + " ahora está "
                + (apto ? "APTO" : "NO APTO") + ".");
    }

    private void consultarEstadoFisico() {
        Socio socio = elegirSocio();
        if (socio == null) {
            return;
        }
        System.out.println(socio.getLogin() + ": " + (socio.isFisicoApto() ? "APTO" : "NO APTO"));
    }
}