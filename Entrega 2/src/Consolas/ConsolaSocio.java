package Consolas;

import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Socio;

public class ConsolaSocio {

    private static final String CARPETA_DATOS = "datos";

    public static void main(String[] args) {
        Club club = new Club();
        PersistenciaCentral persistencia = new PersistenciaCentral();
        Entrada entrada = new Entrada();

        // 1. Cargar datos
        try {
            persistencia.cargarTodo(club, CARPETA_DATOS);
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
            return;
        }

        // 2. Login (3 intentos)
        Socio socio = Autenticacion.iniciarSesion(club, entrada, Socio.class, 3);
        if (socio == null) {
            return;
        }

        // 3. Menú
        MenuSocio menu = new MenuSocio(club, socio, entrada);
        menu.mostrar();

        // 4. Guardar al salir
        try {
            persistencia.guardarTodo(club, CARPETA_DATOS);
            System.out.println("Cambios guardados.");
        } catch (Exception e) {
            System.out.println("No se pudieron guardar los cambios: " + e.getMessage());
        }
    }
}
