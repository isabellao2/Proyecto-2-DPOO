package Consolas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.Club;
import DeporteConjunto.ComposicionEquipo;
import DeporteConjunto.DeporteConjunto;
import DeporteConjunto.Equipo;
import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.Instalacion;
import Partidos.EstadisticaPartido;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.Bebida;
import TiendaCafeteria.Cafeteria;
import TiendaCafeteria.Snack;
import TiendaCafeteria.TiendaDeportiva;
import TiendaCafeteria.Venta;
import Torneos.EstadoTorneo;
import Torneos.Modalidad;
import Torneos.Torneo;
import Usuarios.Socio;
import Usuarios.Usuario;

public class MenuSocio {

    private Club club;
    private Socio socio;
    private Entrada entrada;

    public MenuSocio(Club club, Socio socio, Entrada entrada) {
        this.club = club;
        this.socio = socio;
        this.entrada = entrada;
    }

    public void mostrar() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            System.out.println("===== MENÚ SOCIO: " + socio.getLogin() + " =====");
            System.out.println("1. Comprar en la tienda");
            System.out.println("2. Comprar en la cafetería");
            System.out.println("3. Reservar una instalación");
            System.out.println("4. Inscribirme a una disciplina individual");
            System.out.println("5. Inscribirme a un equipo");
            System.out.println("6. Ver si estoy habilitado para jugar");
            System.out.println("7. Ver mis estadísticas");
            System.out.println("8. Ver mi historial de compras");
            System.out.println("9. Ver mis puntos");
            System.out.println("10. Ver torneos");
            System.out.println("11. Inscribirme a un torneo");
            System.out.println("12. Retirarme de un torneo");
            System.out.println("0. Salir");
            opcion = entrada.leerEntero("Opción: ", 0, 12);

