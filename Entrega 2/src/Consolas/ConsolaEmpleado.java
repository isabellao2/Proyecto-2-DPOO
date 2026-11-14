package Consolas;

import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Empleado;

public class ConsolaEmpleado {

    private static final String CARPETA_DATOS = "datos";

 
    public static void main(String[] args) {
        Club club = new Club();
        PersistenciaCentral persistencia = new PersistenciaCentral();
        Entrada entrada = new Entrada();
        System.out.println("Carpeta de datos: " + new java.io.File("datos").getAbsolutePath());

        // Carga de datos
        try {
            persistencia.cargarTodo(club, CARPETA_DATOS);
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
            return;
        }

        // Autenticación: máximo 3 intentos (acepta entrenador y fisioterapeuta)
        Empleado empleado = Autenticacion.iniciarSesion(club, entrada, Empleado.class, 3);
        if (empleado == null) {
            return;
        }

        // Menú según el tipo de empleado
        MenuEmpleado menu = new MenuEmpleado(club, empleado, entrada);
        menu.mostrar();

        // Guardar al salir
        try {
            persistencia.guardarTodo(club, CARPETA_DATOS);
            System.out.println("Cambios guardados.");
        } catch (Exception e) {
            System.out.println("No se pudieron guardar los cambios: " + e.getMessage());
        }
    }
}