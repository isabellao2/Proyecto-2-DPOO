package Consolas;

import DeporteConjunto.ComposicionEquipo;
import DeporteConjunto.Equipo;
import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Entrenador;
import Usuarios.Usuario;

import java.util.List;
import java.util.Scanner;

public class PruebaInformarEquipo {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("    INFORMACION DE EQUIPO   ");

    
            System.out.print("Enter para usar datos: ");
            String ruta = scanner.nextLine();

            if (ruta.equals("")) {
                ruta = "datos";
            }

            Club datos = new Club();
            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodo(datos, ruta);

            System.out.println("Datos cargados correctamente.");

            System.out.println("\nINICIO DE SESION");

            System.out.print("Login del entrenador: ");
            String login = scanner.nextLine();

            System.out.print("Contrasena: ");
            String contrasena = scanner.nextLine();

            Usuario usuario = datos.buscarUsuario(login);

            if (usuario == null) {
                System.out.println("El usuario no existe.");
                return;
            }

            if (!usuario.verificar(login, contrasena)) {
                System.out.println("Contrasena incorrecta.");
                return;
            }

            if (!(usuario instanceof Entrenador)) {
                System.out.println("El usuario no es un entrenador.");
                return;
            }

            Entrenador entrenador = (Entrenador) usuario;
            System.out.println("Bienvenido, entrenador " + entrenador.getLogin());

            Equipo equipo = null;

            while (equipo == null) {
                System.out.print("\nIngrese el nombre del equipo a revisar: ");
                String nombreEquipo = scanner.nextLine().trim();

                equipo = datos.buscarEquipo(nombreEquipo);

                if (equipo == null) {
                    System.out.println("No encontramos ese equipo. Intente de nuevo.");
                }
            }

            // info equiop
           
            System.out.println("\nREPORTE DEL EQUIPO: " + equipo.getNombre().toUpperCase());
            System.out.println("Deporte: " + equipo.getDeporte().getNombre());
            System.out.println("Categoria: " + equipo.getCategoria().getNombre());

            int minimo = equipo.getDeporte().getNumeroMinimoJugadores();
            int habilitados = equipo.contarHabilitados();

            System.out.println("\nJUGADORES HABILITADOS: " + habilitados);
            System.out.println("MINIMO REQUERIDO: " + minimo);

            if (equipo.cumpleMinimoJugadores()) {
                System.out.println("ESTADO: El equipo SI cumple con el minimo para jugar un partido.");
            } else {
                System.out.println("ESTADO: El equipo NO cumple con el minimo. Faltan " + (minimo - habilitados) + " jugadores.");
            }

            // 2. Cambio: Tabla de jugadores disponibles (habilitados)
            System.out.println("\n--- JUGADORES DISPONIBLES ---");
            System.out.printf("%-20s | %-15s | %-10s%n", "LOGIN", "POSICION", "CAMISETA");
            System.out.println("--------------------------------------------------");
            
            boolean hayDisponibles = false;
            for (ComposicionEquipo comp : equipo.getComposicion()) {
                if (comp.estaHabilitado()) {
                    System.out.printf("%-20s | %-15s | %-10d%n", 
                            comp.getJugador().getLogin(), 
                            comp.getPosicion(), 
                            comp.getNumeroCamiseta());
                    hayDisponibles = true;
                }
            }
            
            if (!hayDisponibles) {
                System.out.println("   (No hay jugadores disponibles en este equipo)");
            }


            System.out.println("\n--- JUGADORES NO HABILITADOS (SANCIONADOS/LESIONADOS) ---");
            List<String> noHabilitados = equipo.listanohabilitados();
            
            if (noHabilitados.isEmpty()) {
                System.out.println("   (Ninguno)");
            } else {
                for (String razon : noHabilitados) {
                    System.out.println("   - " + razon);
                }
            }
            
            System.out.println("\nFin del reporte.");

        } catch (Exception e) {
            System.out.println("Ocurrio un error en el sistema: " + e.getMessage());
        }
    }
}