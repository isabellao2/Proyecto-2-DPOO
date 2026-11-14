package TiendaCafeteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Torneos.BonoDescuento;
import Usuarios.Entrenador;
import Usuarios.Empleado;
import Usuarios.Socio;
import TiendaCafeteria.Bebida;
import TiendaCafeteria.Snack;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.Venta;
import TiendaCafeteria.Cafeteria;
import TiendaCafeteria.TiendaDeportiva;


class TiendaCafeteriaTest {

    private static final double DELTA = 0.001;

    private TiendaDeportiva tienda;
    private Cafeteria cafeteria;
    private Articulo balon;
    private Articulo camiseta;
    private Socio socio;
    private Entrenador coach;
    private List<Empleado> empleados;

    @BeforeEach
    void preparar() {
        tienda = new TiendaDeportiva();
        balon = new Articulo("Balón", "Balones", 100000, 10);
        camiseta = new Articulo("Camiseta", "Ropa", 50000, 5);
        tienda.agregarArticulo(balon);
        tienda.agregarArticulo(camiseta);
        cafeteria = new Cafeteria();
        socio = new Socio("laura", "1234", 20);
        coach = new Entrenador("coach", "x", "COACH1");
        empleados = List.of(coach);
    }

    private Map<Articulo, Integer> carrito(Articulo a, int cantidad) {
        Map<Articulo, Integer> m = new LinkedHashMap<>();
        m.put(a, cantidad);
        return m;
    }

    @Test
    void elArticuloSabeSiEstaAgotadoYSeAbastece() {
        Articulo a = new Articulo("Cuerda", "Accesorios", 8000, 0);
        assertTrue(a.estaAgotado());
        a.abastecer(5);
        assertFalse(a.estaAgotado());
        assertEquals(5, a.getCantidadDisponible());
        assertThrows(IllegalArgumentException.class, () -> a.abastecer(0));
        assertThrows(IllegalArgumentException.class, () -> a.abastecer(-3));
    }

    @Test
    void elArticuloSeMueveDeUbicacion() {
        balon.mover("Tienda Piscina");
        assertEquals("Tienda Piscina", balon.getUbicacion());
        assertThrows(IllegalArgumentException.class, () -> balon.mover(" "));
        assertThrows(IllegalArgumentException.class, () -> balon.mover(null));
    }



    @Test
    void laCompraEnTiendaPagaIvaDel19PorCiento() {
        Venta v = tienda.vender(socio, carrito(balon, 1), null, 0, empleados);
        assertEquals(100000, v.getValor(), DELTA);
        assertEquals(19000, v.getImpuesto(), DELTA);
        assertEquals(119000, v.calcularTotal(), DELTA);
        assertEquals("TIENDA", v.getRubro());
        assertEquals(LocalDate.now(), v.getFecha());
    }

    @Test
    void laVentaDescuentaInventarioYSumaPuntosAlComprador() {
        tienda.vender(socio, carrito(balon, 3), null, 0, empleados);
        assertEquals(7, balon.getCantidadDisponible());
        assertEquals(357, socio.getPuntosFidelidad()); 
        assertEquals(1, socio.getHistorialCompras().size());
    }

    @Test
    void sumaVariosArticulosEnUnaCompra() {
        Map<Articulo, Integer> c = new LinkedHashMap<>();
        c.put(balon, 1);
        c.put(camiseta, 2);
        Venta v = tienda.vender(socio, c, null, 0, empleados);
        assertEquals(200000, v.getValor(), DELTA);
    }



    @Test
    void elCodigoDeUnEmpleadoDa8PorCientoAUnSocioY15AOtroEmpleado() {
        Venta deSocio = tienda.vender(socio, carrito(balon, 1), "COACH1", 0, empleados);
        assertEquals(8000, deSocio.getDescuento(), DELTA);
        Entrenador otro = new Entrenador("otro", "x", "COACH2");
        Venta deEmpleado = tienda.vender(otro, carrito(balon, 1), "COACH1", 0, empleados);
        assertEquals(15000, deEmpleado.getDescuento(), DELTA);
    }

