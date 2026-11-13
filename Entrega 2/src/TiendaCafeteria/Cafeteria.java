package TiendaCafeteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Usuarios.Socio;

public class Cafeteria {

	// Atributos
	private List<Bebida> bebidas;
	private List<Snack> snacks;
	private List<Venta> ventas;

	public static final double IMPUESTO_CONSUMO = 0.08;
	public static final double PROPINA_SUGERIDA = 0.10;

	// Constructor
	public Cafeteria() {
		this.bebidas = new ArrayList<>();
		this.snacks = new ArrayList<>();
		this.ventas = new ArrayList<>();
	}

	
	public Venta vender(Socio comprador, List<Bebida> bebidasAComprar, List<Snack> snacksAComprar,
			double propina, boolean alergenosConfirmados, LocalDateTime momento) {


		if (comprador == null) {
			throw new IllegalArgumentException("El comprador es obligatorio");
		}
		if (momento == null) {
			throw new IllegalArgumentException("El momento de la venta es obligatorio");
		}
		if (propina < 0) {
			throw new IllegalArgumentException("La propina no puede ser negativa");
		}

		List<Bebida> bs = bebidasAComprar;
		if (bs == null) {
    		bs = new ArrayList<>();
		}
		List<Snack> ss = snacksAComprar;
		if (ss == null) {
    	ss = new ArrayList<>();
		}
		if (bs.isEmpty() && ss.isEmpty()) {
    	throw new IllegalArgumentException("Debe incluir al menos un producto");
		}	

		for (Bebida b : bs) {
    	if (b == null || !bebidas.contains(b)) {
        throw new IllegalArgumentException("Bebida no disponible en la cafetería");
    		}
		}
		for (Snack s : ss) {
    	if (s == null || !snacks.contains(s)) {
        throw new IllegalArgumentException("Snack no disponible en la cafetería");
    		}
		}

		// Restriccion de la bebida caliente
		boolean hayCaliente = false;
		for (Bebida b : bebidas) {
			if (!puedeDespachar(b, comprador, momento)) {
				throw new IllegalArgumentException(
						"No se puede despachar bebida caliente a un socio que está en una instalación restringida");
			}
			if (b.isCaliente()) {
				hayCaliente = true;
			}
		}

		// Alérgenos se informan al socio 
		List<String> avisos = informarAlergenos(snacks);
		if (!avisos.isEmpty() && !alergenosConfirmados) {
			String mensaje = "El socio debe confirmar los alérgenos: ";
			for (String aviso : avisos) {
				mensaje += aviso + ". ";
			}
			throw new IllegalStateException(mensaje);
		}

		// Totales
		double subtotal = 0.0;
		for (Bebida b : bebidas) {
			subtotal += b.getPrecio();
		}
		for (Snack s : snacks) {
			subtotal += s.getPrecio();
		}
		double impuesto = calcularImpuestoConsumo(subtotal);

		// Crear la venta y actualiza el estado
		Venta venta = new Venta(momento.toLocalDate(), "CAFETERIA", subtotal, 0.0, impuesto, propina, comprador);

		if (hayCaliente) {
			comprador.registrarBebidaCaliente(momento);
		}
		comprador.registrarCompra(venta);
		ventas.add(venta);
		return venta;
	}

	// Restriccion de bebida caliente
	public boolean puedeDespachar(Bebida b, Socio s, LocalDateTime momento) {
		if (!b.isCaliente()) {
			return true;
		}
		return !s.estaEnInstalacionRestringida(momento);
	}

	// Metodos de Impuesto y propina
	public double calcularImpuestoConsumo(double subtotal) {
		return subtotal * IMPUESTO_CONSUMO;
	}

	public double calcularPropinaSugerida(double subtotal) {
		return subtotal * PROPINA_SUGERIDA;
	}

	public List<String> informarAlergenos(List<Snack> snacksAComprar) {
		List<String> reporteAlergenos = new ArrayList<>();

		if (snacksAComprar != null) {
			for (Snack s : snacksAComprar) {
				if (s != null && s.tieneAlergenos()) {
					reporteAlergenos.add("El snack " + s.getNombre() + " contiene alérgenos: " + s.getAlergenos());
				}
			}
		}

		return reporteAlergenos;
	}

	// Bebidas y snacks 
	public void agregarBebida(Bebida b) {
		if (b != null) {
			this.bebidas.add(b);
		}
	}

	public void agregarSnack(Snack s) {
		if (s != null) {
			this.snacks.add(s);
		}
	}
    
	// Reportes de ventas por periodo
	public List<Venta> getVentasEntre(LocalDate desde, LocalDate hasta) {
		List<Venta> resultado = new ArrayList<>();
		for (Venta v : ventas) {
			if (!v.getFecha().isBefore(desde) && !v.getFecha().isAfter(hasta)) {
				resultado.add(v);
			}
		}
		return resultado;
	}

	public List<Bebida> getBebidas() {
		return new ArrayList<>(bebidas);
	}

	public List<Snack> getSnacks() {
		return new ArrayList<>(snacks);
	}

	public List<Venta> getVentas() {
		return new ArrayList<>(ventas);
	}

	// persistencia
	public void cargarVentaGuardada(Venta v) {
		if (v != null) {
			ventas.add(v);
		}
	}
}