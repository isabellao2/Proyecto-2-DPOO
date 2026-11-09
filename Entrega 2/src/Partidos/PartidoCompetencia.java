package Partidos;

import java.util.Date;
import DeporteConjunto.DeporteConjunto;
import Competencias.Competencia;
import Competencias.EquipoExterno;

public class PartidoCompetencia extends Partido {
	
	// atributos 
    private String institucionRival;
    private String escenario;
    private String local; 
    private Competencia competencia;
    private EquipoExterno equipoExterno; 
    
    // constructor 
    public PartidoCompetencia(Date fecha, String institucionRival, String escenario, String local, Competencia competencia, EquipoExterno equipoExterno, DeporteConjunto deporte) {
        super(fecha, deporte);
        this.institucionRival = institucionRival;
        this.escenario = escenario;
        this.local = local;
        this.competencia = competencia;
        this.equipoExterno = equipoExterno;
    }
    
    // metodos 
    public String getInstitucionRival() {
        return institucionRival;
    }

    public void setInstitucionRival(String institucionRival) {
        this.institucionRival = institucionRival;
    }

    public String getEscenario() {
        return escenario;
    }

    public void setEscenario(String escenario) {
        this.escenario = escenario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public EquipoExterno getEquipoExterno() {
        return equipoExterno;
    }

    public void setEquipoExterno(EquipoExterno equipoExterno) {
        this.equipoExterno = equipoExterno;
    }
}