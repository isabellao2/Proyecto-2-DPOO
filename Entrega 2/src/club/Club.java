package club;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Competencias.Competencia;
import DeporteConjunto.Categoria;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.Instalacion;
import Entrenamiento.SesionesEntrenamiento;
import Partidos.Partido;
import TiendaCafeteria.Cafeteria;
import TiendaCafeteria.TiendaDeportiva;
import Usuarios.Administrador;
import Usuarios.Empleado;
import Usuarios.Entrenador;
import Usuarios.Fisioterapeuta;
import Usuarios.Socio;
import Usuarios.Usuario;
import Torneos.Torneo;

/**
 * Raíz del modelo del Club Deportivo Séneca.
 * Es dueña de las colecciones principales del sistema y es el punto
 * desde el que la persistencia carga y guarda el estado completo.
 * No conoce archivos ni la consola.
 */
public class Club {

    // ---------------------------------------------------------------
    // Colecciones indexadas por su identificador
    // (LinkedHashMap conserva el orden de inserción, útil al guardar)
    // ---------------------------------------------------------------

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();                  // por login
    private final Map<String, DeporteConjunto> deportes = new LinkedHashMap<>();          // por nombre
    private final Map<String, Categoria> categorias = new LinkedHashMap<>();             // por nombre
    private final Map<String, Equipo> equipos = new LinkedHashMap<>();                   // por nombre
    private final Map<String, Competencia> competencias = new LinkedHashMap<>();         // por nombre
    private final Map<String, DisciplinaIndividual> disciplinas = new LinkedHashMap<>(); // por nombre
    private final Map<String, Instalacion> instalaciones = new LinkedHashMap<>();  
    private final Map<String, Torneo> torneos = new LinkedHashMap<>();      // por nombre

    // Colecciones sin identificador natural: se guardan como listas
    private final List<Partido> partidos = new ArrayList<>();
    private final List<SesionesEntrenamiento> sesiones = new ArrayList<>();

    // Únicas en el club
    private TiendaDeportiva tienda;
    private Cafeteria cafeteria;

    
    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public Club() {
        // El club arranca vacío; la persistencia lo llena al cargar.
    }

    // ---------------------------------------------------------------
    // Usuarios
    // ---------------------------------------------------------------

    public void agregarUsuario(Usuario u) {
        agregarSinRepetir(usuarios, u.getLogin(), u, "usuario");
        if (u instanceof Administrador) {
            ((Administrador) u).setClub(this);
        }
    }

      public void eliminarUsuario(Usuario u) {
        usuarios.remove(u.getLogin());
    }

    public Usuario buscarUsuario(String login) {
        return usuarios.get(login);
    }

    /** Devuelve el usuario si login y contraseña coinciden; si no, null. */
    public Usuario autenticar(String login, String contrasena) {
        Usuario u = usuarios.get(login);
        if (u != null && u.verificar(login, contrasena)) {
            return u;
        }
        return null;
    }

    public Collection<Usuario> getUsuarios() {
        return Collections.unmodifiableCollection(usuarios.values());
    }

    public Socio buscarSocio(String login) {
        Usuario u = usuarios.get(login);
        return (u instanceof Socio) ? (Socio) u : null;
    }

    

    public List<Socio> getSocios() {
        return filtrarUsuarios(Socio.class);
    }

    public List<Empleado> getEmpleados() {
        return filtrarUsuarios(Empleado.class);
    }

    public List<Entrenador> getEntrenadores() {
        return filtrarUsuarios(Entrenador.class);
    }

    public List<Fisioterapeuta> getFisioterapeutas() {
        return filtrarUsuarios(Fisioterapeuta.class);
    }

    public List<Administrador> getAdministradores() {
        return filtrarUsuarios(Administrador.class);
    }

    /** Busca al empleado dueño de un código de descuento (para la tienda y la cafetería). */
    public Empleado buscarEmpleadoPorCodigo(String codigo) {
        for (Empleado e : getEmpleados()) {
            if (e.esCodigoEmpleado(codigo)) {
                return e;
            }
        }
        return null;
    }
   
    // ---------------------------------------------------------------
    // Deportes de conjunto
    // ---------------------------------------------------------------

