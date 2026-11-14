package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.InscripcionIndividual;
import DeporteIndividual.Instalacion;
import DeporteIndividual.PartidoPractica;
import DeporteIndividual.PuntajeRanking;
import DeporteIndividual.Reserva;
import Excepciones.ClubException;
import Usuarios.Socio;

class DeporteIndividualTest {

    private Instalacion t1;
    private Instalacion t2;
    private Instalacion p1;
    private DisciplinaIndividual tenis;   
    private DisciplinaIndividual padel;
    private Socio ana;     
    private Socio bruno;   
    private Socio carla;   

    @BeforeEach
    void preparar() {
        t1 = new Instalacion("Cancha Tenis 1", "Cancha de tenis", 4, false);
        t2 = new Instalacion("Cancha Tenis 2", "Cancha de tenis", 4, false);
        p1 = new Instalacion("Cancha Padel 1", "Cancha de pádel", 4, false);
        tenis = new DisciplinaIndividual("Tenis", 1);
        tenis.agregarInstalacion(t1);
        tenis.agregarInstalacion(t2);
        padel = new DisciplinaIndividual("Pádel", 2);
        padel.agregarInstalacion(p1);

        ana = new Socio("ana", "1234", 20);
        bruno = new Socio("bruno", "1234", 21);
        carla = new Socio("carla", "1234", 22);
        ana.inscribirseEnDisciplina(tenis, "Principiante");
        ana.inscribirseEnDisciplina(padel, "Principiante");
        bruno.inscribirseEnDisciplina(tenis, "Principiante");
        carla.inscribirseEnDisciplina(tenis, "Avanzado");
    }

    private LocalDateTime hora(int hora, int minuto) {
        return LocalDateTime.of(2026, 11, 20, hora, minuto);
    }


    //Instalacion y Reserva

    @Test
    void modalidadesSegunJugadores() {
        assertTrue(t1.aceptaModalidad("SENCILLOS", 2));
        assertFalse(t1.aceptaModalidad("SENCILLOS", 3));
        assertTrue(t1.aceptaModalidad("DOBLES", 4));
        assertFalse(t1.aceptaModalidad("DOBLES", 5));
        assertFalse(t1.aceptaModalidad("TRIPLES", 2));
        Instalacion mesa = new Instalacion("Mesa 1", "Mesa de tenis de mesa", 2, true);
        assertFalse(mesa.aceptaModalidad("DOBLES", 4));
    }

    @Test
    void bebidaCalienteSoloSeRestringeEnPadelYMesaTechada() {
        assertTrue(p1.restringeBebidaCaliente());
        assertTrue(new Instalacion("M1", "Mesa de tenis de mesa", 2, true).restringeBebidaCaliente());
        assertFalse(new Instalacion("M2", "Mesa de tenis de mesa", 2, false).restringeBebidaCaliente());
        assertFalse(t1.restringeBebidaCaliente());
    }

    @Test
    void reservaOcupaSoloSuHorarioYNoAdmiteCruces() {
        t1.agregarReserva(new Reserva(hora(10, 0), hora(11, 0), 2, "SENCILLOS", ana, t1));

        assertTrue(t1.estaOcupada(hora(10, 30), hora(11, 30)));
        assertFalse(t1.estaOcupada(hora(11, 0), hora(12, 0)));
        Reserva cruzada = new Reserva(hora(10, 30), hora(11, 30), 2, "SENCILLOS", bruno, t1);
        assertThrows(ClubException.class, () -> t1.agregarReserva(cruzada));
    }

    @Test
    void reservaConDatosInvalidosSeRechaza() {
        assertThrows(ClubException.class, () -> new Reserva(hora(11, 0), hora(10, 0), 2, "SENCILLOS", ana, t1));
        assertThrows(ClubException.class, () -> new Reserva(hora(10, 0), hora(11, 0), 0, "SENCILLOS", ana, t1));
        assertThrows(ClubException.class, () -> new Reserva(hora(10, 0), hora(11, 0), 2, " ", ana, t1));
        assertThrows(ClubException.class, () -> new Reserva(hora(10, 0), hora(11, 0), 2, "SENCILLOS", null, t1));
    }


    // DisciplinaIndividual
    @Test
    void maximoDeInstalacionesSimultaneas() {
        t1.agregarReserva(new Reserva(hora(10, 0), hora(11, 0), 2, "SENCILLOS", ana, t1));

        assertTrue(tenis.alcanzoMaximoSimultaneas(hora(10, 0), hora(11, 0)));
        assertFalse(tenis.alcanzoMaximoSimultaneas(hora(11, 0), hora(12, 0)));

        DisciplinaIndividual conDosPermitidas = new DisciplinaIndividual("Tenis B", 2);
        conDosPermitidas.agregarInstalacion(t1);
        conDosPermitidas.agregarInstalacion(t2);
        assertFalse(conDosPermitidas.alcanzoMaximoSimultaneas(hora(10, 0), hora(11, 0)));
    }

