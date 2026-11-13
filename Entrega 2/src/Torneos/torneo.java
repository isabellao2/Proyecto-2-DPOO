package Torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
import DeporteConjunto.Equipo;
import Excepciones.ClubException;
import Usuarios.Empleado;
import Usuarios.Turno;
import Usuarios.Usuario;

public class Torneo {
    public static final double porcentaje_premio =0.80;

    private final String nombre;
    private final TipoTorneo tipo;
    private final Modalidad modalidad;
    private final String deporte;
    private final String categoria;
    private final int cupos;
    private final double tarifa;
    private final LocalDate fecha;
    private final List <InscripcionTorneo> inscripciones;
    private EstadoTorneo estado;
    private boolean preferencialLiberados;

    // constructor con los casos de valores nulos o vacios

    public Torneo(String nombre, TipoTorneo tipo, Modalidad modalidad, String deporte, String categoria, int cupos, double tarifa, LocalDate fecha) throws ClubException {
                if (nombre == null || nombre.isBlank ()){
                    throw new ClubException ("El torneo debe tener un nombre");
                }
                if (tipo == null || modalidad == null) {
                    throw new ClubException ("Debe especificar el tipo y la modalidad del torneo");
                }
                if (deporte == null || deporte.isBlank()|| categoria == null || categoria.isBlank()){
                    throw new ClubException ("Debe tener un deporte y una categoria o nivel");
                }
                if (cupos <=0){
                    throw new ClubException ("El torneo debe tener al menos dos cupos");
                }
                if (fecha == null){
                    throw new ClubException ("Debe indicar la fecha del torneo");
                }
                if (tipo == TorneoTipo.competitivo && tarifa <=0){
                    throw new ClubException ("Al ser competitivo debe tener una tarifa mas alta que 0 ");
                }
                this.nombre = nombre.trim();
                this.tipo = tipo;
                this.modalidad = modalidad;
                this.deporte = deporte.trim();
                this.categoria = categoria.trim();
                this.cupos = cupos;
                this.tarifa = (tipo == TipoTorneo.RECREATIVO) ? 0 : tarifa;
                this.fecha = fecha;
                this.estado = EstadoTorneo.EN_INSCRIPCION;
                this.preferencialLiberados = false;
                this.inscripciones = new ArrayList<>();
            }

            // getters

            public String getNombre() {
                return nombre;
            }
        
            public TipoTorneo getTipo() {
                return tipo;
            }
        
            public Modalidad getModalidad() {
                return modalidad;
            }
        
            public String getDeporte() {
                return deporte;
            }
        
            public String getCategoria() {
                return categoria;
            }
        
            public int getCupos() {
                return cupos;
            }
        
            public double getTarifa() {
                return tarifa;
            }
        
            public LocalDate getFecha() {
                return fecha;
            }
        
            public EstadoTorneo getEstado() {
                return estado;
            }
        
            public List<InscripcionTorneo> getInscripciones() {
                return Collections.unmodifiableList(inscripciones);
            }
        
            @Override
            public String toString() {
                return nombre + " (" + tipo + ", " + estado + ")";
            }

            // inscripcion en los torneos 

            public InscripcionTorneo inscribirElJugador (Usuario participante, Usuario compañero, boolean mejorRanking) throws ClubException {
                validarUnaInscripcionAbierta ("inscribete");
                if(modalidad == Modalidad.CONJUNTO) {
                    throw new ClubException("Este torneo"+ nombre + "se juega en equipos, debes inscirbir un equipo completo");
                }
                if (participante == null){
                    throw new ClubException ("Denber insertar un jugador para inscribir");

                }
                if (modalidad == Modalidad.DOBLES && compañero == null) {
                    throw new ClubException("Un torneo de dobles necesita una pareja.");
                }
                if (modalidad == Modalidad.INDIVIDUAL && compañero != null) {
                    throw new ClubException("Este torneo es individual, no se inscribe con pareja.");
                }
                if (compañero != null && compañero == participante) {
                    throw new ClubException("La pareja debe ser otra persona.");
                }
                if (estaInscrito(participante) || estaInscrito(compañero)) {
                    throw new ClubException("Ya hay una inscripción de esa persona en " + nombre + ".");
                }
                double aPagar = tarifa;
                if (participante instanceof Empleado) {
                    validarEmpleado((Empleado) participante);
                    aPagar = 0;
                }
                if (pareja instanceof Empleado) {
                    validarEmpleado((Empleado) compañero);
                }
                TipoCupo cupo = asignarCupo(mejorRanking);
                InscripcionTorneo inscripcion = new InscripcionTorneo(participante, pareja, null, cupo, aPagar);
                inscripciones.add(inscripcion);
                return inscripcion;
            }

