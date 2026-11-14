package DeporteConjunto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Excepciones.ClubException;
import Partidos.EstadisticaFutbol;
import Partidos.PartidoInterno;
import Usuarios.Socio;

class DeporteConjuntoTest {

    private DeporteConjunto futbol;
    private DeporteConjunto baloncesto;
    private Equipo equipo;

    @BeforeEach
    void preparar() {
    futbol = DeporteConjunto.futbol();
    equipo = new Equipo("Cerros FC", futbol, Categoria.MAYORES, 10);
}

    private void llenar(Equipo e, int cantidad) {
    for (int i = 1; i <= cantidad; i++) {
        new Socio("j" + i, "1234", 20).inscribirseEnEquipo(e, "Jugador", i);
        }
    }

    // Categoria

    @Test
    void categoriaCumpleEdadEnLosBordes() {
        assertTrue(Categoria.SUB_13.cumpleEdad(12));
        assertFalse(Categoria.SUB_13.cumpleEdad(13));
        assertTrue(Categoria.SUB_15.cumpleEdad(13));
        assertTrue(Categoria.SUB_15.cumpleEdad(14));
        assertFalse(Categoria.SUB_15.cumpleEdad(15));
        assertTrue(Categoria.SUB_17.cumpleEdad(15));
        assertTrue(Categoria.SUB_17.cumpleEdad(16));
        assertFalse(Categoria.SUB_17.cumpleEdad(17));
        assertTrue(Categoria.MAYORES.cumpleEdad(17));
    }

    @Test
    void categoriaConDatosInvalidosSeRechaza() {
        assertThrows(ClubException.class, () -> new Categoria(" ", 0, 10));
        assertThrows(ClubException.class, () -> new Categoria("Rara", -1, 10));
        assertThrows(ClubException.class, () -> new Categoria("Rara", 10, 9));
    }

    //DeporteConjunto

    @Test
    void deportesPredefinidosTienenSusReglas() {
        assertEquals(7, futbol.getNumeroMinimoJugadores());
        assertTrue(futbol.permiteEmpate());
        assertEquals(5, baloncesto.getNumeroMinimoJugadores());
        assertFalse(baloncesto.permiteEmpate());
        assertEquals(6, DeporteConjunto.voleibol().getNumeroMinimoJugadores());
        assertFalse(DeporteConjunto.voleibol().permiteEmpate());
    }

    @Test
    void cumpleMinimoEnElBorde() {
        assertFalse(futbol.cumpleMinimo(6));
        assertTrue(futbol.cumpleMinimo(7));
    }

    @Test
    void esFutbolIgnoraTildesYMayusculas() {
        assertTrue(futbol.esFutbol());
        assertTrue(new DeporteConjunto("FUTBOL", 7, true).esFutbol());
        assertFalse(baloncesto.esFutbol());
    }

    @Test
    void deporteEsIgualPorNombreSinImportarMayusculas() {
        assertEquals(new DeporteConjunto("fútbol", 7, true), futbol);
    }

    @Test
    void deporteConDatosInvalidosSeRechaza() {
        assertThrows(ClubException.class, () -> new DeporteConjunto("", 7, true));
        assertThrows(ClubException.class, () -> new DeporteConjunto("Rugby", 0, true));
    }

    // Equipo

    @Test
    void equipoConDatosInvalidosSeRechaza() {
        assertThrows(ClubException.class, () -> new Equipo(" ", futbol, Categoria.MAYORES, 10));
        assertThrows(ClubException.class, () -> new Equipo("X", null, Categoria.MAYORES, 10));
        assertThrows(ClubException.class, () -> new Equipo("X", futbol, null, 10));
        // el cupo no puede ser menor al mínimo del deporte
        assertThrows(ClubException.class, () -> new Equipo("X", futbol, Categoria.MAYORES, 6));
    }

    //Equipo: inscripción de jugadores

    @Test
    void agregarJugadorRegistraLaComposicionEnAmbosLados() {
        Socio ana = socio("ana", 20);

        ComposicionEquipo c = equipo.agregarJugador(ana, "portero", 1);

        assertEquals("portero", c.getPosicion());
        assertEquals(1, c.getNumeroCamiseta());
        assertTrue(equipo.tieneJugador(ana));
        assertTrue(ana.perteneceAEquipoDe(futbol));
        assertEquals(1, ana.getComposicion().size());
    }

    @Test
    void rechazaSiYaEstaEnElMismoEquipo() {
        Socio ana = socio("ana", 20);
        equipo.agregarJugador(ana, "portero", 1);

        assertThrows(ClubException.class, () -> equipo.agregarJugador(ana, "defensa", 2));
        assertEquals(1, equipo.getJugadores().size());
    }

