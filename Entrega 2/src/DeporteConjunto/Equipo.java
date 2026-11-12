package DeporteConjunto;

import Excepciones.ClubException;
import Partidos.Partido;
import Usuarios.Socio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Equipo {

    private final String nombre;
    private final int cupoMaximo;
    private final DeporteConjunto deporte;
    private final Categoria categoria;
    private final List<ComposicionEquipo> composicion;
    private final List<Partido> partidosJugados;

    public Equipo(String nombre, DeporteConjunto deporte, Categoria categoria, int cupoMaximo) throws ClubException {
        if (nombre == null || nombre.isBlank()) {
            throw new ClubException("Todo equipo necesita un nombre.");
        }
        if (deporte == null || categoria == null) {
            throw new ClubException("Para crear el equipo indica su deporte y su categoría.");
        }
        if (cupoMaximo < deporte.getNumeroMinimoJugadores()) {
            throw new ClubException("El cupo de " + cupoMaximo + " es muy pequeño: " + deporte.getNombre()
                    + " necesita al menos " + deporte.getNumeroMinimoJugadores() + " jugadores.");
        }
        this.nombre = nombre.trim();
        this.deporte = deporte;
        this.categoria = categoria;
        this.cupoMaximo = cupoMaximo;
        this.composicion = new ArrayList<>();
        this.partidosJugados = new ArrayList<>();
    }

    public boolean hayCupo() {
        return composicion.size() < cupoMaximo;
    }

    public boolean camisetaDisponible(int numero) {
        for (ComposicionEquipo c : composicion) {
            if (c.getNumeroCamiseta() == numero) {
                return false;
            }
        }
        return true;
    }

    public boolean tieneJugador(Socio s) {
        return getComposicion(s) != null;
    }

    public ComposicionEquipo getComposicion(Socio s) {
        for (ComposicionEquipo c : composicion) {
            if (c.getJugador() == s) {
                return c;
            }
        }
        return null;
    }

    public int contarHabilitados() {
        int total = 0;
        for (ComposicionEquipo c : composicion) {
            if (c.estaHabilitado()) {
                total++;
            }
        }
        return total;
    }

    public boolean cumpleMinimoJugadores() {
        return deporte.cumpleMinimo(contarHabilitados());
    }

    public List<String> listanohabilitados (){
         List<String> lista = new ArrayList<>();
         for (ComposicionEquipo c : composicion) {
            if (!c.estaHabilitado()) {
                lista.add(c.consultarEstado());
        }
    }
    return lista;
}

    }

    
    public boolean puedeAceptar(Socio s, int numeroCamiseta) throws ClubException {
        if (s == null) {
            throw new ClubException("Necesitamos al socio que quiere unirse al equipo.");
        }
        if (tieneJugador(s)) {
            throw new ClubException(s.getLogin() + " ya hace parte del equipo " + nombre + ".");
        }
        if (s.perteneceAEquipoDe(deporte)) {
            throw new ClubException(s.getLogin() + " ya pertenece a un equipo de " + deporte.getNombre()
                    + ". Solo se puede estar en un equipo por deporte.");
        }
        if (!categoria.cumpleEdad(s.getEdad())) {
            throw new ClubException("Con " + s.getEdad() + " años no puedes entrar a la categoría "
                    + categoria.getNombre() + " (de " + categoria.getEdadMinima() + " a "
                    + categoria.getEdadMaxima() + " años).");
        }
        if (!hayCupo()) {
            throw new ClubException("El equipo " + nombre + " ya está completo (cupo de " + cupoMaximo + ").");
        }
        if (numeroCamiseta < 1 || numeroCamiseta > 99) {
            throw new ClubException("El número de camiseta debe estar entre 1 y 99.");
        }
        if (!camisetaDisponible(numeroCamiseta)) {
            throw new ClubException("La camiseta " + numeroCamiseta + " ya tiene dueño en " + nombre
                    + ". ¿Eliges otro número?");
        }
        return true;
    }

    public ComposicionEquipo agregarJugador(Socio s, String posicion, int numeroCamiseta) throws ClubException {
        puedeAceptar(s, numeroCamiseta);
        ComposicionEquipo c = new ComposicionEquipo(s, this, posicion, numeroCamiseta);
        composicion.add(c);
        s.registrarComposicion(c);
        return c;
    }

    public void retirarJugador(Socio s) throws ClubException {
        ComposicionEquipo c = getComposicion(s);
        if (c == null) {
            throw new ClubException((s == null ? "Ese jugador" : s.getLogin()) + " no hace parte del equipo " + nombre + ".");
        }
        composicion.remove(c);
        s.retirarComposicion(c);
    }


    public void disolver() {
        for (ComposicionEquipo c : new ArrayList<>(composicion)) {
            c.getJugador().retirarComposicion(c);
        }
        composicion.clear();
    }

    public void registrarPartidoJugado(Partido p) {
        if (p != null && !partidosJugados.contains(p)) {
            partidosJugados.add(p);
        }
    }

    public Partido getUltimoPartidoJugado() {
        return partidosJugados.isEmpty() ? null : partidosJugados.get(partidosJugados.size() - 1);
    }

    public List<Partido> getPartidosJugados() {
        return Collections.unmodifiableList(partidosJugados);
    }

    public List<Socio> getJugadores() {
        List<Socio> jugadores = new ArrayList<>();
        for (ComposicionEquipo c : composicion) {
            jugadores.add(c.getJugador());
        }
        return jugadores;
    }

    public List<ComposicionEquipo> getComposicion() {
        return Collections.unmodifiableList(composicion);
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public DeporteConjunto getDeporte() {
        return deporte;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