            public InscripcionTorneo inscripcionEquipos (Equipo equipo, boolean mejorRanking) throws ClubException{
                validarUnaInscripcionAbierta ("inscribe al equipo");
                if (modalidad != Modalidad.CONJUNTO){
                    throw new ClubException ("este torneo"+ nombre + " no se puede jugar en equipos de conjunto");

                }
                if  (!equipo.getDeporte().getNombre().equalsIgnoreCase(deporte)
                || !equipo.getCategoria().getNombre().equalsIgnoreCase(categoria)) {
                    throw new ClubException("El equipo " + equipo.getNombre() + " no es de " + deporte + " " + categoria + ".");
            }
                if (!equipo.cumpleMinimoJugadores()) {
                     throw new ClubException("El equipo " + equipo.getNombre()
                    + " no alcanza el mínimo de jugadores habilitados.");
            }
                if (estaInscrito(equipo)) {
                     throw new ClubException("El equipo " + equipo.getNombre() + " ya está inscrito.");
            }
            TipoCupo cupo = asignarCupo(mejorRanking);
            InscripcionTorneo inscripcion = new InscripcionTorneo(null, null, equipo, cupo, tarifa);
            inscripciones.add(inscripcion);
            return inscripcion;
    }

    // funciones que se necesitan para que funcione lo de arriba 
             public void validarEmpleado (Empleado e) throws ClubException {
                for (Turno t : e.getTurnos ()){
                    if (t.getDia() == fecha.getDayOfWeek()) {
                        throw new ClubException (e.getLogin()+ "tiene un turno este dia" + t.getDia () + "que es el dia del torneo asi que no puede inscribirse :()");
                    }
                }
             }

             public void validarUnaInscripcionAbierta (String inscrip) throws ClubException {
                if (estado != EstadoTorneo.EN_INSCRIPCION){
                    throw new ClubException ("No puedes"+ inscrip + 
                    
                    "pues el torneo" + nombre + "ya inicio o ya termino...");
                }
             }
    // premios del torneo 
            public double calcularPremio () throws ClubException {
                if (tipo != TipoTorneo.competitivo){
                    throw new ClubException (" Los torneos recreativos no tienen un premio monetario, es un bono");
                }
                return calcularRecaudo () * porcentaje_premio;
            }

            public void calcularRecaudo (){
                double total = 0;
                for ( InscripcionTorneo i : inscripciones){
                    total += i.getTarifa();
                }
                return total;
            }

            }

        // operaciones de consulta o sobre los jugadores 

            public InscripcionTorneo buscarinscrip (Usuario u) {
                for (InscripcionTorneo i : inscripciones){
                    if (i.esDe(u)) {
                        return i;
                    }
                }
                return null;
            }

            public InscripcionTorneo buscarequipo (Equipo e){
                for (InscripcionTorneo i : inscripciones){
                    if (i.esDe(e)){
                        return i;
                    }
                }
                return null;
            }

            public void retirarJugador(Usuario u) throws ClubException {
            validarInscripcionAbierta("retirarte");
            InscripcionTorneo inscripcion = buscar(u);
            if (inscripcion == null) {
                throw new ClubException("Esa persona no está inscrita en " + nombre );
            }
            inscripciones.remove(inscripcion);
        }
    
            public void retirarEquipo(Equipo equipo) throws ClubException {
                validarInscripcionAbierta("retirar al equipo");
                InscripcionTorneo inscripcion = buscar(equipo);
                if (inscripcion == null) {
                    throw new ClubException("Ese equipo no está inscrito en " + nombre );
                }
                inscripciones.remove(inscripcion);
            }
        
            public boolean estaInscrito(Usuario u) {
                return buscar(u) != null;
            }
        
            public boolean estaInscrito(Equipo e) {
                return buscar(e) != null;
            }


            






                
                