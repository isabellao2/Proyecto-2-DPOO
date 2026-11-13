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

class TorneoTest {

	private static final LocalDate LUNES = LocalDate.of(2026, 11, 16);
	 
    private Torneo competitivo;
    private Socio socio1;
    private Socio socio2;
    private Socio socio3;
 
    @BeforeEach
    void preparar() {
        competitivo = new Torneo("Copa", TipoTorneo.competitivo, Modalidad.INDIVIDUAL, "Tenis",
                "Intermedio", 5, 110000, LUNES);
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
    	Torneo r = new Torneo("R", TipoTorneo.recreativo, Modalidad.INDIVIDUAL, "Tenis", "Intermedio", 4,
                9999, LUNES);
        assertEquals(0, r.getTarifa());
    }
    @Test
    void torneoCompetitivoTarifa () {
    	assertThrows(ClubException.class, () -> new Torneo("T", TipoTorneo.competitivo,
                Modalidad.INDIVIDUAL, "Tenis", "Intermedio", 10, 0, LUNES));
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
    }
    