            try {
                if (opcion == 1) {
                    comprarEnTienda();
                } else if (opcion == 2) {
                    comprarEnCafeteria();
                } else if (opcion == 3) {
                    reservarInstalacion();
                } else if (opcion == 4) {
                    inscribirseEnDisciplina();
                } else if (opcion == 5) {
                    inscribirseEnEquipo();
                } else if (opcion == 6) {
                    verHabilitacion();
                } else if (opcion == 7) {
                    verEstadisticas();
                } else if (opcion == 8) {
                    verHistorial();
                } else if (opcion == 9) {
                    System.out.println("Tienes " + socio.getPuntosFidelidad() + " puntos.");
                } else if (opcion == 10) {
                    verTorneos();
                } else if (opcion == 11) {
                    inscribirseEnTorneo();
                } else if (opcion == 12) {
                    retirarseDeTorneo();
                }
            } catch (RuntimeException e) {
                System.out.println("No se pudo completar: " + e.getMessage());
            }
        }
    }

    // Cafetería 

    private void comprarEnCafeteria() {
        Cafeteria cafe = club.getCafeteria();
        List<Bebida> bebidas = cafe.getBebidas();
        List<Snack> snacks = cafe.getSnacks();

        List<Bebida> bebidasElegidas = new ArrayList<>();
        List<Snack> snacksElegidos = new ArrayList<>();

        System.out.println("--- Bebidas ---");
        for (int i = 0; i < bebidas.size(); i++) {
            Bebida b = bebidas.get(i);
            String tipo = b.isCaliente() ? "caliente" : "fría";
            System.out.println((i + 1) + ". " + b.getNombre() + " (" + tipo + ") $" + b.getPrecio());
        }
        int eleccion = -1;
        while (eleccion != 0) {
            eleccion = entrada.leerEntero("Número de bebida (0 para terminar): ", 0, bebidas.size());
            if (eleccion != 0) {
                bebidasElegidas.add(bebidas.get(eleccion - 1));
            }
        }

        System.out.println("--- Snacks ---");
        for (int i = 0; i < snacks.size(); i++) {
            Snack s = snacks.get(i);
            System.out.println((i + 1) + ". " + s.getNombre() + " $" + s.getPrecio());
        }
        eleccion = -1;
        while (eleccion != 0) {
            eleccion = entrada.leerEntero("Número de snack (0 para terminar): ", 0, snacks.size());
            if (eleccion != 0) {
                snacksElegidos.add(snacks.get(eleccion - 1));
            }
        }

        if (bebidasElegidas.isEmpty() && snacksElegidos.isEmpty()) {
            System.out.println("No elegiste nada.");
            return;
        }

        boolean confirmados = true;
        List<String> avisos = cafe.informarAlergenos(snacksElegidos);
        if (!avisos.isEmpty()) {
            System.out.println("ATENCIÓN:");
            for (String aviso : avisos) {
                System.out.println(" - " + aviso);
            }
            confirmados = entrada.leerSiNo("¿Confirmas que quieres comprar estos snacks?");
            if (!confirmados) {
                System.out.println("Compra cancelada.");
                return;
            }
        }

        double subtotal = 0;
        for (Bebida b : bebidasElegidas) {
            subtotal += b.getPrecio();
        }
        for (Snack s : snacksElegidos) {
            subtotal += s.getPrecio();
        }
        System.out.println("Propina sugerida (10%): $" + cafe.calcularPropinaSugerida(subtotal));
        double propina = entrada.leerDecimal("Propina que quieres dar: ", 0);

        Venta venta = cafe.vender(socio, bebidasElegidas, snacksElegidos, propina, confirmados,
                LocalDateTime.now());
        System.out.println("Compra realizada. Total: $" + venta.calcularTotal());
        System.out.println("Puntos actuales: " + socio.getPuntosFidelidad());
    }

    // Tienda 

    private void comprarEnTienda() {
        TiendaDeportiva tienda = club.getTienda();
        List<Articulo> articulos = tienda.getArticulosDisponibles();

        System.out.println("--- Artículos ---");
        for (int i = 0; i < articulos.size(); i++) {
            Articulo a = articulos.get(i);
            System.out.println((i + 1) + ". " + a.getNombre() + " | $" + a.getPrecio()
                    + " | disponibles: " + a.getCantidadDisponible() + " | " + a.getUbicacion());
        }

        Map<Articulo, Integer> carrito = new HashMap<>();
        int eleccion = -1;
        while (eleccion != 0) {
            eleccion = entrada.leerEntero("Número de artículo (0 para terminar): ", 0, articulos.size());
            if (eleccion != 0) {
                Articulo a = articulos.get(eleccion - 1);
                int cantidad = entrada.leerEntero("Cantidad: ", 1, 1000);
                int yaEnCarrito = 0;
                if (carrito.containsKey(a)) {
                    yaEnCarrito = carrito.get(a);
                }
                carrito.put(a, yaEnCarrito + cantidad);
            }
        }
        if (carrito.isEmpty()) {
            System.out.println("No elegiste nada.");
            return;
        }

        String codigo = null;
        int puntos = 0;
        if (entrada.leerSiNo("¿Tienes un código de descuento?")) {
            codigo = entrada.leerTexto("Código: ");
        } else if (socio.getPuntosFidelidad() > 0
                && entrada.leerSiNo("Tienes " + socio.getPuntosFidelidad() + " puntos. ¿Quieres usarlos?")) {
            puntos = entrada.leerEntero("Puntos a usar: ", 1, socio.getPuntosFidelidad());
        }

        Venta venta = tienda.vender(socio, carrito, codigo, puntos, club.getEmpleados(), LocalDate.now());
        System.out.println("Compra realizada.");
        System.out.println("Subtotal: $" + venta.getValor() + " | Descuento: $" + venta.getDescuento()
                + " | IVA: $" + venta.getImpuesto());
        System.out.println("Total: $" + venta.calcularTotal());
        System.out.println("Puntos actuales: " + socio.getPuntosFidelidad());
    }

    // Reservas 

    private void reservarInstalacion() {
        List<Instalacion> lista = new ArrayList<>(club.getInstalaciones());
        System.out.println("--- Instalaciones ---");
        for (int i = 0; i < lista.size(); i++) {
            Instalacion ins = lista.get(i);
            System.out.println((i + 1) + ". " + ins.getNombre() + " (" + ins.getTipo()
                    + ", capacidad " + ins.getCapacidad() + ")");
        }
        int eleccion = entrada.leerEntero("Número de instalación (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return;
        }
        Instalacion elegida = lista.get(eleccion - 1);

        LocalDateTime inicio = entrada.leerFechaHora("Inicio");
        LocalDateTime fin = entrada.leerFechaHora("Fin");
        System.out.println("1. SENCILLOS");
        System.out.println("2. DOBLES");
        int mod = entrada.leerEntero("Modalidad: ", 1, 2);
        String modalidad = "SENCILLOS";
        if (mod == 2) {
            modalidad = "DOBLES";
        }
        int jugadores = entrada.leerEntero("Número de jugadores: ", 1, 4);

        socio.reservarInstalacion(elegida, inicio, fin, jugadores, modalidad);
        System.out.println("Reserva realizada en " + elegida.getNombre() + ".");
    }

    // Inscripciones

    private void inscribirseEnDisciplina() {
        List<DisciplinaIndividual> lista = new ArrayList<>(club.getDisciplinas());
        System.out.println("--- Disciplinas ---");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i).getNombre());
        }
        int eleccion = entrada.leerEntero("Número de disciplina (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return;
        }
        System.out.println("1. Principiante");
        System.out.println("2. Intermedio");
        System.out.println("3. Avanzado");
        int n = entrada.leerEntero("Tu nivel: ", 1, 3);
        String nivel = "Principiante";
        if (n == 2) {
            nivel = "Intermedio";
        } else if (n == 3) {
            nivel = "Avanzado";
        }
        socio.inscribirseEnDisciplina(lista.get(eleccion - 1), nivel);
        System.out.println("Inscripción realizada en " + lista.get(eleccion - 1).getNombre() + ".");
    }

    private void inscribirseEnEquipo() {
        List<Equipo> lista = new ArrayList<>(club.getEquipos());
        System.out.println("--- Equipos ---");
        for (int i = 0; i < lista.size(); i++) {
            Equipo e = lista.get(i);
            System.out.println((i + 1) + ". " + e.getNombre() + " (" + e.getDeporte().getNombre()
                    + ", " + e.getCategoria().getNombre() + ")");
        }
        int eleccion = entrada.leerEntero("Número de equipo (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return;
        }
        String posicion = entrada.leerTexto("Posición (por ejemplo: portero, delantero): ");
        int camiseta = entrada.leerEntero("Número de camiseta: ", 1, 99);
        socio.inscribirseEnEquipo(lista.get(eleccion - 1), posicion, camiseta);
        System.out.println("Quedaste inscrito en " + lista.get(eleccion - 1).getNombre() + ".");
    }

    // Consultas 

    private void verHabilitacion() {
        if (socio.getComposicion().isEmpty()) {
            System.out.println("No estás inscrito en ningún equipo.");
            return;
        }
        for (ComposicionEquipo c : socio.getComposicion()) {
            DeporteConjunto deporte = c.getEquipo().getDeporte();
            String estado = socio.estaHabilitadoParaJugar(deporte) ? "HABILITADO" : "NO HABILITADO";
            System.out.println(deporte.getNombre() + " (" + c.getEquipo().getNombre() + "): " + estado);
        }
    }

    private void verEstadisticas() {
        for (DeporteConjunto d : club.getDeportes()) {
            List<EstadisticaPartido> lista = socio.consultarEstadisticasPorDeporte(d);
            System.out.println(d.getNombre() + ": " + lista.size() + " partido(s) con estadísticas");
            for (EstadisticaPartido e : lista) {
                System.out.println("  " + e);
            }
        }
    }

    private void verHistorial() {
        if (socio.getHistorialCompras().isEmpty()) {
            System.out.println("Aún no tienes compras.");
            return;
        }
        for (Venta v : socio.getHistorialCompras()) {
            System.out.println(v.getFecha() + " | " + v.getRubro() + " | $" + v.calcularTotal());
        }
    }

    // Torneos

    private void verTorneos() {
        List<Torneo> lista = new ArrayList<>(club.getTorneos());
        if (lista.isEmpty()) {
            System.out.println("No hay torneos creados.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Torneo t = lista.get(i);
            System.out.println((i + 1) + ". " + t.getNombre() + " | " + t.getTipo() + " | "
                    + t.getModalidad() + " | " + t.getDeporte() + " " + t.getCategoria()
                    + " | " + t.getFecha() + " | " + t.getEstado()
                    + " | cupos libres: " + t.getCuposLibres() + "/" + t.getCupos()
                    + " | tarifa: $" + t.getTarifa());
        }
    }


    private Torneo elegirTorneo() {
        List<Torneo> lista = new ArrayList<>(club.getTorneos());
        if (lista.isEmpty()) {
            System.out.println("No hay torneos creados.");
            return null;
        }
        verTorneos();
        int eleccion = entrada.leerEntero("Número de torneo (0 para cancelar): ", 0, lista.size());
        if (eleccion == 0) {
            return null;
        }
        return lista.get(eleccion - 1);
    }

    private void inscribirseEnTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        if (torneo.getEstado() != EstadoTorneo.EN_INSCRIPCION) {
            System.out.println("Ese torneo ya no admite inscripciones.");
            return;
        }
        if (torneo.getModalidad() == Modalidad.CONJUNTO) {
            System.out.println("Ese torneo es de equipos: lo inscribe el administrador o el entrenador.");
            return;
        }

        Usuario companero = null;
        if (torneo.getModalidad() == Modalidad.DOBLES) {
            String login = entrada.leerTexto("Login de tu pareja: ");
            companero = club.buscarUsuario(login);
            if (companero == null) {
                System.out.println("No existe un usuario con ese login.");
                return;
            }
        }

        // mejorRanking PENDIENTE calcularlo en la lógica (ELO)
        torneo.inscribirElJugador(socio, companero, false);
        if (torneo.getTarifa() > 0) {
            System.out.println("Inscripción realizada. Tarifa: $" + torneo.getTarifa()
                    + " (el pago se gestiona fuera del sistema).");
        } else {
            System.out.println("Inscripción realizada.");
        }
    }

    private void retirarseDeTorneo() {
        Torneo torneo = elegirTorneo();
        if (torneo == null) {
            return;
        }
        torneo.retirarJugador(socio);
        System.out.println("Te retiraste de " + torneo.getNombre() + ". Se liberó tu cupo.");
    }
}