package persistencias;

import club.Club;
import Excepciones.PersistenciaException;
import Usuarios.Empleado;
import Usuarios.Turno;
import Usuarios.Usuario;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalTime;

/** Lee y guarda turnos.txt. Formato: loginEmpleado;DIA(LUNES..DOMINGO);HH:mm */
public class PersistenciaTurnos {

    private static final String ARCHIVO = "turnos.txt";
    private static final String SEPARADOR = ";";
    private static final String[] DIAS = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

    public void cargarTurnos(Club club, String carpeta) throws IOException, PersistenciaException {
        String ruta = carpeta + "/" + ARCHIVO;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split(SEPARADOR);
                Usuario u = club.buscarUsuario(partes[0].trim());
                if (!(u instanceof Empleado)) {
                    throw new PersistenciaException("Turno de un empleado que no existe: " + partes[0]);
                }
                try {
                    Empleado e = (Empleado) u;
                    e.agregarTurno(new Turno(e, aDia(partes[1]), LocalTime.parse(partes[2].trim())));
                } catch (RuntimeException ex) {
                    throw new PersistenciaException("Turno inválido en la línea \"" + linea + "\": " + ex.getMessage());
                }
            }
        }
    }

    public void guardarTurnos(Club club, String carpeta) throws IOException, PersistenciaException {
        String ruta = carpeta + "/" + ARCHIVO;
        try (PrintWriter escritor = new PrintWriter(new FileWriter(ruta))) {
            escritor.println("# loginEmpleado;dia(LUNES..DOMINGO);hora(HH:mm)");
            for (Empleado e : club.getEmpleados()) {
                for (Turno t : e.getTurnos()) {
                    escritor.println(e.getLogin() + SEPARADOR + DIAS[t.getDia().getValue() - 1]
                            + SEPARADOR + t.getHora());
                }
            }
        }
    }

    private DayOfWeek aDia(String texto) throws PersistenciaException {
        String t = texto.trim().toUpperCase().replace("É", "E").replace("Á", "A");
        for (int i = 0; i < DIAS.length; i++) {
            if (DIAS[i].equals(t)) {
                return DayOfWeek.of(i + 1);
            }
        }
        throw new PersistenciaException("Día desconocido: " + texto);
    }
}
