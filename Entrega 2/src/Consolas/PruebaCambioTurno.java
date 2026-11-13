
package Consolas;

import Excepciones.ClubException;
import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Turno;
import Usuarios.Usuario;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class PruebaCambioTurno {

	public static void main(String[] args) {

         try (Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("CAMBIO DE TURNO DE EMPLEADOS");

            System.out.print("Enter para usar datos: ");
            String ruta = scanner.nextLine();
            if (ruta.equals("")) {
                ruta = "datos";
            }

             Club datos = new Club();

            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodo(datos, ruta);

            System.out.println("Datos cargados correctamente.");
            System.out.println(datos.getUsuarios().size() + " usuarios, " + datos.getEquipos().size() + " equipos, "
                    + datos.getInstalaciones().size() + " instalaciones.");

            System.out.println("Turnos actuales:");
            for (Empleado e : datos.getEmpleados()) {
                System.out.println(e.getLogin() + ": " + e.getTurnos());
            }


            System.out.println("\nINICIO DE SESION DEL EMPLEADO");
            System.out.print("Login: ");
            String loginEmpleado = scanner.nextLine();
            System.out.print("Contrasena: ");
            String contrasenaEmpleado = scanner.nextLine();

            Usuario usuario = datos.buscarUsuario(loginEmpleado);

            if (usuario == null
                    || !usuario.verificar(loginEmpleado, contrasenaEmpleado)) {
                System.out.println("Login o contrasena incorrectos.");
                return;
            }

            if (!(usuario instanceof Empleado)) {
                System.out.println("El usuario no es un empleado.");
                return;
            }

            Empleado empleado = (Empleado) usuario;
            System.out.println("Bienvenido, " + empleado.getLogin());

            if (empleado.getTurnos().isEmpty()) {
                System.out.println("No tienes turnos asignados.");
                return;
            }

            boolean otraSolicitud = true;

            while (otraSolicitud) {

                List<Turno> turnos = empleado.getTurnos();

                System.out.println("\nTus Turnos");
                for (int i = 0; i < turnos.size(); i++) {
                    System.out.println((i + 1) + ". " + turnos.get(i));
                }

                try {
                    System.out.print("Numero del turno que quieres cambiar: ");
                    int numeroTurno = Integer.parseInt(scanner.nextLine().trim());

                    if (numeroTurno < 1 || numeroTurno > turnos.size()) {
                        System.out.println("Numero de turno incorrecto.");
                        continue;
                    }

                    Turno turnoActual = turnos.get(numeroTurno - 1);

                    System.out.println("Dias: 1=Lunes, 2=Martes, 3=Miercoles,");
                    System.out.println("4=Jueves, 5=Viernes, 6=Sabado, 7=Domingo");
                    System.out.print("Nuevo dia: ");
                    int numeroDia = Integer.parseInt(scanner.nextLine().trim());

                    if (numeroDia < 1 || numeroDia > 7) {
                        System.out.println("Dia incorrecto.");
                        continue;
                    }

                    System.out.print("Nueva hora (HH:mm): ");
                    LocalTime horaNueva;
                    try {
                        horaNueva = LocalTime.parse(scanner.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Formato de hora incorrecto.");
                        continue;
                    }

                    Turno turnoNuevo = new Turno(
                            empleado, DayOfWeek.of(numeroDia), horaNueva);

                    try {
                        empleado.solicitarCambioTurno(turnoActual, turnoNuevo);
                        System.out.println("Solicitud enviada correctamente.");
                    } catch (ClubException e) {
                        System.out.println("No se pudo enviar la solicitud: "
                                + e.getMessage());
                        continue;
                    }

                    System.out.println("\nINICIO DE SESION DEL ADMINISTRADOR");
                    System.out.print("Login del administrador: ");
                    String loginAdmin = scanner.nextLine();
                    System.out.print("Contrasena del administrador: ");
                    String contrasenaAdmin = scanner.nextLine();

                    Usuario usuarioAdmin = datos.buscarUsuario(loginAdmin);

                    if (usuarioAdmin == null) {
                        System.out.println("Administrador no encontrado.");
                    } else if (!usuarioAdmin.verificar(loginAdmin, contrasenaAdmin)) {
                        System.out.println("Contrasena incorrecta.");
                    } else if (!(usuarioAdmin instanceof Administrador)) {
                        System.out.println("Este usuario no es administrador.");
                    } else {
                        Administrador administrador = (Administrador) usuarioAdmin;

                        System.out.print("Aprobar el cambio? (s/n): ");
                        boolean aprobar = scanner.nextLine().equalsIgnoreCase("s");

                        try {
                            administrador.gestionarCambioTurno(
                                    empleado, turnoActual, aprobar,
                                    datos.getEmpleados());

                            System.out.println(aprobar
                                    ? "Cambio aprobado."
                                    : "Cambio rechazado.");
                        } catch (ClubException e) {
                            System.out.println("No se pudo realizar el cambio: "
                                    + e.getMessage());
                        }
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Debes escribir un numero.");
                    continue;
                }

                System.out.print("Otra solicitud? (s/n): ");
                otraSolicitud = scanner.nextLine().equalsIgnoreCase("s");
            }

            System.out.println("\nTurnos finales:");
            for (Empleado e : datos.getEmpleados()) {
                System.out.println(e.getLogin() + ": " + e.getTurnos());
            }

            persistencia.guardarTodo(datos,ruta);
            System.out.println("Cambios guardados correctamente.");

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }

      
    }
}