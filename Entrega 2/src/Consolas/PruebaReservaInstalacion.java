package Consolas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.Instalacion;
import DeporteIndividual.Reserva;
import Excepciones.ClubException;
import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Socio;
import Usuarios.Usuario;

public class PruebaReservaInstalacion {

    private static final String[] NIVELES = { "Principiante", "Intermedio", "Avanzado" };
    private static final String[] MODALIDADES = { "SENCILLOS", "DOBLES" };

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("RESERVA DE INSTALACIONES");

            Club datos = new Club();
            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodo(datos, "datos");

            System.out.println("Datos cargados correctamente.");
            System.out.println(datos.getDisciplinas().size() + " disciplinas, "
                    + datos.getInstalaciones().size() + " instalaciones.");

            System.out.println("\nINICIO DE SESION DEL SOCIO");
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Contrasena: ");
            String contrasena = scanner.nextLine();

            Usuario usuario = datos.buscarUsuario(login);

            if (usuario == null || !usuario.verificar(login, contrasena)) {
                System.out.println("Login o contrasena incorrectos.");
                return;
            }
            if (!(usuario instanceof Socio)) {
                System.out.println("El usuario no es un socio.");
                return;
            }

            Socio socio = (Socio) usuario;
            System.out.println("Bienvenido, " + socio.getLogin());

            boolean salir = false;

            while (!salir) {
                System.out.println("\nMENU");
                System.out.println("1. Inscribirme en una disciplina");
                System.out.println("2. Reservar una instalacion");
                System.out.println("3. Ver mis reservas");
                System.out.println("0. Salir");

                int opcion = leerEntero(scanner, "Opcion", 0, 3);

                if (opcion == 1) {
                    inscribir(scanner, datos, socio);
                } else if (opcion == 2) {
                    reservar(scanner, datos, socio);
                } else if (opcion == 3) {
                    mostrarReservas(socio);
                } else {
                    salir = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }
    }

    // ---------- Opciones del menu ----------

    private static void inscribir(Scanner scanner, Club datos, Socio socio) {
        List<DisciplinaIndividual> disciplinas = new ArrayList<DisciplinaIndividual>(datos.getDisciplinas());
        if (disciplinas.isEmpty()) {
            System.out.println("No hay disciplinas cargadas.");
            return;
        }

        System.out.println("\nDISCIPLINAS");
        for (int i = 0; i < disciplinas.size(); i++) {
            DisciplinaIndividual d = disciplinas.get(i);
            System.out.println((i + 1) + ". " + d.getNombre()
                    + (d.estaInscrito(socio) ? " (ya inscrito)" : ""));
        }
        DisciplinaIndividual disciplina = disciplinas.get(
                leerEntero(scanner, "Disciplina", 1, disciplinas.size()) - 1);

        System.out.println("\nNIVELES");
        for (int i = 0; i < NIVELES.length; i++) {
            System.out.println((i + 1) + ". " + NIVELES[i]);
        }
        String nivel = NIVELES[leerEntero(scanner, "Nivel", 1, NIVELES.length) - 1];

        try {
            socio.inscribirseEnDisciplina(disciplina, nivel);
            System.out.println("INSCRIPCION ACEPTADA: " + disciplina.getNombre() + " nivel " + nivel);
        } catch (ClubException e) {
            System.out.println("INSCRIPCION RECHAZADA: " + e.getMessage());
        }
    }

    private static void reservar(Scanner scanner, Club datos, Socio socio) {
        List<Instalacion> instalaciones = new ArrayList<Instalacion>(datos.getInstalaciones());
        if (instalaciones.isEmpty()) {
            System.out.println("No hay instalaciones cargadas.");
            return;
        }

        System.out.println("\nINSTALACIONES");
        for (int i = 0; i < instalaciones.size(); i++) {
            Instalacion ins = instalaciones.get(i);
            System.out.println((i + 1) + ". " + ins.getNombre() + " | " + disciplinaDe(datos, ins));
        }
        Instalacion instalacion = instalaciones.get(
                leerEntero(scanner, "Instalacion", 1, instalaciones.size()) - 1);

        LocalDate fecha = leerFecha(scanner, "Fecha (AAAA-MM-DD)");
        LocalTime horaInicio = leerHora(scanner, "Hora de inicio (HH:mm)");
        LocalTime horaFin = leerHora(scanner, "Hora de fin (HH:mm)");

        System.out.println("\nMODALIDADES");
        for (int i = 0; i < MODALIDADES.length; i++) {
            System.out.println((i + 1) + ". " + MODALIDADES[i]);
        }
        String modalidad = MODALIDADES[leerEntero(scanner, "Modalidad", 1, MODALIDADES.length) - 1];
        int jugadores = leerEntero(scanner, "Numero de jugadores", 1, 4);

        LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
        LocalDateTime fin = LocalDateTime.of(fecha, horaFin);

        System.out.println("\nESTADO ANTES");
        System.out.println("Reservas del socio: " + socio.getReservas().size());

        try {
            Reserva r = socio.reservarInstalacion(instalacion, inicio, fin, jugadores, modalidad);
            System.out.println("\nRESERVA APROBADA: " + r.getInstalacion().getNombre()
                    + " de " + r.getInicio() + " a " + r.getFin());
        } catch (ClubException e) {
            System.out.println("\nRESERVA RECHAZADA: " + e.getMessage());
        }

        System.out.println("\nESTADO DESPUES");
        System.out.println("Reservas del socio: " + socio.getReservas().size());
    }

    private static void mostrarReservas(Socio socio) {
        System.out.println("\nReservas de " + socio.getLogin() + ":");
        if (socio.getReservas().isEmpty()) {
            System.out.println("   (ninguna)");
        }
        for (Reserva r : socio.getReservas()) {
            System.out.println("   " + r.getInstalacion().getNombre() + " | "
                    + r.getInicio() + " a " + r.getFin());
        }
    }

    // ---------- Auxiliares ----------

    private static String disciplinaDe(Club datos, Instalacion ins) {
        for (DisciplinaIndividual d : datos.getDisciplinas()) {
            if (d.tieneInstalacion(ins)) {
                return d.getNombre();
            }
        }
        return "(sin disciplina)";
    }

    private static int leerEntero(Scanner scanner, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje + " [" + min + "-" + max + "]: ");
            try {
                int n = Integer.parseInt(scanner.nextLine().trim());
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // cae al mensaje de abajo
            }
            System.out.println("Escribe un numero entre " + min + " y " + max + ".");
        }
    }

    private static LocalDate leerFecha(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Fecha incorrecta.");
            }
        }
    }

    private static LocalTime leerHora(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            try {
                return LocalTime.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Hora incorrecta.");
            }
        }
    }
}

