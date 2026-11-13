package Consolas;

import java.util.Scanner;

import DeporteConjunto.ComposicionEquipo;
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
            System.out.println(datos.getEquipos().size() + " equipos, "
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

            mostrarEquiposDelSocio(socio);

            System.out.println("\nEQUIPOS DEL CLUB");
            for (Equipo e : datos.getEquipos()) {
                System.out.println("- " + e.getNombre() + " | " + e.getDeporte().getNombre()
                        + " | " + e.getCategoria().getNombre()
                        + " | " + e.getComposicion().size() + "/" + e.getCupoMaximo());
            }

            boolean otra = true;

            while (otra) {

                System.out.print("\nNombre del equipo: ");
                Equipo equipo = datos.buscarEquipo(scanner.nextLine().trim());

                if (equipo == null) {
                    System.out.println("No encontramos ese equipo.");
                    continue;
                }

                System.out.print("Posicion: ");
                String posicion = scanner.nextLine().trim();

                System.out.print("Numero de camiseta: ");
                int camiseta;
                try {
                    camiseta = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Debes escribir un numero.");
                    continue;
                }

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

                System.out.print("\nProbar otra inscripcion? (s/n): ");
                otra = scanner.nextLine().equalsIgnoreCase("s");
            }

            mostrarEquiposDelSocio(socio);

            persistencia.guardarTodo(datos, ruta);
            System.out.println("\nCambios guardados correctamente.");

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }
    }

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
                    + c.getEquipo().getDeporte().getNombre());
        }
    }
}