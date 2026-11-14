package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Excepciones.ClubException;
import Partidos.EstadisticaFutbol;
import Partidos.PartidoInterno;
import Usuarios.Socio;

class DeporteConjuntoTest {

    private DeporteConjunto futbol;
    private Equipo equipo;
    private Socio ana;

    @BeforeEach
    void preparar() {
        futbol = DeporteConjunto.futbol();
        equipo = new Equipo("Cerros FC", futbol, Categoria.MAYORES, 7);
        ana = new Socio("ana", "1234", 20);
    }

    //Categoría 

    @Test
    void categoriaCumpleEdadEnLosBordes() {
        assertTrue(Categoria.SUB_15.cumpleEdad(13));
        assertTrue(Categoria.SUB_15.cumpleEdad(14));
        assertFalse(Categoria.SUB_15.cumpleEdad(15));
        assertTrue(Categoria.MAYORES.cumpleEdad(17));
    }

    // Deporte 

    @Test
    void futbolTieneMinimoSieteYPermiteEmpate() {
        assertEquals(7, futbol.getNumeroMinimoJugadores());
        assertTrue(futbol.permiteEmpate());
        assertFalse(DeporteConjunto.baloncesto().permiteEmpate());
    }

    // HU-04 / RF02: inscribirse a un equipo 

    @Test
    void socioSeInscribeConPosicionYCamiseta() {
        ana.inscribirseEnEquipo(equipo, "portero", 1);

        assertTrue(equipo.tieneJugador(ana));
        assertEquals(1, ana.getComposicion().size());
    }

    @Test
    void rechazaSegundoEquipoDelMismoDeporte() {
        Equipo otro = new Equipo("Andes FC", futbol, Categoria.MAYORES, 7);
        ana.inscribirseEnEquipo(equipo, "portero", 1);

        assertThrows(ClubException.class, () -> ana.inscribirseEnEquipo(otro, "portero", 1));
    }

    @Test
    void permiteEquiposDeDeportesDistintos() {
        Equipo basket = new Equipo("Cerros Basket", DeporteConjunto.baloncesto(), Categoria.MAYORES, 5);

        ana.inscribirseEnEquipo(equipo, "portero", 1);
        ana.inscribirseEnEquipo(basket, "base", 1);

        assertEquals(2, ana.getComposicion().size());
    }

    @Test
    void rechazaEdadFueraDeLaCategoria() {
        Equipo sub15 = new Equipo("Sub15 FC", futbol, Categoria.SUB_15, 7);

        assertThrows(ClubException.class, () -> ana.inscribirseEnEquipo(sub15, "delantero", 9));
    }

    @Test
    void rechazaCamisetaRepetida() {
        Socio bruno = new Socio("bruno", "1234", 20);
        ana.inscribirseEnEquipo(equipo, "portero", 1);

        assertThrows(ClubException.class, () -> bruno.inscribirseEnEquipo(equipo, "defensa", 1));
    }

    @Test
    void rechazaCuandoElEquipoEstaLleno() {
        for (int i = 1; i <= 7; i++) {
            new Socio("j" + i, "1234", 20).inscribirseEnEquipo(equipo, "Jugador", i);
        }

        assertThrows(ClubException.class, () -> ana.inscribirseEnEquipo(equipo, "portero", 8));
    }

    @Test
    void retirarJugadorLiberaElCupo() {
        ana.inscribirseEnEquipo(equipo, "portero", 1);

        equipo.retirarJugador(ana);

        assertFalse(equipo.tieneJugador(ana));
        assertTrue(equipo.camisetaDisponible(1));
    }

    @Test
    void conSieteHabilitadosCumpleElMinimoYSinUnoNo() {
        for (int i = 1; i <= 7; i++) {
            new Socio("j" + i, "1234", 20).inscribirseEnEquipo(equipo, "Jugador", i);
        }
        assertTrue(equipo.cumpleMinimoJugadores());

        equipo.getJugadores().get(0).setFisicoApto(false);

        assertFalse(equipo.cumpleMinimoJugadores());
    }

    //HU-07 / RF03: suspensión por tarjetas

    @Test
    void dosAmarillasSuspendenAlJugador() {
        ana.inscribirseEnEquipo(equipo, "delantero", 9);
        PartidoInterno partido = new PartidoInterno(new Date(), futbol, List.of(equipo));
        partido.agregarParticipante(ana);
        partido.agregarEstadistica(new EstadisticaFutbol(ana, 0, 0, 2, false, partido));
        equipo.registrarPartidoJugado(partido);

        assertFalse(ana.estaHabilitadoParaJugar(futbol));
    }

    @Test
    void unaSolaAmarillaNoSuspende() {
        ana.inscribirseEnEquipo(equipo, "delantero", 9);
        PartidoInterno partido = new PartidoInterno(new Date(), futbol, List.of(equipo));
        partido.agregarParticipante(ana);
        partido.agregarEstadistica(new EstadisticaFutbol(ana, 0, 0, 1, false, partido));
        equipo.registrarPartidoJugado(partido);

        assertTrue(ana.estaHabilitadoParaJugar(futbol));
    }
}