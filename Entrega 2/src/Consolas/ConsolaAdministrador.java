package Consolas;

import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Administrador;

public class ConsolaAdministrador {

    private static final String CARPETA_DATOS = "datos";

    public static void main(String[] args) {
        Club club = new Club();
        PersistenciaCentral persistencia = new PersistenciaCentral();
        Entrada entrada = new Entrada();

        //Carga de datos
        try {
            persistencia.cargarTodo(club, CARPETA_DATOS);
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
            return;
        }

        // Autenticacion: máximo 3 intentos
        Administrador admin = Autenticacion.iniciarSesion(club, entrada, Administrador.class, 3);
        if (admin == null) {
            return;
        }

        // Menu del administrador
        MenuAdministrador menu = new MenuAdministrador(club, admin, entrada);
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