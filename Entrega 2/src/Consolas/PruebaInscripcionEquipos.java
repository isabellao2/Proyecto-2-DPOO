package Consolas;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DeporteConjunto.ComposicionEquipo;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Excepciones.ClubException;
import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Socio;
import Usuarios.Usuario;

public class PruebaInscripcionEquipos {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("INSCRIPCION A EQUIPOS");

            String ruta = "datos";
            Club datos = new Club();
            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodo(datos, ruta);

            System.out.println("Datos cargados correctamente.");
            System.out.println(datos.getDeportes().size() + " deportes, "
                    + datos.getEquipos().size() + " equipos, "
                    + datos.getSocios().size() + " socios.");

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
            System.out.println("Bienvenido, " + socio.getLogin() + " (" + socio.getEdad() + " años)");

            boolean salir = false;

            while (!salir) {
                System.out.println("\nMENU");
                System.out.println("1. Inscribirme en un equipo");
                System.out.println("2. Ver mis equipos");
                System.out.println("0. Salir y guardar");

                int opcion = leerEntero(scanner, "Opcion", 0, 2);

                if (opcion == 1) {
                    inscribir(scanner, datos, socio);
                } else if (opcion == 2) {
                    mostrarEquiposDelSocio(socio);
                } else {
                    salir = true;
                }
            }

            persistencia.guardarTodo(datos, ruta);
            System.out.println("\nCambios guardados correctamente.");

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }
    }

    // Inscripcion 

    private static void inscribir(Scanner scanner, Club datos, Socio socio) {

        // 1. Deporte
        List<DeporteConjunto> deportes = new ArrayList<DeporteConjunto>(datos.getDeportes());
        if (deportes.isEmpty()) {
            System.out.println("No hay deportes cargados.");
            return;
        }

        System.out.println("\nDEPORTES");
        for (int i = 0; i < deportes.size(); i++) {
            DeporteConjunto d = deportes.get(i);
            ComposicionEquipo actual = socio.getComposicion(d);
            String estado = (actual == null)
                    ? "sin equipo"
                    : "ya estas en " + actual.getEquipo().getNombre();
            System.out.println((i + 1) + ". " + d.getNombre() + " (" + estado + ")");
        }
        DeporteConjunto deporte = deportes.get(
                leerEntero(scanner, "Deporte", 1, deportes.size()) - 1);

        // 2. Equipo de ese deporte
        List<Equipo> equipos = new ArrayList<Equipo>();
        for (Equipo e : datos.getEquipos()) {
            if (e.getDeporte().equals(deporte)) {
                equipos.add(e);
            }
        }
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos de " + deporte.getNombre() + ".");
            return;
        }

        System.out.println("\nEQUIPOS DE " + deporte.getNombre().toUpperCase());
        for (int i = 0; i < equipos.size(); i++) {
            Equipo e = equipos.get(i);
            System.out.println((i + 1) + ". " + e.getNombre() + " | " + e.getCategoria().getNombre()
                    + " (" + e.getCategoria().getEdadMinima() + "-" + e.getCategoria().getEdadMaxima()
                    + " años) | cupo " + e.getComposicion().size() + "/" + e.getCupoMaximo()
                    + " | " + estadoPara(socio, e));
        }
        Equipo equipo = equipos.get(leerEntero(scanner, "Equipo", 1, equipos.size()) - 1);

        // Posicion segun el deporte
        String[] posiciones = posicionesDe(deporte);
        System.out.println("\nPOSICIONES");
        for (int i = 0; i < posiciones.length; i++) {
            System.out.println((i + 1) + ". " + posiciones[i]);
        }
        String posicion = posiciones[leerEntero(scanner, "Posicion", 1, posiciones.length) - 1];

        // Camiseta
        System.out.println("\nCamisetas ocupadas en " + equipo.getNombre() + ": " + camisetasOcupadas(equipo));
        int camiseta = leerEntero(scanner, "Numero de camiseta", 1, 30 );

        // Inscribir
        System.out.println("\nESTADO ANTES");
        mostrarEquipo(equipo);

        try {
            ComposicionEquipo c = socio.inscribirseEnEquipo(equipo, posicion, camiseta);
            System.out.println("\nINSCRIPCION ACEPTADA: " + socio.getLogin() + " como "
                    + c.getPosicion() + " con la camiseta " + c.getNumeroCamiseta());
        } catch (ClubException e) {
            System.out.println("\nINSCRIPCION RECHAZADA: " + e.getMessage());
        }

        System.out.println("\nESTADO DESPUES");
        mostrarEquipo(equipo);
    }

    /** Estado del equipo respecto al socio, en el mismo orden en que Equipo.puedeAceptar valida. */
    private static String estadoPara(Socio socio, Equipo e) {
        if (e.tieneJugador(socio)) {
            return "YA ESTAS EN ESTE EQUIPO";
        }
        if (socio.perteneceAEquipoDe(e.getDeporte())) {
            return "YA ESTAS EN OTRO EQUIPO DE ESTE DEPORTE";
        }
        if (!e.getCategoria().cumpleEdad(socio.getEdad())) {
            return "EDAD NO APTA";
        }
        if (!e.hayCupo()) {
            return "LLENO";
        }
        return "DISPONIBLE";
    }

    private static String[] posicionesDe(DeporteConjunto d) {
        String nombre = DeporteConjunto.normalizar(d.getNombre());
        if (nombre.equals("futbol")) {
            return new String[] { "portero", "defensa", "mediocampista", "delantero" };
        }
        if (nombre.equals("baloncesto")) {
            return new String[] { "base", "escolta", "alero", "ala-pivot", "pivot" };
        }
        if (nombre.equals("voleibol")) {
            return new String[] { "armador", "opuesto", "central", "libero", "receptor" };
        }
        return new String[] { "jugador" };
    }

    // ---------- Salida de estado ----------

    private static void mostrarEquipo(Equipo e) {
        System.out.println(e.getNombre() + " (" + e.getDeporte().getNombre() + ", "
                + e.getCategoria().getNombre() + ") - cupo "
                + e.getComposicion().size() + "/" + e.getCupoMaximo());
        for (ComposicionEquipo c : e.getComposicion()) {
            System.out.println("   #" + c.getNumeroCamiseta() + " " + c.getJugador().getLogin()
                    + " - " + c.getPosicion());
        }
    }

    private static void mostrarEquiposDelSocio(Socio s) {
        System.out.println("\nEquipos de " + s.getLogin() + ":");
        if (s.getComposicion().isEmpty()) {
            System.out.println("   (ninguno)");
        }
        for (ComposicionEquipo c : s.getComposicion()) {
            System.out.println("   " + c.getEquipo().getNombre() + " - "
                    + c.getEquipo().getDeporte().getNombre() + " - " + c.getPosicion()
                    + " - camiseta " + c.getNumeroCamiseta());
        }
    }

    private static String camisetasOcupadas(Equipo e) {
        if (e.getComposicion().isEmpty()) {
            return "(ninguna)";
        }
        StringBuilder sb = new StringBuilder();
        for (ComposicionEquipo c : e.getComposicion()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(c.getNumeroCamiseta());
        }
        return sb.toString();
    }

    // Entrada 
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
}