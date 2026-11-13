package Consolas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Usuario;
import Usuarios.Empleado;
import TiendaCafeteria.TiendaDeportiva;
import TiendaCafeteria.Articulo;
import TiendaCafeteria.Venta;

public class PruebaVentaEnLaTienda {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("TIENDA DEPORTIVA");

            Club datos = new Club();
            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodo(datos, "datos");

            System.out.println("Datos cargados correctamente.");
            System.out.println(datos.getTienda().getArticulosDisponibles().size() + " articulos en catalogo.");

            System.out.println("\nINICIO DE SESION");
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Contrasena: ");
            String contrasena = scanner.nextLine();

            Usuario usuario = datos.buscarUsuario(login);

            if (usuario == null || !usuario.verificar(login, contrasena)) {
                System.out.println("Login o contrasena incorrectos.");
                return;
            }

            System.out.println("Bienvenido, " + usuario.getLogin());
            System.out.println("Puntos actuales: " + usuario.getPuntosFidelidad());

            boolean salir = false;

            while (!salir) {
                System.out.println("\nMENU");
                System.out.println("1. Realizar una compra");
                System.out.println("2. Ver mi historial de compras");
                System.out.println("0. Salir");

                int opcion = leerEntero(scanner, "Opcion", 0, 2);

                if (opcion == 1) {
                    realizarCompra(scanner, datos, usuario);
                } else if (opcion == 2) {
                    mostrarHistorial(usuario);
                } else {
                    salir = true;
                }
            }

            System.out.println("Guardando datos...");
            persistencia.guardarTodo(datos, "datos");

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }
    }

    // ---------- Opciones del menu ----------

    private static void realizarCompra(Scanner scanner, Club datos, Usuario usuario) {
        TiendaDeportiva tienda = datos.getTienda();
        List<Articulo> catalogo = tienda.getArticulosDisponibles();

        if (catalogo.isEmpty()) {
            System.out.println("No hay articulos cargados.");
            return;
        }

        Map<Articulo, Integer> carrito = new HashMap<>();
        boolean agregando = true;

        System.out.println("\nARTICULOS DISPONIBLES");
        for (int i = 0; i < catalogo.size(); i++) {
            Articulo art = catalogo.get(i);
            System.out.println((i + 1) + ". " + art.getNombre() + " | " + art.getCategoria() 
                    + " | Precio: $" + art.getPrecio() + " | Stock: " + art.getCantidadDisponible());
        }

        while (agregando) {
            int numArt = leerEntero(scanner, "\nSeleccione el articulo (0 para terminar)", 0, catalogo.size());
            
            if (numArt == 0) {
                agregando = false;
                continue;
            }

            Articulo artSel = catalogo.get(numArt - 1);
            int cantidad = leerEntero(scanner, "Cantidad a llevar", 1, 100);

            carrito.put(artSel, carrito.getOrDefault(artSel, 0) + cantidad);
            System.out.println("Agregado al carrito.");
        }

        if (carrito.isEmpty()) {
            System.out.println("No se seleccionaron articulos. Cancelando compra.");
            return;
        }

        System.out.print("\n¿Tiene un codigo de descuento? (S/N): ");
        String tieneCodigo = scanner.nextLine().trim();
        String codigo = null;
        if (tieneCodigo.equalsIgnoreCase("S")) {
            System.out.print("Ingrese el codigo: ");
            codigo = scanner.nextLine().trim();
        }

        int puntos = 0;
        if (usuario.getPuntosFidelidad() > 0) {
            System.out.print("¿Cuantos puntos desea redimir? (Tiene " + usuario.getPuntosFidelidad() + "): ");
            puntos = leerEntero(scanner, "Puntos", 0, usuario.getPuntosFidelidad());
        }

        List<Empleado> empleados = obtenerEmpleados(datos);

        System.out.println("\nESTADO ANTES");
        System.out.println("Puntos del usuario: " + usuario.getPuntosFidelidad());
        for (Map.Entry<Articulo, Integer> item : carrito.entrySet()) {
            System.out.println("Stock de " + item.getKey().getNombre() + ": " + item.getKey().getCantidadDisponible());
        }

        try {
            Venta venta = tienda.vender(usuario, carrito, codigo, puntos, empleados, LocalDate.now());
            
            System.out.println("\nVENTA APROBADA");
            System.out.println("Fecha: " + venta.getFecha());
            System.out.println("Subtotal: $" + String.format("%.2f", venta.getValor()));
            System.out.println("Descuento: $" + String.format("%.2f", venta.getDescuento()));
            System.out.println("IVA (19%): $" + String.format("%.2f", venta.getImpuesto()));
            System.out.println("Total pagado: $" + String.format("%.2f", venta.calcularTotal()));
            System.out.println("Puntos ganados: " + venta.calcularPuntosFidelidad());
            
            persistenciaGuardarTemporal(datos, "datos"); // Guardado rapido interno

        } catch (IllegalArgumentException e) {
            System.out.println("\nVENTA RECHAZADA: " + e.getMessage());
        }

        System.out.println("\nESTADO DESPUES");
        System.out.println("Puntos del usuario: " + usuario.getPuntosFidelidad());
        for (Map.Entry<Articulo, Integer> item : carrito.entrySet()) {
            System.out.println("Stock de " + item.getKey().getNombre() + ": " + item.getKey().getCantidadDisponible());
        }
    }

    private static void mostrarHistorial(Usuario usuario) {
        System.out.println("\nHistorial de compras de " + usuario.getLogin() + ":");
        List<Venta> historial = usuario.getHistorialCompras();
        
        if (historial.isEmpty()) {
            System.out.println("   (ninguna)");
            return;
        }

        for (Venta v : historial) {
            System.out.println("   " + v.getFecha() + " | " + v.getRubro() 
                    + " | Total: $" + String.format("%.2f", v.calcularTotal()));
        }
    }

    // ---------- Auxiliares ----------

    private static int leerEntero(Scanner scanner, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje + " [" + min + "-" + max + "]: ");
            try {
                int n = Integer.parseInt(scanner.nextLine().trim());
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // cae al mensaje de abajo
            }
            System.out.println("Escribe un numero entre " + min + " y " + max + ".");
        }
    }

    private static List<Empleado> obtenerEmpleados(Club datos) {
        List<Empleado> empleados = new ArrayList<>();
        for (Usuario u : datos.getUsuarios()) {
            if (u instanceof Empleado) {
                empleados.add((Empleado) u);
            }
        }
        return empleados;
    }
    
    // Metodo puente para guardar sin tener que pasar el objeto persistencia a todos lados
    private static void persistenciaGuardarTemporal(Club datos, String carpeta) {
        try {
            new PersistenciaCentral().guardarTodo(datos, carpeta);
        } catch (Exception e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }
}