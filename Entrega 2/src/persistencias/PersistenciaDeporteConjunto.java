package persistencias;

import club.Club;
import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Excepciones.PersistenciaException;
import Usuarios.Socio;
import DeporteConjunto.ComposicionEquipo;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Lee deportes.txt, categorias.txt, equipos.txt y composiciones.txt (en ese orden). */
public class PersistenciaDeporteConjunto {

    private static final String SEPARADOR = ";";

    public void cargarDeporteConjunto(Club club, String carpeta) throws IOException, PersistenciaException {
        try {
            for (String[] p : leer(carpeta + "/deportes.txt")) {
                club.agregarDeporte(new DeporteConjunto(p[0].trim(), Integer.parseInt(p[1].trim()),
                        Boolean.parseBoolean(p[2].trim())));
            }
            for (String[] p : leer(carpeta + "/categorias.txt")) {
                club.agregarCategoria(new Categoria(p[0].trim(), Integer.parseInt(p[1].trim()),
                        Integer.parseInt(p[2].trim())));
            }
            for (String[] p : leer(carpeta + "/equipos.txt")) {
                DeporteConjunto d = club.buscarDeporte(p[1].trim());
                Categoria c = club.buscarCategoria(p[2].trim());
                if (d == null || c == null) {
                    throw new PersistenciaException("El equipo " + p[0] + " usa un deporte o categoría que no existe.");
                }
                club.agregarEquipo(new Equipo(p[0].trim(), d, c, Integer.parseInt(p[3].trim())));
            }
            for (String[] p : leer(carpeta + "/composiciones.txt")) {
                Equipo e = club.buscarEquipo(p[0].trim());
                Socio s = club.buscarSocio(p[1].trim());
                if (e == null || s == null) {
                    throw new PersistenciaException("Composición con equipo o socio inexistente: " + p[0] + ", " + p[1]);
                }
                e.agregarJugador(s, p[2].trim(), Integer.parseInt(p[3].trim()));
            }
        } catch (RuntimeException ex) {
            throw new PersistenciaException("No se pudo cargar el deporte de conjunto: " + ex.getMessage());
        }
    }

//Toca eliminarla 
    public void guardarDeporteConjunto(Club club, String carpeta) throws IOException, PersistenciaException {
    try (PrintWriter equipos = new PrintWriter(new FileWriter(carpeta + "/equipos.txt"));
            PrintWriter composiciones = new PrintWriter(new FileWriter(carpeta + "/composiciones.txt"))) {
        equipos.println("# nombre;deporte;categoria;cupoMaximo");
        composiciones.println("# equipo;loginSocio;posicion;numeroCamiseta");
        for (Equipo e : club.getEquipos()) {
            equipos.println(e.getNombre() + SEPARADOR + e.getDeporte().getNombre() + SEPARADOR
                    + e.getCategoria().getNombre() + SEPARADOR + e.getCupoMaximo());
            for (ComposicionEquipo c : e.getComposicion()) {
                composiciones.println(e.getNombre() + SEPARADOR + c.getJugador().getLogin() + SEPARADOR
                        + c.getPosicion() + SEPARADOR + c.getNumeroCamiseta());
            }
        }
    }

    private java.util.List<String[]> leer(String ruta) throws IOException {
        java.util.List<String[]> filas = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    filas.add(linea.split(SEPARADOR));
                }
            }
        }
        return filas;
    }
    //cambio
   public void guardarDeporteConjunto(Club club, String carpeta) throws IOException, PersistenciaException {
        guardarEquipos(club, carpeta + "/equipos.txt");
        guardarComposiciones(club, carpeta + "/composiciones.txt");
    }

    private void guardarEquipos(Club club, String ruta) throws PersistenciaException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ruta))) {
            escritor.println("# nombre;deporte;categoria;cupo");
            for (Equipo e : club.getEquipos()) {
                escritor.println(e.getNombre() + SEPARADOR + e.getDeporte().getNombre()
                        + SEPARADOR + e.getCategoria().getNombre() + SEPARADOR + e.getCupoMaximo());
            }
        } catch (IOException ex) {
            throw new PersistenciaException("No se pudo escribir " + ruta + ". Error: " + ex);
        }
    }

    private void guardarComposiciones(Club club, String ruta) throws PersistenciaException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ruta))) {
            escritor.println("# equipo;login;posicion;camiseta");
            for (Equipo e : club.getEquipos()) {
                for (ComposicionEquipo c : e.getComposicion()) {
                    escritor.println(e.getNombre() + SEPARADOR + c.getJugador().getLogin()
                            + SEPARADOR + c.getPosicion() + SEPARADOR + c.getNumeroCamiseta());
                }
            }
        } catch (IOException ex) {
            throw new PersistenciaException("No se pudo escribir " + ruta + ". Error: " + ex);
        }
    }
}