    public void agregarDeporte(DeporteConjunto d) {
        agregarSinRepetir(deportes, d.getNombre(), d, "deporte");
    }

    public DeporteConjunto buscarDeporte(String nombre) {
        return deportes.get(nombre);
    }

    public Collection<DeporteConjunto> getDeportes() {
        return Collections.unmodifiableCollection(deportes.values());
    }

    public void agregarCategoria(Categoria c) {
        agregarSinRepetir(categorias, c.getNombre(), c, "categoría");
    }

    public Categoria buscarCategoria(String nombre) {
        return categorias.get(nombre);
    }

    public Collection<Categoria> getCategorias() {
        return Collections.unmodifiableCollection(categorias.values());
    }

    public void agregarEquipo(Equipo e) {
        agregarSinRepetir(equipos, e.getNombre(), e, "equipo");
    }

    public Equipo buscarEquipo(String nombre) {
        return equipos.get(nombre);
    }

    public Collection<Equipo> getEquipos() {
        return Collections.unmodifiableCollection(equipos.values());
    }

    public void eliminarEquipo(Equipo e) {
        equipos.remove(e.getNombre());
    }

    // ---------------------------------------------------------------
    // Competencias
    // ---------------------------------------------------------------

    public void agregarCompetencia(Competencia c) {
        agregarSinRepetir(competencias, c.getNombre(), c, "competencia");
    }

    public Competencia buscarCompetencia(String nombre) {
        return competencias.get(nombre);
    }

    public Collection<Competencia> getCompetencias() {
        return Collections.unmodifiableCollection(competencias.values());
    }

    // ---------------------------------------------------------------
    // Academias individuales
    // ---------------------------------------------------------------

    public void agregarDisciplina(DisciplinaIndividual d) {
        agregarSinRepetir(disciplinas, d.getNombre(), d, "disciplina");
    }

    public DisciplinaIndividual buscarDisciplina(String nombre) {
        return disciplinas.get(nombre);
    }

    public Collection<DisciplinaIndividual> getDisciplinas() {
        return Collections.unmodifiableCollection(disciplinas.values());
    }

    public void agregarInstalacion(Instalacion i) {
        agregarSinRepetir(instalaciones, i.getNombre(), i, "instalación");
    }

    public Instalacion buscarInstalacion(String nombre) {
        return instalaciones.get(nombre);
    }

    public Collection<Instalacion> getInstalaciones() {
        return Collections.unmodifiableCollection(instalaciones.values());
    }

    // ---------------------------------------------------------------
    // Partidos, entrenamientos y ventas
    // ---------------------------------------------------------------

    public void agregarPartido(Partido p) {
        partidos.add(p);
    }

    public List<Partido> getPartidos() {
        return Collections.unmodifiableList(partidos);
    }

    public void agregarSesion(SesionesEntrenamiento s) {
        sesiones.add(s);
    }

    public List<SesionesEntrenamiento> getSesiones() {
        return Collections.unmodifiableList(sesiones);
    }

    // ---------------------------------------------------------------
    // Tienda y cafetería
    // ---------------------------------------------------------------

    public TiendaDeportiva getTienda() {
        return tienda;
    }

    public void setTienda(TiendaDeportiva tienda) {
        this.tienda = tienda;
    }

    public Cafeteria getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }

    // ---------------------------------------------------------------
    // Auxiliares privados
    // ---------------------------------------------------------------

    private <T> void agregarSinRepetir(Map<String, T> mapa, String id, T objeto, String tipo) {
        if (mapa.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un(a) " + tipo + " con identificador: " + id);
        }
        mapa.put(id, objeto);
    }

    private <T> List<T> filtrarUsuarios(Class<T> tipo) {
        List<T> resultado = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            if (tipo.isInstance(u)) {
                resultado.add(tipo.cast(u));
            }
        }
        return resultado;
    }

        // torneos 

        public void agregarTorneo(Torneo t) {
            agregarSinRepetir(torneos, t.getNombre(), t, "torneo");
        }
    
        public Torneo buscarTorneo(String nombre) {
            return torneos.get(nombre);
        }
    
        public Collection<Torneo> getTorneos() {
            return Collections.unmodifiableCollection(torneos.values());
        }
}
