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
       private final double valorBono;
    private final List <InscripcionTorneo> inscripciones;
    private EstadoTorneo estado;
    private boolean preferencialLiberados;
    private final List<String> historialEncuentros = new ArrayList<>();
    private Usuario ganador;
    private Equipo equipoGanador;

    // constructor con los casos de valores nulos o vacios

    public Torneo(String nombre, TipoTorneo tipo, Modalidad modalidad, String deporte, String categoria, int cupos, double tarifa, LocalDate fecha, double valorBono) throws ClubException {
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
                    throw new ClubException ("El torneo debe tener al menos un cupo");
                }
                if (fecha == null){
                    throw new ClubException ("Debe indicar la fecha del torneo");
                }
                if (tipo == TipoTorneo.COMPETITIVO && tarifa <=0){
                    throw new ClubException ("Al ser competitivo debe tener una tarifa mas alta que 0 ");
                }

                if (tipo == TipoTorneo.RECREATIVO && valorBono <= 0){
                    throw new ClubException ("Un torneo recreativo debe tener un bono mayor que 0");
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
                this.valorBono = (tipo == TipoTorneo.RECREATIVO) ? valorBono : 0;
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

             public double getValorBono() {
                return valorBono;
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
                if (compañero instanceof Empleado) {
                    validarEmpleado((Empleado) compañero);
                }
                TipoCupo cupo = asignarCupo(mejorRanking);
                InscripcionTorneo inscripcion = new InscripcionTorneo(participante, compañero, null, cupo, aPagar);
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
                if (tipo != TipoTorneo.COMPETITIVO){
                    throw new ClubException (" Los torneos recreativos no tienen un premio monetario, es un bono");
                }
                return calcularRecaudo () * porcentaje_premio;
            }

            public double calcularRecaudo (){
                double total = 0;
                for ( InscripcionTorneo i : inscripciones){
                    total += i.getTarifaPagada();
                }
                return total;
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
            validarUnaInscripcionAbierta("retirarte");
            InscripcionTorneo inscripcion = buscarinscrip(u);
            if (inscripcion == null) {
                throw new ClubException("Esa persona no está inscrita en " + nombre );
            }
            inscripciones.remove(inscripcion);
        }
    
            public void retirarEquipo(Equipo equipo) throws ClubException {
                validarUnaInscripcionAbierta("retirar al equipo");
                InscripcionTorneo inscripcion = buscarequipo(equipo);
                if (inscripcion == null) {
                    throw new ClubException("Ese equipo no está inscrito en " + nombre );
                }
                inscripciones.remove(inscripcion);
            }
        
            public boolean estaInscrito(Usuario u) {
                return buscarinscrip(u) != null;
            }
        
            public boolean estaInscrito(Equipo e) {
                return buscarequipo(e) != null;
            }

   

        // operaciones pqra saber lo de los cupos 

            public static int calcularCuposPreferenciales (int cuposTotales){
                return (cuposTotales + 4) / 5;
            }
            public int getCuposPreferenciales(){
                return calcularCuposPreferenciales(cupos);
            }
            public int getCuposLibres(){
                return cupos - inscripciones.size();
            }
            public int getCuposRegularesLibres (){
                return getCuposLibres() - getCuposPreferencialesLibres ();
            }
            private int conteoCupos (TipoCupo tipoCupo) {
                int total = 0;
                for(InscripcionTorneo i : inscripciones){
                    if(i.getTipoCupo()==tipoCupo){
                        total ++;
                    }
                }
                return total;
            }

            public void iniciar() throws ClubException {
            if (estado != EstadoTorneo.EN_INSCRIPCION) {
                throw new ClubException("El torneo " + nombre + " ya inició.");
            }
            if (inscripciones.size() < 2) {
                throw new ClubException("Se necesitan al menos 2 inscritos para iniciar el torneo.");
            }
            preferencialLiberados = true;
            estado = EstadoTorneo.EN_CURSO;
        }
    
            public void finalizar() throws ClubException {
                if (estado != EstadoTorneo.EN_CURSO) {
                    throw new ClubException("Solo se puede finalizar un torneo que está en curso.");
                }
                estado = EstadoTorneo.FINALIZADO;
            }
            private TipoCupo asignarCupo(boolean mejorRanking) throws ClubException {
            if (mejorRanking && getCuposPreferencialesLibres() > 0) {
                return TipoCupo.PREFERENCIAL;
            }
            if (getCuposRegularesLibres() > 0) {
                return TipoCupo.REGULAR;
            }
            if (getCuposLibres() == 0) {
                throw new ClubException("El torneo " + nombre + " no tiene cupos disponibles.");
            }
            throw new ClubException("Los cupos que quedan en " + nombre
                + " son preferenciales y solo son para los mejores del ranking.");
            }
            public int getCuposPreferencialesLibres() {
                if (preferencialLiberados) {
                    return 0;
                }
                return Math.max(0, getCuposPreferenciales() - conteoCupos(TipoCupo.PREFERENCIAL));
            }
    // Nuevo para persistencia 
             public void restaurarEstado(EstadoTorneo estadoGuardado) {
                this.estado = estadoGuardado;
                if (estadoGuardado != EstadoTorneo.EN_INSCRIPCION) {
            this.preferencialLiberados = true;
        }
    }

    // Nuevo para persistencia 
            public void restaurarInscripcion(InscripcionTorneo inscripcion) {
                if (inscripcion != null) {
                inscripciones.add(inscripcion);
        }
    }
    // Registrar un encuentro o resultado en el historial
    public void registrarEncuentro(String descripcionEncuentro) {
        if (historialEncuentros == null) {
        }
        historialEncuentros.add(descripcionEncuentro);
    }

    // Consultar el historial detallado (requerimiento)
    public List<String> getHistorialEncuentros() {
        return Collections.unmodifiableList(historialEncuentros);
    }

    public Usuario getGanador() {
        return ganador;
    }

    public Equipo getEquipoGanador() {
        return equipoGanador;
    }

    // Nuevo método para finalizar el torneo con un ganador específico
    public void finalizarTorneo(Usuario ganadorUsuario, Equipo ganadorEquipo) throws ClubException {
        if (estado != EstadoTorneo.EN_CURSO) {
            throw new ClubException("Solo se puede finalizar un torneo que está en curso.");
        }

        if (modalidad == Modalidad.CONJUNTO) {
            if (ganadorEquipo == null || !estaInscrito(ganadorEquipo)) {
                throw new ClubException("Debe especificar un equipo ganador válido que esté inscrito en el torneo.");
            }
            this.equipoGanador = ganadorEquipo;
            registrarEncuentro("Ganador: equipo " + ganadorEquipo.getNombre());
        } else {
            if (ganadorUsuario == null || !estaInscrito(ganadorUsuario)) {
                throw new ClubException("Debe especificar un participante ganador válido que esté inscrito en el torneo.");
            }

            if (tipo == TipoTorneo.COMPETITIVO) {
                // REGLA: los empleados no reciben el premio en efectivo
                if (ganadorUsuario instanceof Empleado) {
                    registrarEncuentro("Ganador: " + ganadorUsuario.getLogin() + " (Empleado - Sin premio en efectivo)");
                } else {
                    registrarEncuentro("Ganador: " + ganadorUsuario.getLogin() + " (premio en efectivo)");
                }
            } else {
                // REGLA: los torneos recreativos premian con un bono de descuento
                if (ganadorUsuario instanceof Usuarios.Socio) {
                    ((Usuarios.Socio) ganadorUsuario).agregarBonoDescuento(new BonoDescuento(valorBono, nombre));
                    registrarEncuentro("Ganador recreativo con bono asignado: " + ganadorUsuario.getLogin());
                } else {
                    registrarEncuentro("Ganador: " + ganadorUsuario.getLogin() + " (sin bono)");
                }
            }
            this.ganador = ganadorUsuario;
        }

        estado = EstadoTorneo.FINALIZADO;
    }
    
}




                
                