    @Test
    void disciplinaNoAceptaInstalacionesNulasNiRepetidas() {
        assertThrows(ClubException.class, () -> tenis.agregarInstalacion(null));
        assertThrows(ClubException.class, () -> tenis.agregarInstalacion(t1));
        assertThrows(ClubException.class, () -> new DisciplinaIndividual("Tenis", 0));
    }

    
    //InscripcionIndividual, PuntajeRanking y PartidoPractica
   
    @Test
    void nivelInvalidoSeRechazaYSeNormalizaAMayusculas() {
        assertThrows(ClubException.class, () -> new InscripcionIndividual(ana, tenis, "Experto"));
        assertEquals("INTERMEDIO", new InscripcionIndividual(ana, tenis, "intermedio").getNivel());
    }

    @Test
    void compatiblesSiSonDelMismoNivelOContiguos() {
        InscripcionIndividual principiante = ana.getInscripcion(tenis);
        InscripcionIndividual otroPrincipiante = bruno.getInscripcion(tenis);
        InscripcionIndividual avanzado = carla.getInscripcion(tenis);
        InscripcionIndividual intermedio = new InscripcionIndividual(new Socio("d", "1234", 20), tenis, "Intermedio");

        assertTrue(principiante.esNivelCompatible(otroPrincipiante));
        assertTrue(principiante.esNivelCompatible(intermedio));
        assertTrue(intermedio.esNivelCompatible(avanzado));
        assertFalse(principiante.esNivelCompatible(avanzado));
    }

    @Test
    void nivelCambiaSegunElElo() {
        InscripcionIndividual ins = ana.getInscripcion(tenis);

        ins.actualizarNivelPorElo(1099);
        assertEquals("PRINCIPIANTE", ins.getNivel());
        ins.actualizarNivelPorElo(1100);
        assertEquals("INTERMEDIO", ins.getNivel());
        ins.actualizarNivelPorElo(1400);
        assertEquals("AVANZADO", ins.getNivel());
    }

    @Test
    void eloSumaRestaYNuncaBajaDeCero() {
        PuntajeRanking ranking = ana.obtenerRanking(tenis);
        assertEquals(1000, ranking.getElo());

        ranking.actualizar(true);
        assertEquals(1015, ranking.getElo());
        ranking.actualizar(false);
        ranking.actualizar(false);
        assertEquals(985, ranking.getElo());

        PuntajeRanking casiCero = new PuntajeRanking(ana, tenis, 10);
        casiCero.actualizar(false);
        assertEquals(0, casiCero.getElo());
    }

    @Test
    void partidoPracticaValidaInscripcionNivelesYRival() {
        LocalDate fecha = LocalDate.of(2026, 11, 20);
        Socio diego = new Socio("diego", "1234", 20);   

        assertTrue(new PartidoPractica(tenis, ana, bruno, fecha).puedeJugarse());
        assertThrows(ClubException.class, () -> new PartidoPractica(tenis, ana, carla, fecha));
        assertThrows(ClubException.class, () -> new PartidoPractica(tenis, ana, ana, fecha));
        assertThrows(ClubException.class, () -> new PartidoPractica(tenis, ana, diego, fecha));
    }

    @Test
    void resultadoDelPartidoActualizaElRankingUnaSolaVez() {
        PartidoPractica partido = new PartidoPractica(tenis, ana, bruno, LocalDate.of(2026, 11, 20));

        assertThrows(ClubException.class, () -> partido.actualizarRanking(carla)); 
        partido.actualizarRanking(ana);

        assertEquals(1015, ana.obtenerRanking(tenis).getElo());
        assertEquals(985, bruno.obtenerRanking(tenis).getElo());
        assertThrows(ClubException.class, () -> partido.actualizarRanking(bruno));
    }

    // RF01: inscribirse a una disciplina individual con un nivel
    @Test
    void inscripcionGuardaNivelYRankingInicial() {
        assertTrue(tenis.estaInscrito(ana));
        assertEquals("PRINCIPIANTE", ana.getInscripcion(tenis).getNivel());
        assertEquals(1000, ana.obtenerRanking(tenis).getElo());
        assertEquals(1500, carla.obtenerRanking(tenis).getElo());
    }

    @Test
    void inscripcionNoSePuedeHacerDosVecesNiConNivelInvalido() {
        assertThrows(ClubException.class, () -> ana.inscribirseEnDisciplina(tenis, "Intermedio"));
        assertThrows(ClubException.class, () -> bruno.inscribirseEnDisciplina(padel, "Experto"));
        assertFalse(padel.estaInscrito(bruno));
    }

  
    // RF05 / HU-06: reservar una instalación individual
    @Test
    void reservaAprobadaMarcaLaInstalacionOcupada() {
        ana.reservarInstalacion(t1, hora(10, 0), hora(11, 0), 2, "SENCILLOS");

        assertEquals(1, ana.getReservas().size());
        assertTrue(t1.estaOcupada(hora(10, 0), hora(11, 0)));
    }

    @Test
    void seRechazaSiLaInstalacionYaEstaReservada() {
        ana.reservarInstalacion(t1, hora(10, 0), hora(11, 0), 2, "SENCILLOS");

        assertThrows(ClubException.class,
                () -> bruno.reservarInstalacion(t1, hora(10, 30), hora(11, 30), 2, "SENCILLOS"));
        assertTrue(bruno.getReservas().isEmpty());
    }

