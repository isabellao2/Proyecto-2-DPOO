package persistencias;

import club.Club;
import Excepciones.PersistenciaException;

import java.io.IOException;

/**
 * Punto único de entrada a la persistencia. Respeta el orden de carga por dependencias:
 * usuarios -> turnos -> deporte de conjunto -> instalaciones -> tienda y cafetería.
 */
public class PersistenciaCentral {

    private final PersistenciaUsuarios usuarios = new PersistenciaUsuarios();
    private final PersistenciaTurnos turnos = new PersistenciaTurnos();
    private final PersistenciaDeporteConjunto deporteConjunto = new PersistenciaDeporteConjunto();
    private final PersistenciaDeporteIndividual deporteIndividual = new PersistenciaDeporteIndividual();
    private final PersistenciaTiendaCafeteria tiendaCafeteria = new PersistenciaTiendaCafeteria();

    public void cargarTodo(Club club, String carpeta) throws IOException, PersistenciaException {
        usuarios.cargarUsuarios(club, carpeta);
        turnos.cargarTurnos(club, carpeta);
        deporteConjunto.cargarDeporteConjunto(club, carpeta);
        deporteIndividual.cargarInstalaciones(club, carpeta);
        tiendaCafeteria.cargarTiendaCafeteria(club, carpeta);
        club.conectarAdministradores(); //cambio
    }

    /** Guarda lo que las consolas pueden modificar: usuarios (puntos), turnos y tienda/cafetería. */
    public void guardarTodo(Club club, String carpeta) throws IOException, PersistenciaException {
        usuarios.guardarUsuarios(club, carpeta);
        turnos.guardarTurnos(club, carpeta);
        deporteConjunto.guardarDeporteConjunto(club,carpeta);
        tiendaCafeteria.guardarTiendaCafeteria(club, carpeta);
    }
}