    @Test
    void rechazaSiYaEstaEnOtroEquipoDelMismoDeporte() {
        Equipo otro = new Equipo("Andes FC", futbol, Categoria.MAYORES, 10);
        Socio ana = socio("ana", 20);
        equipo.agregarJugador(ana, "portero", 1);

        assertThrows(ClubException.class, () -> otro.agregarJugador(ana, "portero", 1));
        assertFalse(otro.tieneJugador(ana));
    }

    @Test
    void permiteElMismoSocioEnEquiposDeDeportesDistintos() {
        Equipo basket = new Equipo("Cerros Basket", baloncesto, Categoria.MAYORES, 5);
        Socio ana = socio("ana", 20);

        equipo.agregarJugador(ana, "portero", 1);
        basket.agregarJugador(ana, "base", 1);

        assertEquals(2, ana.getComposicion().size());
    }

    @Test
    void rechazaSiLaEdadNoCorrespondeALaCategoria() {
        Equipo sub17 = new Equipo("Sub17 FC", futbol, Categoria.SUB_17, 10);

        sub17.agregarJugador(socio("de16", 16), "delantero", 9);
        assertThrows(ClubException.class, () -> sub17.agregarJugador(socio("de17", 17), "delantero", 10));
        assertThrows(ClubException.class, () -> sub17.agregarJugador(socio("de14", 14), "delantero", 11));
        assertEquals(1, sub17.getJugadores().size());
    }

    @Test
    void rechazaSiElEquipoEstaLleno() {
        Equipo chico = new Equipo("Chico FC", futbol, Categoria.MAYORES, 7);
        plantel(chico, 7);

        assertFalse(chico.hayCupo());
        assertThrows(ClubException.class, () -> chico.agregarJugador(socio("extra", 20), "defensa", 8));
    }

    @Test
    void rechazaCamisetaRepetida() {
        equipo.agregarJugador(socio("ana", 20), "portero", 1);

        assertThrows(ClubException.class, () -> equipo.agregarJugador(socio("bruno", 20), "defensa", 1));
    }

    @Test
    void rechazaSocioNuloYPosicionVacia() {
        assertThrows(ClubException.class, () -> equipo.agregarJugador(null, "portero", 1));
        Socio ana = socio("ana", 20);
        assertThrows(ClubException.class, () -> equipo.agregarJugador(ana, " ", 1));
        assertFalse(equipo.tieneJugador(ana));
        assertTrue(ana.getComposicion().isEmpty());
    }

    // Equipo

    @Test
    void retirarJugadorLiberaElCupoYLaCamiseta() {
        Equipo chico = new Equipo("Chico FC", futbol, Categoria.MAYORES, 7);
        List<Socio> js = plantel(chico, 7);

        chico.retirarJugador(js.get(2));  

        assertTrue(chico.hayCupo());
        assertTrue(chico.camisetaDisponible(3));
        assertTrue(js.get(2).getComposicion().isEmpty());
        chico.agregarJugador(socio("nuevo", 20), "defensa", 3);
        assertEquals(7, chico.getJugadores().size());
    }

    @Test
    void retirarAQuienNoEstaSeRechaza() {
        assertThrows(ClubException.class, () -> equipo.retirarJugador(socio("fantasma", 20)));
        assertThrows(ClubException.class, () -> equipo.retirarJugador(null));
    }

    // Mínimo de jugadores habilitados

    @Test
    void elMinimoDeFutbolSeCumpleConSieteHabilitados() {
        plantel(equipo, 6);
        assertFalse(equipo.cumpleMinimoJugadores());

        equipo.agregarJugador(socio("j7", 20), "portero", 7);
        assertTrue(equipo.cumpleMinimoJugadores());
    }

    @Test
    void unJugadorNoAptoFisicamenteNoCuentaParaElMinimo() {
        List<Socio> js = plantel(equipo, 7);
        assertTrue(equipo.cumpleMinimoJugadores());

        js.get(0).setFisicoApto(false);

        assertEquals(6, equipo.contarHabilitados());
        assertFalse(equipo.cumpleMinimoJugadores());
        assertEquals(1, equipo.listanohabilitados().size());
    }

    //Tarjetas y suspensión

    @Test
    void dosAmarillasEnElPartidoAnteriorSuspenden() {
        List<Socio> js = plantel(equipo, 7);
        PartidoInterno p = jugarPartido(equipo, js);

        tarjetas(p, js.get(0), 2, false);

        ComposicionEquipo c = js.get(0).getComposicion(futbol);
        assertTrue(c.estaSuspendido());
        assertFalse(js.get(0).estaHabilitadoParaJugar(futbol));
        assertTrue(c.consultarEstado().contains("suspendido"));
        assertEquals(6, equipo.contarHabilitados());
    }