    @Test
    void seRechazaSiSeAlcanzoElMaximoSimultaneo() {
        ana.reservarInstalacion(t1, hora(10, 0), hora(11, 0), 2, "SENCILLOS");

        assertThrows(ClubException.class,
                () -> bruno.reservarInstalacion(t2, hora(10, 0), hora(11, 0), 2, "SENCILLOS"));

 
        bruno.reservarInstalacion(t2, hora(11, 0), hora(12, 0), 2, "SENCILLOS");
        assertEquals(1, bruno.getReservas().size());
    }

    @Test
    void hu06_sinInscripcionHorarioOModalidadInvalidosSeRechaza() {
        assertThrows(ClubException.class,
                () -> bruno.reservarInstalacion(p1, hora(10, 0), hora(11, 0), 2, "SENCILLOS")); 
        assertThrows(ClubException.class,
                () -> ana.reservarInstalacion(t1, hora(11, 0), hora(10, 0), 2, "SENCILLOS"));   
        assertThrows(ClubException.class,
                () -> ana.reservarInstalacion(t1, hora(10, 0), hora(11, 0), 3, "SENCILLOS"));  
        assertTrue(ana.getReservas().isEmpty());
    }

    @Test
    void hu06_unSocioNoPuedeTenerDosReservasQueSeCruzan() {
        ana.reservarInstalacion(t1, hora(10, 0), hora(11, 0), 2, "SENCILLOS");

        assertThrows(ClubException.class,
                () -> ana.reservarInstalacion(p1, hora(10, 30), hora(11, 30), 2, "SENCILLOS"));
        assertEquals(1, ana.getReservas().size());
    }

    // Restricción de bebida caliente (compra seguida de reserva)
    @Test
    void bebidaCaliente_padelRechazadoTrasUnaCompraReciente() {
        ana.registrarBebidaCaliente(hora(9, 50));

        assertThrows(ClubException.class,
                () -> ana.reservarInstalacion(p1, hora(10, 0), hora(11, 0), 2, "SENCILLOS"));
        assertTrue(ana.getReservas().isEmpty());
    }

    @Test
    void bebidaCaliente_tenisPermitidoYFueraDeLaVentanaTambien() {
        ana.registrarBebidaCaliente(hora(9, 50));
        ana.reservarInstalacion(t1, hora(10, 0), hora(11, 0), 2, "SENCILLOS");
        assertEquals(1, ana.getReservas().size());

        ana.registrarBebidaCaliente(hora(11, 0));   
        ana.reservarInstalacion(p1, hora(12, 0), hora(13, 0), 2, "SENCILLOS");
        assertEquals(2, ana.getReservas().size());
    }

    @Test
    void bebidaCaliente_estarJugandoOAPuntoDeEmpezarEnPadelRestringe() {
        ana.reservarInstalacion(p1, hora(10, 0), hora(11, 0), 2, "SENCILLOS");

        assertTrue(ana.estaEnInstalacionRestringida(hora(10, 30)));
        assertTrue(ana.estaEnInstalacionRestringida(hora(9, 45)));
        assertFalse(ana.estaEnInstalacionRestringida(hora(9, 0)));
        assertFalse(ana.estaEnInstalacionRestringida(hora(11, 0)));
    }


    // RF04: programar un partido de práctica
    @Test
    void practicaValidaReservaLaCanchaYQuedaRegistrada() {
        ana.programarPartidoPractica(bruno, tenis, t1, hora(10, 0), hora(11, 0));

        assertEquals(1, ana.getPartidosPractica().size());
        assertEquals(1, ana.getReservas().size());
        assertTrue(t1.estaOcupada(hora(10, 0), hora(11, 0)));
    }

    @Test
    void nivelesNoCompatiblesSeRechazaYNoQuedaReserva() {
        assertThrows(ClubException.class,
                () -> ana.programarPartidoPractica(carla, tenis, t1, hora(10, 0), hora(11, 0)));

        assertTrue(ana.getReservas().isEmpty());
        assertFalse(t1.estaOcupada(hora(10, 0), hora(11, 0)));
    }

    @Test
    void elRivalYQuienProgramaDebenEstarInscritos() {
        Socio diego = new Socio("diego", "1234", 20);

        assertThrows(ClubException.class,
                () -> ana.programarPartidoPractica(diego, tenis, t1, hora(10, 0), hora(11, 0)));
        assertThrows(ClubException.class,
                () -> bruno.programarPartidoPractica(ana, padel, p1, hora(10, 0), hora(11, 0)));
    }

    @Test
    void elResultadoActualizaElRankingDeAmbos() {
        PartidoPractica partido = ana.programarPartidoPractica(bruno, tenis, t1, hora(10, 0), hora(11, 0));
        partido.actualizarRanking(bruno);

        assertEquals(1015, bruno.obtenerRanking(tenis).getElo());
        assertEquals(985, ana.obtenerRanking(tenis).getElo());
    }
}