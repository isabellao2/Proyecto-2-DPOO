package Usuarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Excepciones.ClubException;
import TiendaCafeteria.Venta;

public abstract class Usuario {
	
	public static final double VALOR_PUNTO = 1; // CAMBIO 

	private String login;
	private String contraseña;
	private int puntosFidelidad;
	private List<Venta> historialdecompras;
	
	
	public Usuario(String login, String contraseña) {
		super();
		this.login = login;
		this.contraseña = contraseña;
		this.puntosFidelidad = 0;
		this.historialdecompras = new ArrayList<>();
	}

	public String getContraseña() {
		return contraseña;
	}


	public void setContraseña(String contraseña) {
		if (contraseña == null || contraseña.trim().isEmpty()) {

            throw new ClubException(" :( la contraseña no puede estar vacía.");
        }

        this.contraseña = contraseña;
	}
	


	public void setLogin(String login) {
		if (login == null || login.trim().isEmpty()) {

            throw new ClubException(" :( el login no puede estar vacío.");
        }

        this.login = login;
	}


	public String getLogin() {
		return login;
	}

	public int getPuntosFidelidad() {
		return puntosFidelidad;
	}
	
	public void setPuntosFidelidad(int puntosFidelidad) {
		if (puntosFidelidad <0) {
			throw new ClubException (" no puedes tener menos de 0 puntos");
		}
		this.puntosFidelidad = puntosFidelidad;
	}
	
	public List<Venta> getHistorialCompras() {
		return Collections.unmodifiableList(historialdecompras);
	}
	
	public boolean verificar (String login, String contraseña) {
		return this.login.equals(login) && this.contraseña.equals(contraseña);
	}
	
	// Punto de fidelidad CafeteriaTienda
	public void acumularPuntos (int punto) {
		if (punto >0) {
			puntosFidelidad += punto;}
	}
	public boolean tienePuntosSuficientes (int puntos) {
		if (puntos > 0 && puntosFidelidad >= puntos) {
			return true;
		}
		return false;
	}
	
	public int canjearPuntos (int puntos) {
		if (puntos <= 0) {
			throw new ClubException(" Indica una cantidad de puntos mayor que cero.");
		}
		if (!tienePuntosSuficientes(puntos)) {
			throw new ClubException(" Tienes " + puntosFidelidad
					+ " puntos y quieres redimir " + puntos);
		}
		puntosFidelidad -= puntos;
		return (int) ((puntos) * (VALOR_PUNTO));
	}
	
	public void registrarCompra(Venta v) throws ClubException {
		if (v == null) {
			throw new ClubException("no hay una venta para registrar.");
		}
		historialdecompras.add(v);
		acumularPuntos(v.calcularPuntosFidelidad());
	}

	public void cargarCompraGuardada(Venta v) {
		if (v != null) {
			historialdecompras.add(v);
		}
	}
}
