package Consolas;

import DeporteConjunto.Equipo;
import DeporteIndividual.Instalacion;
import Entrenamiento.Asistencia;
import Entrenamiento.SesionesEntrenamiento;
import Excepciones.ClubException;
import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Entrenador;
import Usuarios.Socio;
import Usuarios.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PruebaEntrenamiento {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
    
            System.out.println("    Sesion Entrenamiento   ");
  

            System.out.print("Enter para usar datos:");
            String ruta = scanner.nextLine();

            if (ruta.equals("")) {
                ruta = "datos";
            }

            Club datos = new Club ();
            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodos(datos, ruta);

            System.out.println("Datos cargados correctamente.");

            System.out.println("\nINICIO ");

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

            System.out.println("Bienvenido, " + entrenador.getLogin());

            System.out.println("\nINSTALACIONES DISPONIBLES");

            for (DeporteIndividual.Instalacion inst : datos.getInstalaciones()) {
                System.out.println("- " + inst.getNombre());
            }

            System.out.println("\nEQUIPOS DISPONIBLES");

            for (Equipo eq : datos.getEquipos()) {
                System.out.println("- " + eq.getNombre());
            }

            SesionesEntrenamiento sesion = null;

            while (sesion == null) {

                System.out.print("\nNombre de la instalacion: ");
                String nombreInstalacion = scanner.nextLine();

                Instalacion instalacion =  datos.buscarInstalacion(nombreInstalacion);


                if (instalacion == null) {
                    System.out.println("No encontramos esa instalacion.");
                    continue;
                }

                System.out.print("Fecha (AAAA-MM-DD): ");
                String textoFecha = scanner.nextLine();

                LocalDate fecha;

                try {
                    fecha = LocalDate.parse(textoFecha);
                } catch (Exception e) {
                    System.out.println("Fecha incorrecta.");
                    continue;
                }

                System.out.print("Hora de inicio (HH:mm): ");
                String textoHora = scanner.nextLine();

                LocalTime hora;

                try {
                    hora = LocalTime.parse(textoHora);
                } catch (Exception e) {
                    System.out.println("Hora incorrecta.");
                    continue;
                }

                System.out.print("Duracion en minutos: ");
                int duracion;

                try {
                    duracion = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    System.out.println("Debes escribir un numero.");
                    continue;
                }

                try {
                    sesion = entrenador.programarSesion(
                            fecha, hora, duracion, instalacion);

                    System.out.println("Sesion programada correctamente.");

                    System.out.println("Inicio: " + sesion.getInicio());
                    System.out.println("Fin: " + sesion.getFin());

                } catch (ClubException e) {
                    System.out.println("No se pudo programar: "
                            + e.getMessage());

                    System.out.print("Quieres intentarlo otra vez? (s/n): ");
                    String respuesta = scanner.nextLine();

                    if (!respuesta.equalsIgnoreCase("s")) {
                        persistencia.guardaTodo(datos, ruta);
                        return;
                    }
                }
            }

            Equipo equipo = null;

            while (equipo == null) {

                System.out.print("\nNombre del equipo: ");
                String nombreEquipo = scanner.nextLine();

                equipo = datos.buscarEquipo(nombreEquipo);

                if (equipo == null) {
                    System.out.println("No encontramos ese equipo.");
                }
            }

            System.out.println("\nCONVOCATORIA");

            for (Socio socio : equipo.getJugadores()) {

                try {
                    sesion.convocar(socio);

                    System.out.println("Convocado: " + socio.getLogin());

                } catch (ClubException e) {
                    System.out.println(e.getMessage());
                }
            }

            List<Socio> convocados =
                    new ArrayList<Socio>(sesion.getJugConvocados());

            System.out.println("Total de convocados: " + convocados.size());

            System.out.println("\nASISTENCIA Y EVALUACIONES");

            for (Socio socio : convocados) {

                System.out.print("Asistio " + socio.getLogin()
                        + "? (s/n): ");

                String respuesta = scanner.nextLine();

                boolean asistio = respuesta.equalsIgnoreCase("s");

                try {
                    if (asistio) {

                   
                        sesion.registrarAsistencia(socio, "ASISTIO");

                 
                        System.out.print("Nota de desempeno (0-10): ");
                        int nota;

                        try {
                            nota = Integer.parseInt(scanner.nextLine());
                        } catch (Exception e) {
                            System.out.println("La nota debe ser un numero.");
                            continue;
                        }

   
                        System.out.print("Comentario: ");
                        String comentario = scanner.nextLine();

        
                        sesion.registrarAsistencia(
                                socio, "ASISTIO", nota, comentario);

                        System.out.println("Evaluacion guardada.");

                    } else {
                        sesion.registrarAsistencia(socio, "FALLO");

                        System.out.println("Inasistencia registrada.");
                    }

                } catch (ClubException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            System.out.println("\nResumen entreno :");

            for (Asistencia asistencia : sesion.getAsistencias()) {

                System.out.print(
                        asistencia.getSocio().getLogin() + ": "
                        + asistencia.getEstado());

                if (asistencia.tieneEvaluacion()) {
                    System.out.print(" | Nota: "
                            + asistencia.getNota());

                    System.out.print(" | Comentario: "
                            + asistencia.getComentario());
                }

                System.out.println();
            }
            persistencia.guardarTodo(datos, ruta);

            System.out.println("\nCambios guardados correctamente.");

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }

        scanner.close();
    }
}
