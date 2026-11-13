package persistencias;

import club.Club;
import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.Instalacion;
import Excepciones.PersistenciaException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Lee disciplinas.txt (nombre;maxInstalacionesSimultaneas) e
 * instalaciones.txt (nombre;tipo;capacidad;techada;disciplina o -).
 */
public class PersistenciaDeporteIndividual {

    private static final String SEPARADOR = ";";

    public void cargarInstalaciones(Club club, String carpeta) throws IOException, PersistenciaException {
        cargarDisciplinas(club, carpeta + "/disciplinas.txt");
        cargarInstalacionesArchivo(club, carpeta + "/instalaciones.txt");
    }

    private void cargarDisciplinas(Club club, String ruta) throws IOException, PersistenciaException {
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] p = linea.split(SEPARADOR);
                try {
                    club.agregarDisciplina(new DisciplinaIndividual(p[0].trim(),
                            Integer.parseInt(p[1].trim())));
                } catch (RuntimeException ex) {
                    throw new PersistenciaException("Disciplina inválida en \"" + linea + "\": " + ex.getMessage());
                }
            }
        }
    }

    private void cargarInstalacionesArchivo(Club club, String ruta) throws IOException, PersistenciaException {
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] p = linea.split(SEPARADOR);
                try {
                    Instalacion instalacion = new Instalacion(p[0].trim(), p[1].trim(),
                            Integer.parseInt(p[2].trim()), Boolean.parseBoolean(p[3].trim()));
                    club.agregarInstalacion(instalacion);

                    if (p.length > 4 && !p[4].trim().equals("-")) {
                        DisciplinaIndividual d = club.buscarDisciplina(p[4].trim());
                        if (d == null) {
                            throw new PersistenciaException("La instalación " + p[0].trim()
                                    + " usa una disciplina que no existe: " + p[4].trim());
                        }
                        d.agregarInstalacion(instalacion);
                    }
                } catch (PersistenciaException ex) {
                    throw ex;
                } catch (RuntimeException ex) {
                    throw new PersistenciaException("Instalación inválida en \"" + linea + "\": " + ex.getMessage());
                }
            }
        }
    }
}