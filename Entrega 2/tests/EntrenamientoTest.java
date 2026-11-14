package Entrenamiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import DeporteIndividual.Instalacion;
import Excepciones.ClubException;
import Usuarios.Entrenador;
import Usuarios.Socio;

class EntrenamientoTest {

    private static final LocalDate DIA = LocalDate.of(2026, 11, 20);

    private Entrenador coach;
    private Instalacion cancha;
    private Equipo equipo;
    private Socio ana;
    private Socio bruno;

    @BeforeEach
    void preparar() {
        coach = new Entrenador("coach", "1234", "COD-COACH");
        cancha = new Instalacion("Cancha 1", "Cancha de fútbol", 22, false);
        equipo = new Equipo("Cerros FC", DeporteConjunto.futbol(), Categoria.MAYORES, 10);
        ana = new Socio("ana", "1234", 20);
        bruno = new Socio("bruno", "1234", 20);
        ana.inscribirseEnEquipo(equipo, "Delantero", 9);
        bruno.inscribirseEnEquipo(equipo, "Defensa", 4);
    }

    private SesionesEntrenamiento sesion() {
        return coach.programarSesion(DIA, LocalTime.of(10, 0), 60, cancha);
    }

    //HU-10 / RF23: programar sesión 

    @Test
    void sesionDisponibleQuedaProgramadaYOcupaLaInstalacion() {
        SesionesEntrenamiento s = sesion();

        assertEquals(1, coach.getSesiones().size());
        assertEquals(DIA.atTime(11, 0), s.getFin());
        assertFalse(s.disponibleIns(DIA.atTime(10, 30), DIA.atTime(11, 30)));
    }

    @Test
    void sesionEnInstalacionYaOcupadaSeRechaza() {
        sesion();
        Entrenador otro = new Entrenador("otro", "1234", "COD-OTRO");

        assertThrows(ClubException.class,
                () -> otro.programarSesion(DIA, LocalTime.of(10, 30), 60, cancha));
        assertTrue(otro.getSesiones().isEmpty());
    }

    @Test
    void elEntrenadorNoPuedeTenerDosSesionesQueSeCruzan() {
        sesion();
        Instalacion otraCancha = new Instalacion("Cancha 2", "Cancha de fútbol", 22, false);

        assertThrows(ClubException.class,
                () -> coach.programarSesion(DIA, LocalTime.of(10, 30), 60, otraCancha));
        assertEquals(1, coach.getSesiones().size());
    }

    @Test
    void sesionesConsecutivasEnLaMismaInstalacionSePermiten() {
        sesion();

        coach.programarSesion(DIA, LocalTime.of(11, 0), 60, cancha);

        assertEquals(2, coach.getSesiones().size());
    }

    //Convocatoria

    @Test
    void convocarARegistraAlJugador() {
        SesionesEntrenamiento s = sesion();

        s.convocar(ana);

        assertTrue(s.fueConvocado(ana));
        assertFalse(s.fueConvocado(bruno));
    }

    @Test
    void noSeConvocaAQuienNoTieneEquipoNiDosVeces() {
        SesionesEntrenamiento s = sesion();
        Socio sinEquipo = new Socio("diego", "1234", 20);
        s.convocar(ana);

        assertThrows(ClubException.class, () -> s.convocar(sinEquipo));
        assertThrows(ClubException.class, () -> s.convocar(ana));
        assertThrows(ClubException.class, () -> s.convocar(null));
        assertEquals(1, s.getJugConvocados().size());
    }

    //HU-11 / RF24: asistencia 

    @Test
    void asistenciaDeUnConvocadoQuedaRegistrada() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        s.convocar(bruno);

        coach.registrarAsistencia(s, ana, "asistio");
        coach.registrarAsistencia(s, bruno, "FALLO");   // no distingue mayúsculas