    @Test
    void unaRojaSuspende() {
        List<Socio> js = plantel(equipo, 7);
        PartidoInterno p = jugarPartido(equipo, js);

        tarjetas(p, js.get(1), 0, true);

        assertTrue(js.get(1).getComposicion(futbol).estaSuspendido());
        assertEquals(1, js.get(1).getComposicion(futbol).contarRojas());
    }

    @Test
    void unaSolaAmarillaNoSuspende() {
        List<Socio> js = plantel(equipo, 7);
        PartidoInterno p = jugarPartido(equipo, js);

        tarjetas(p, js.get(2), 1, false);

        assertFalse(js.get(2).getComposicion(futbol).estaSuspendido());
        assertTrue(js.get(2).estaHabilitadoParaJugar(futbol));
        assertEquals(7, equipo.contarHabilitados());
    }

    @Test
    void laSuspensionSoloAplicaAlSiguientePartido() {
        List<Socio> js = plantel(equipo, 7);
        PartidoInterno primero = jugarPartido(equipo, js);
        tarjetas(primero, js.get(0), 0, true);
        assertTrue(js.get(0).getComposicion(futbol).estaSuspendido());

        jugarPartido(equipo, js);   // el siguiente partido, sin tarjetas

        assertFalse(js.get(0).getComposicion(futbol).estaSuspendido());
    }

    @Test
    void sinPartidosJugadosNadieEstaSuspendido() {
        List<Socio> js = plantel(equipo, 7);

        assertNull(equipo.getUltimoPartidoJugado());
        assertFalse(js.get(0).getComposicion(futbol).estaSuspendido());
    }

    @Test
    void lasAmarillasSeAcumulanEnLaConsulta() {
        List<Socio> js = plantel(equipo, 7);
        tarjetas(jugarPartido(equipo, js), js.get(0), 1, false);
        tarjetas(jugarPartido(equipo, js), js.get(0), 1, false);

        assertEquals(2, js.get(0).getComposicion(futbol).contarAmarillas());
    }

    @Test
    void estadoMuestraInhabilitadoSiNoEstaAptoFisicamente() {
        Socio ana = socio("ana", 20);
        equipo.agregarJugador(ana, "portero", 1);
        ana.setFisicoApto(false);

        assertTrue(ana.getComposicion(futbol).consultarEstado().contains("no apto"));
        assertFalse(ana.estaHabilitadoParaJugar(futbol));
    }

    @Test
    void unEquipoConUnSuspendidoYSoloSieteJugadoresNoPuedeJugar() {
        List<Socio> js = plantel(equipo, 7);
        PartidoInterno p = jugarPartido(equipo, js);
        tarjetas(p, js.get(0), 0, true);

        assertFalse(equipo.cumpleMinimoJugadores());
        assertFalse(p.puedeJugarse(equipo));
    }

    //Integración: HU-04 / RF02 (inscribirse a un equipo)

    @Test
    void hu04_socioSeInscribeConPosicionYCamiseta() {
        Socio ana = socio("ana", 20);

        ComposicionEquipo c = ana.inscribirseEnEquipo(equipo, "delantero", 9);

        assertTrue(equipo.tieneJugador(ana));
        assertEquals("delantero", c.getPosicion());
        assertEquals(9, c.getNumeroCamiseta());
    }

    @Test
    void hu04_rechazaSegundoEquipoDelMismoDeporte() {
        Equipo otro = new Equipo("Andes FC", futbol, Categoria.MAYORES, 10);
        Socio ana = socio("ana", 20);
        ana.inscribirseEnEquipo(equipo, "delantero", 9);

        assertThrows(ClubException.class, () -> ana.inscribirseEnEquipo(otro, "delantero", 9));
        assertEquals(1, ana.getComposicion().size());
    }

    @Test
    void hu04_rechazaEquipoNulo() {
        assertThrows(ClubException.class, () -> socio("ana", 20).inscribirseEnEquipo(null, "delantero", 9));
    }

    //Integración: HU-07 / RF03 (estado de habilitación)

    @Test
    void sociaConDosAmarillasSeVeInhabilitadaYLuegoHabilitada() {
        List<Socio> js = plantel(equipo, 7);
        Socio ana = js.get(0);
        tarjetas(jugarPartido(equipo, js), ana, 2, false);
        assertFalse(ana.estaHabilitadoParaJugar(futbol));

        jugarPartido(equipo, js);

        assertTrue(ana.estaHabilitadoParaJugar(futbol));
        assertTrue(ana.getComposicion(futbol).consultarEstado().contains("habilitado"));
    }
}