    @Test
    void elIvaSeCobraSobreElValorYaDescontado() {
        Venta v = tienda.vender(socio, carrito(balon, 1), "COACH1", 0, empleados);
        assertEquals((100000 - 8000) * 0.19, v.getImpuesto(), DELTA);
    }

    @Test
    void rechazaUnCodigoQueNoExiste() {
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), "FALSO", 0, empleados));
        assertEquals(10, balon.getCantidadDisponible());
    }

    @Test
    void canjeaPuntosComoDescuento() {
        socio.setPuntosFidelidad(200);
        Venta v = tienda.vender(socio, carrito(balon, 1), null, 100, empleados);
        assertEquals(5000, v.getDescuento(), DELTA); // 100 puntos x $50
        assertEquals(100 + 113, socio.getPuntosFidelidad()); // quedan 100 y suma los de esta compra (total 113.050)
    }

    @Test
    void elDescuentoPorPuntosNuncaSuperaElSubtotal() {
        Articulo barato = new Articulo("Silbato", "Accesorios", 2000, 5);
        tienda.agregarArticulo(barato);
        socio.setPuntosFidelidad(1000);
        Venta v = tienda.vender(socio, carrito(barato, 1), null, 1000, empleados);
        assertEquals(2000, v.getDescuento(), DELTA);
    }

    @Test
    void elCodigoYLosPuntosNoSeAcumulan() {
        socio.setPuntosFidelidad(100);
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), "COACH1", 50, empleados));
        assertEquals(100, socio.getPuntosFidelidad());
    }

    @Test
    void rechazaCanjearMasPuntosDeLosQueTiene() {
        socio.setPuntosFidelidad(10);
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), null, 50, empleados));
    }



    @Test
    void rechazaVentasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(null, carrito(balon, 1), null, 0, empleados));
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, new LinkedHashMap<>(), null, 0, empleados));
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 0), null, 0, empleados));
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 11), null, 0, empleados));
        Articulo ajeno = new Articulo("Raqueta", "Tenis", 90000, 3);
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(ajeno, 1), null, 0, empleados));
    }

    @Test
    void noSePermiteAgregarDosArticulosConElMismoNombre() {
        assertThrows(IllegalArgumentException.class, () -> tienda.agregarArticulo(new Articulo("balón", "X", 1, 1)));
        assertThrows(IllegalArgumentException.class, () -> tienda.agregarArticulo(null));
    }

    @Test
    void elSnackRegistraSusAlergenos() {
        Snack s = new Snack("Barra de maní", 4000);
        assertFalse(s.tieneAlergenos());
        s.agregarAlergeno("maní");
        assertTrue(s.tieneAlergenos());
        assertEquals(List.of("maní"), s.getAlergenos());
    }

    private List<Bebida> bebidas(Bebida... b) {
        return new ArrayList<>(List.of(b));
    }

    @Test
    void laCafeteriaCobraImpuestoAlConsumoDel8PorCiento() {
        Venta v = cafeteria.vender(socio, bebidas(new Bebida("Jugo", 6000, false)), null, 0);
        assertEquals(6000, v.getValor(), DELTA);
        assertEquals(480, v.getImpuesto(), DELTA);
        assertEquals(6480, v.calcularTotal(), DELTA);
        assertEquals("CAFETERIA", v.getRubro());
    }

    @Test
    void sumaBebidasSnacksYPropina() {
        Venta v = cafeteria.vender(socio, bebidas(new Bebida("Jugo", 6000, false)),
                new ArrayList<>(List.of(new Snack("Galletas", 3000))), 900);
        assertEquals(9000, v.getValor(), DELTA);
        assertEquals(900, v.getPropina(), DELTA);
        assertEquals(9000 + 720 + 900, v.calcularTotal(), DELTA);
        assertEquals(900, cafeteria.calcularPropinaSugerida(9000), DELTA);
    }

    @Test
    void rechazaVentasInvalidasEnLaCafeteria() {
        assertThrows(IllegalArgumentException.class, () -> cafeteria.vender(null, bebidas(new Bebida("Jugo", 6000, false)), null, 0));
        assertThrows(IllegalArgumentException.class, () -> cafeteria.vender(socio, null, null, 0));
        assertThrows(IllegalArgumentException.class, () -> cafeteria.vender(socio, bebidas(new Bebida("Jugo", 6000, false)), null, -1));
    }

    @Test
    void unaBebidaCalienteMarcaAlSocioComoQuienLaLleva() {
        assertFalse(socio.isBebidaCalienteActiva());
        cafeteria.vender(socio, bebidas(new Bebida("Café", 4000, true)), null, 0);
        assertTrue(socio.isBebidaCalienteActiva());
    }

    @Test
    void unaBebidaFriaNoMarcaAlSocio() {
        cafeteria.vender(socio, bebidas(new Bebida("Agua", 2000, false)), null, 0);
        assertFalse(socio.isBebidaCalienteActiva());
    }

    @Test
    void siNoEstaEnUnaInstalacionSeLeDespachaCualquierBebida() {
        assertTrue(cafeteria.puedeDespachar(new Bebida("Café", 4000, true), socio));
        assertTrue(cafeteria.puedeDespachar(new Bebida("Agua", 2000, false), socio));
    }


    @Test
    void laVentaCalculaTotalRubroYPuntos() {
        Venta v = new Venta(LocalDate.now(), "TIENDA", 100000, 10000, 17100, 0, socio);
        assertEquals(107100, v.calcularTotal(), DELTA);
        assertTrue(v.esRubro("tienda"));
        assertFalse(v.esRubro("cafeteria"));
        assertFalse(v.esRubro(null));
        assertEquals(2142, v.calcularPuntosFidelidad(v.calcularTotal()));
        assertNotNull(v.getComprador());
    }
    
    @Test
    void elBonoDaVeintePorCientoYSeConsume() {
        BonoDescuento bono = new BonoDescuento("Copa", BonoDescuento.PORCENTAJE_DEFECTO);
        socio.agregarBonoDescuento(bono);
        // Pasamos 'bono' y 'LocalDate.now()' en lugar de 'true'
        Venta v = tienda.vender(socio, carrito(balon, 1), null, 0, bono, empleados, LocalDate.now());
        assertEquals(20000, v.getDescuento(), DELTA);
        assertEquals((100000 - 20000) * 1.19, v.calcularTotal(), DELTA);
        assertTrue(bono.isUsado());
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), null, 0, bono, empleados, LocalDate.now()));
    }

    @Test
    void elBonoNoSeAcumulaConCodigoNiConPuntosYNoSeGastaEnElIntento() {
        BonoDescuento bono = new BonoDescuento(0.2, "Copa");
        socio.agregarBonoDescuento(bono);
        socio.setPuntosFidelidad(100);
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), "COACH1", 0, bono, empleados, LocalDate.now()));
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), null, 50, bono, empleados, LocalDate.now()));
        assertFalse(bono.isUsado());
        assertEquals(10, balon.getCantidadDisponible());
    }

    @Test
    void sinBonoNoSePuedeUsarUno() {
        // Si no tiene bono, pasamos null
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 1), null, 0, null, empleados, LocalDate.now()));
    }

    @Test
    void siLaVentaFallaPorStockElBonoSigueDisponible() {
        BonoDescuento bono = new BonoDescuento(0.2, "Copa");
        socio.agregarBonoDescuento(bono);
        assertThrows(IllegalArgumentException.class, () -> tienda.vender(socio, carrito(balon, 99), null, 0, bono, empleados, LocalDate.now()));
        assertFalse(bono.isUsado());
    }
    
}