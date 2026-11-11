package persistencias;

import club.Club;
import DeporteIndividual.Instalacion;
import Excepciones.PersistenciaException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Lee instalaciones.txt. Formato: nombre;tipo;capacidad;techada;disciplina(o -) */
public class PersistenciaDeporteIndividual {

    private static final String ARCHIVO = "instalaciones.txt";
    private static final String SEPARADOR = ";";

    public void cargarInstalaciones(Club club, String carpeta) throws IOException, PersistenciaException {
        String ruta = carpeta + "/" + ARCHIVO;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] p = linea.split(SEPARADOR);
                try {
                    club.agregarInstalacion(new Instalacion(p[0].trim(), p[1].trim(),
                            Integer.parseInt(p[2].trim()), Boolean.parseBoolean(p[3].trim())));
                } catch (RuntimeException ex) {
                    throw new PersistenciaException("Instalación inválida en \"" + linea + "\": " + ex.getMessage());
                }
            }
        }
    }
}
