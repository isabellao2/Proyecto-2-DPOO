package persistencias;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
 
import DeporteConjunto.Equipo;
import Excepciones.PersistenciaException;
import Torneos.EstadoTorneo;
import Torneos.InscripcionTorneo;
import Torneos.Modalidad;
import Torneos.TipoCupo;
import Torneos.TipoTorneo;
import Torneos.Torneo;
import Usuarios.Usuario;
import club.Club;

public class PersistenciaTorneos {
 
    private static final String ARCHIVO_TORNEOS = "torneos.txt";
    private static final String ARCHIVO_INSCRIPCIONES = "inscripciones_torneo.txt";
    private static final String SEPARADOR = ";";
    private static final String VACIO = "-";
 
    public void cargarTorneos(Club club, String carpeta) throws IOException, PersistenciaException {
        File torneos = new File(carpeta + "/" + ARCHIVO_TORNEOS);
        if (!torneos.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(torneos))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] p = linea.split(SEPARADOR);
                try {
                       double valorBono = 0;
                    if (p.length > 9) {
                        valorBono = Double.parseDouble(p[9].trim());
                    }
                    Torneo t = new Torneo(p[0], TipoTorneo.valueOf(p[1].trim().toLowerCase()), Modalidad.valueOf(p[2].trim()),
                            p[3], p[4], Integer.parseInt(p[5].trim()), Double.parseDouble(p[6].trim()),
                            LocalDate.parse(p[7].trim()), valorBono);
                    t.restaurarEstado(EstadoTorneo.valueOf(p[8].trim()));
                    club.agregarTorneo(t);
                } catch (RuntimeException e) {
                    throw new PersistenciaException("Torneo inválido en la línea \"" + linea + "\": " + e.getMessage());
                }
            }
        }
        cargarInscripciones(club, carpeta);
    }
 
    private void cargarInscripciones(Club club, String carpeta) throws IOException, PersistenciaException {
        File archivo = new File(carpeta + "/" + ARCHIVO_INSCRIPCIONES);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] p = linea.split(SEPARADOR);
                Torneo torneo = club.buscarTorneo(p[0].trim());
                if (torneo == null) {
                    throw new PersistenciaException("Inscripción de un torneo que no existe: " + p[0]);
                }
                try {
                    Usuario participante = VACIO.equals(p[1].trim()) ? null : club.buscarUsuario(p[1].trim());
                    Usuario pareja = VACIO.equals(p[2].trim()) ? null : club.buscarUsuario(p[2].trim());
                    Equipo equipo = VACIO.equals(p[3].trim()) ? null : club.buscarEquipo(p[3].trim());
                    if (participante == null && equipo == null) {
                        throw new PersistenciaException("Inscripción sin jugador ni equipo existente: " + linea);
                    }
                    torneo.restaurarInscripcion(new InscripcionTorneo(participante, pareja, equipo,
                            TipoCupo.valueOf(p[4].trim().toLowerCase()), Double.parseDouble(p[5].trim())));
                } catch (IllegalArgumentException e) {
                    throw new PersistenciaException("Inscripción inválida en la línea \"" + linea + "\": " + e.getMessage());
                }
            }
        }
    }
 
    public void guardarTorneos(Club club, String carpeta) throws IOException, PersistenciaException {
        try (PrintWriter torneos = new PrintWriter(new FileWriter(carpeta + "/" + ARCHIVO_TORNEOS));
                PrintWriter inscripciones = new PrintWriter(new FileWriter(carpeta + "/" + ARCHIVO_INSCRIPCIONES))) {
            torneos.println("# nombre;tipo;modalidad;deporte;categoria;cupos;tarifa;fecha;estado;valorBono");
            inscripciones.println("# torneo;participante;pareja;equipo;tipoCupo;tarifaPagada ( - si no aplica)");
            for (Torneo t : club.getTorneos()) {
                torneos.println(t.getNombre() + SEPARADOR + t.getTipo() + SEPARADOR + t.getModalidad() + SEPARADOR
                        + t.getDeporte() + SEPARADOR + t.getCategoria() + SEPARADOR + t.getCupos() + SEPARADOR
                        + t.getTarifa() + SEPARADOR + t.getFecha() + SEPARADOR + t.getEstado() + SEPARADOR + t.getValorBono());
                for (InscripcionTorneo i : t.getInscripciones()) {
                    inscripciones.println(t.getNombre() + SEPARADOR
                            + (i.getParticipante() == null ? VACIO : i.getParticipante().getLogin()) + SEPARADOR
                            + (i.getCompañero() == null ? VACIO : i.getCompañero().getLogin()) + SEPARADOR
                            + (i.getEquipo() == null ? VACIO : i.getEquipo().getNombre()) + SEPARADOR
                            + i.getTipoCupo() + SEPARADOR + i.getTarifaPagada());
                }
            }
        }
    }
}
