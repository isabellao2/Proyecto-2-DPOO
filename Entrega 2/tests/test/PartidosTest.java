package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Partidos.EstadisticaBaloncesto;
import Partidos.PartidoInterno;
import Usuarios.Socio;
import Partidos.EstadisticaVoleibol;
import Partidos.EstadisticaFutbol;

class PartidosTest {

    private Equipo aguilas;
    private Equipo buhos;
    private Socio jugador;

    @BeforeEach
    void preparar() {
        aguilas = new Equipo("Águilas", DeporteConjunto.futbol(), Categoria.MAYORES, 12);
        buhos = new Equipo("Búhos", DeporteConjunto.futbol(), Categoria.MAYORES, 12);
        jugador = new Socio("laura", "1234", 20);
    }

    private void llenar(Equipo e, String prefijo, int cantidad) {
        for (int i = 1; i <= cantidad; i++) {
            new Socio(prefijo + i, "1234", 20).inscribirseEnEquipo(e, "Jugador", i);
        }
    }

    private PartidoInterno futbol() {
        return new PartidoInterno(new Date(), DeporteConjunto.futbol(), List.of(aguilas, buhos));
    }


    @Test
    void PartidoPuedeJugarSiAmbosTienenMinimo() {
        llenar(aguilas, "a", 7);
        llenar(buhos, "b", 7);
        assertTrue(futbol().puedeJugarse(aguilas));
    }

    @Test
    void noSePuedeSiEquipoNoTieneElMinimo() {
        llenar(aguilas, "a", 7);
        llenar(buhos, "b", 6);
        assertFalse(futbol().puedeJugarse(aguilas));
    }

    @Test
    void JugadorNoAptoNoCuenta() {
        llenar(aguilas, "a", 6);
        Socio lesionado = new Socio("lesionado", "1234", 20);
        lesionado.inscribirseEnEquipo(aguilas, "Delantero", 11);
        lesionado.setFisicoApto(false);
        llenar(buhos, "b", 7);
        assertFalse(futbol().puedeJugarse(aguilas));
    }

    @Test
    void sinEquiposNoSePuedeJugar() {
        PartidoInterno vacio = new PartidoInterno(new Date(), DeporteConjunto.futbol(), List.of());
        assertFalse(vacio.puedeJugarse(aguilas));
    }

    @Test
    void MinimoDependeDelDeporte() {
        DeporteConjunto baloncesto = DeporteConjunto.baloncesto();
        Equipo tigres = new Equipo("Tigres", baloncesto, Categoria.MAYORES, 10);
        Equipo pumas = new Equipo("Pumas", baloncesto, Categoria.MAYORES, 10);
        llenar(tigres, "t", 5);
        llenar(pumas, "p", 4);
        PartidoInterno p = new PartidoInterno(new Date(), baloncesto, List.of(tigres, pumas));
        assertFalse(p.puedeJugarse(tigres));
        new Socio("p5", "1234", 20).inscribirseEnEquipo(pumas, "Pívot", 5);
        assertTrue(p.puedeJugarse(tigres));
    }



    @Test
    void FutbolPermiteEmpates() {
        PartidoInterno p = futbol();
        p.registrarMarcador(2, 2);
        assertTrue(p.hayEmpate());
        assertEquals(2, p.getMarcadorLocal());
        assertEquals(2, p.getMarcadorVisitante());
    }

    @Test
    void BaloncestoYVoleibolRechazanEmpates() {
        PartidoInterno baloncesto = new PartidoInterno(new Date(), DeporteConjunto.baloncesto(), List.of());
        assertThrows(IllegalArgumentException.class, () -> baloncesto.registrarMarcador(80, 80));
        baloncesto.registrarMarcador(80, 75);
        assertFalse(baloncesto.hayEmpate());

        PartidoInterno voleibol = new PartidoInterno(new Date(), DeporteConjunto.voleibol(), List.of());
        assertThrows(IllegalArgumentException.class, () -> voleibol.registrarMarcador(2, 2));
        voleibol.registrarMarcador(3, 1); // el marcador del voleibol son sets
        assertEquals(3, voleibol.getMarcadorLocal());
    }

