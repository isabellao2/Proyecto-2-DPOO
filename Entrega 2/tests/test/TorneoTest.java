package test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
 
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import Excepciones.ClubException;
import Usuarios.Entrenador;
import Usuarios.Socio;
import Torneos.Torneo;
import Torneos.TipoTorneo;
import Torneos.Modalidad;
import Torneos.TipoCupo;

class TorneoTest {

	private static final LocalDate LUNES = LocalDate.of(2026, 11, 16);
	
	 
    private Torneo competitivo;
    private Socio socio1;
    private Socio socio2;
    private Socio socio3;
 
    @BeforeEach
    void preparar() {
    	competitivo = new Torneo("Copa", TipoTorneo.competitivo, Modalidad.INDIVIDUAL, "Tenis",
    	        "Intermedio", 5, 110000, LocalDate.of(2026, 11, 23), 0);
        socio1 = new Socio("socio1", "x", 20);
        socio2 = new Socio("socio2", "x", 21);
        socio3 = new Socio("socio3", "x", 22);
    }
 
    @Test
    void cuposPreferencialesSonElVeintePorCientoArriba() {
        assertEquals(2, Torneo.calcularCuposPreferenciales(10));
        assertEquals(2, Torneo.calcularCuposPreferenciales(8));
        assertEquals(3, Torneo.calcularCuposPreferenciales(12));
        assertEquals(1, Torneo.calcularCuposPreferenciales(5));
    }
    @Test 
    void torneoRecreativoTarifa () {
    	Torneo r = new Torneo("R", TipoTorneo.recreativo, Modalidad.INDIVIDUAL, "Tenis",
    	        "Intermedio", 9999, 0, LocalDate.of(2026, 11, 23), 50000);
    	}
    @Test
    void torneoCompetitivoTarifa () {
        assertThrows(ClubException.class, () -> new Torneo("T", TipoTorneo.competitivo,
                Modalidad.INDIVIDUAL, "Tenis", "Intermedio", 10, 0, LocalDate.of(2026, 11, 23), 0));
    }
    @Test
    void noDuplicarInscripcion () {
    	competitivo.inscribirElJugador(socio3, null, false);
        assertEquals(4, competitivo.getCuposLibres());
        assertThrows(ClubException.class, () -> competitivo.inscribirElJugador(socio3, null, false));
    }
    @Test 
    void liberarCupoTorneo () {
    	competitivo.inscribirElJugador(socio1, null, false);
    	competitivo.retirarJugador(socio1);
    	assertEquals (5, competitivo.getCuposLibres());
    	
    }
    
    @Test
    void empleadoTurnoDiaDelTorneo() {
        Entrenador coach = new Entrenador("coach", "x", "C1");
        coach.agregarTurno(new Usuarios.Turno(coach, DayOfWeek.MONDAY, LocalTime.of(8, 0)));
        assertThrows(ClubException.class, () -> competitivo.inscribirElJugador(coach, null, false));
    }
    @Test
    void CupoPreferencialSinEstarRanking() {
    	Torneo t = new Torneo("P", TipoTorneo.recreativo, Modalidad.INDIVIDUAL, "Tenis", "Avanzado", 2,
    	        0, LocalDate.of(2026, 11, 23), 50000);
        t.inscribirElJugador(socio1, null, false);
        assertThrows(ClubException.class, () -> t.inscribirElJugador(socio2, null, false));
        assertEquals(TipoCupo.preferencial, t.inscribirElJugador(socio2, null, true).getTipoCupo());
    }
    @Test
    void noSeIniciaConMenosDeDosInscritos() {
        competitivo.inscribirElJugador(socio1, null, false);
        assertThrows(ClubException.class, () -> competitivo.iniciar());
    }
 
    @Test
    void dobleDebeTenerCompaniero() {
    	Torneo dobles = new Torneo("D", TipoTorneo.recreativo, Modalidad.DOBLES, "Tenis", "Avanzado", 4,
    	        0, LocalDate.of(2026, 11, 23), 50000);
        assertThrows(ClubException.class, () -> dobles.inscribirElJugador(socio1, null, false));
        dobles.inscribirElJugador(socio1, socio2, false);
        assertTrue(dobles.estaInscrito(socio2));
    }
    @Test
    void CancelarSalidoTorneoIniciado() {
        competitivo.inscribirElJugador(socio1, null, false);
        competitivo.inscribirElJugador(socio2, null, false);
        competitivo.iniciar();
        assertThrows(ClubException.class, () -> competitivo.inscribirElJugador(socio3, null, false));
        assertThrows(ClubException.class, () -> competitivo.retirarJugador(socio1));
    }
    @Test
    void equipoDebeCumplirMinimoDeJugadoresYNoSeRepite() {
        Equipo eq = new Equipo("Águilas", DeporteConjunto.futbol(), Categoria.MAYORES, 12);
        Torneo conj = new Torneo("Mini", TipoTorneo.recreativo, Modalidad.CONJUNTO, "Fútbol", "Mayores", 7,
                0, LocalDate.of(2026, 11, 23), 50000);
        assertThrows(ClubException.class, () -> conj.inscripcionEquipos(eq, false));
        for (int i = 1; i <= 7; i++) {
            new Socio("j" + i, "x", 20).inscribirseEnEquipo(eq, "Delantero", i);
        }
        conj.inscripcionEquipos(eq, false);
        assertTrue(conj.estaInscrito(eq));
        assertThrows(ClubException.class, () -> conj.inscripcionEquipos(eq, false));
    }
    @Test
    void PreferencialesSobrantesPasanARegulares() {
        competitivo.inscribirElJugador(socio1, null, false);
        competitivo.inscribirElJugador(socio2, null, false);
        assertEquals(2, competitivo.getCuposRegularesLibres());
        competitivo.iniciar();
        assertEquals(3, competitivo.getCuposRegularesLibres());
    }
    }
    