        assertEquals("asistio", s.getAsistencia(ana).getEstado());
        assertEquals("fallo", s.getAsistencia(bruno).getEstado());
        assertEquals(2, s.getAsistencias().size());
    }

    @Test
    void asistenciaSinSerConvocadoSeRechaza() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);

        assertThrows(ClubException.class, () -> coach.registrarAsistencia(s, bruno, "asistio"));
        assertTrue(s.getAsistencias().isEmpty());
    }

    @Test
    void estadoInvalidoSeRechaza() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);

        assertThrows(ClubException.class, () -> coach.registrarAsistencia(s, ana, "tarde"));
        assertThrows(ClubException.class, () -> coach.registrarAsistencia(s, ana, null));
        assertTrue(s.getAsistencias().isEmpty());
    }

    @Test
    void registrarDeNuevoCorrigeElEstadoSinDuplicar() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        coach.registrarAsistencia(s, ana, "fallo");

        coach.registrarAsistencia(s, ana, "asistio");

        assertEquals(1, s.getAsistencias().size());
        assertEquals("asistio", s.getAsistencia(ana).getEstado());
    }

    @Test
    void soloElEntrenadorDeLaSesionPuedeRegistrarla() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        Entrenador otro = new Entrenador("otro", "1234", "COD-OTRO");

        assertThrows(ClubException.class, () -> otro.registrarAsistencia(s, ana, "asistio"));
        assertThrows(ClubException.class, () -> coach.registrarAsistencia(null, ana, "asistio"));
    }

    //RF25: evaluación de desempeño 

    @Test
    void evaluacionGuardaNotaYComentario() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        coach.registrarAsistencia(s, ana, "asistio");
        assertFalse(s.getAsistencia(ana).tieneEvaluacion());

        Asistencia a = coach.registrarEvaluacion(s, ana, 8, "  Buen pressing  ");

        assertTrue(a.tieneEvaluacion());
        assertEquals(8, a.getNota());
        assertEquals("Buen pressing", a.getComentario());
    }

    @Test
    void notaEnLosBordesDeCeroADiezEsValida() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        s.convocar(bruno);
        coach.registrarAsistencia(s, ana, "asistio");
        coach.registrarAsistencia(s, bruno, "asistio");

        assertEquals(0, coach.registrarEvaluacion(s, ana, 0, "Muy flojo").getNota());
        assertEquals(10, coach.registrarEvaluacion(s, bruno, 10, "Excelente").getNota());
    }

    @Test
    void notaFueraDeRangoOSinComentarioSeRechaza() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        coach.registrarAsistencia(s, ana, "asistio");

        assertThrows(ClubException.class, () -> coach.registrarEvaluacion(s, ana, 11, "ok"));
        assertThrows(ClubException.class, () -> coach.registrarEvaluacion(s, ana, -1, "ok"));
        assertThrows(ClubException.class, () -> coach.registrarEvaluacion(s, ana, 7, " "));
        assertFalse(s.getAsistencia(ana).tieneEvaluacion());
    }

    @Test
    void noSeEvaluaAQuienFaltoNiAQuienNoTieneAsistencia() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        s.convocar(bruno);
        coach.registrarAsistencia(s, bruno, "fallo");

        assertThrows(ClubException.class, () -> coach.registrarEvaluacion(s, bruno, 5, "ok"));  // faltó
        assertThrows(ClubException.class, () -> coach.registrarEvaluacion(s, ana, 5, "ok"));    // sin asistencia
    }

    // HU-10, HU-11, RF23 a RF25

    @Test
    void flujoCompletoProgramarConvocarAsistirYEvaluar() {
        SesionesEntrenamiento s = sesion();
        s.convocar(ana);
        s.convocar(bruno);

        coach.registrarAsistencia(s, ana, "asistio");
        coach.registrarAsistencia(s, bruno, "fallo");
        coach.registrarEvaluacion(s, ana, 9, "Gran sesión");

        assertEquals(2, s.getJugConvocados().size());
        assertNotNull(s.getAsistencia(ana));
        assertEquals(9, s.getAsistencia(ana).getNota());
        assertFalse(s.getAsistencia(bruno).tieneEvaluacion());
    }
}