    @Test
    void elMarcadorNoPuedeSerNegativo() {
        PartidoInterno p = futbol();
        assertThrows(IllegalArgumentException.class, () -> p.registrarMarcador(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> p.registrarMarcador(0, -2));
    }

    @Test
    void MarcadorRechazadoNoCambiaElResultadoAnterior() {
        PartidoInterno baloncesto = new PartidoInterno(new Date(), DeporteConjunto.baloncesto(), List.of());
        baloncesto.registrarMarcador(70, 60);
        assertThrows(IllegalArgumentException.class, () -> baloncesto.registrarMarcador(50, 50));
        assertEquals(70, baloncesto.getMarcadorLocal());
    }


    @Test
    void sinEstadisticaDevuelveNull() {
        PartidoInterno p = futbol();
        p.agregarParticipante(jugador);
        assertNull(p.obtenerEstadistica(jugador));
    }



    @Test
    void DeporteAceptaSuPropiaEstadistica() {
        PartidoInterno basket = new PartidoInterno(new Date(), DeporteConjunto.baloncesto(), List.of());
        PartidoInterno volei = new PartidoInterno(new Date(), DeporteConjunto.voleibol(), List.of());
        basket.agregarParticipante(jugador);
        volei.agregarParticipante(jugador);
        EstadisticaBaloncesto eb = new EstadisticaBaloncesto(jugador, basket, 12, 4);
        EstadisticaVoleibol ev = new EstadisticaVoleibol(jugador, 3, 2, volei);
        basket.agregarEstadistica(eb);
        volei.agregarEstadistica(ev);
        assertNotNull(basket.obtenerEstadistica(jugador));
        assertNotNull(volei.obtenerEstadistica(jugador));
        assertFalse(basket.corresponde(ev));
        assertFalse(volei.corresponde(eb));
    }

    @Test
    void correspondeRechazaNulos() {
        assertFalse(futbol().corresponde(null));
    }


    @Test
    void dosAmarillasOUnaRojaGeneranSuspension() {
        PartidoInterno p = futbol();
        EstadisticaFutbol e = new EstadisticaFutbol(jugador, 0, 0, 0, false, p);
        assertFalse(e.generaSuspension());
        e.registrarAmarilla();
        assertFalse(e.generaSuspension());
        e.registrarAmarilla();
        assertTrue(e.generaSuspension());

        EstadisticaFutbol roja = new EstadisticaFutbol(jugador, 0, 0, 0, false, p);
        roja.registrarRoja();
        assertTrue(roja.generaSuspension());
        assertTrue(roja.isTarjetasRojas());
    }

    @Test
    void BaloncestoSoloSumaPuntosPositivos() {
        PartidoInterno basket = new PartidoInterno(new Date(), DeporteConjunto.baloncesto(), List.of());
        EstadisticaBaloncesto e = new EstadisticaBaloncesto(jugador, basket, 10, 2);
        e.sumarPuntos(3);
        e.sumarPuntos(-5);
        e.sumarPuntos(0);
        assertEquals(13, e.getPuntos());
        assertEquals(2, e.getRebotes());
    }

    @Test
    void PartidoGuardaFechaDeporteYParticipantes() {
        Date fecha = new Date();
        PartidoInterno p = new PartidoInterno(fecha, DeporteConjunto.futbol(), List.of(aguilas, buhos));
        p.agregarParticipante(jugador);
        assertEquals(fecha, p.getFecha());
        assertTrue(p.getDeporte().esFutbol());
        assertEquals(2, p.getEquiposParticipantes().size());
        assertEquals(1, p.getParticipantes().size());
    }
}