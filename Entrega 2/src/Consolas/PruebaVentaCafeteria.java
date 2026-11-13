package Consolas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DeporteIndividual.Reserva;
import TiendaCafeteria.Bebida;
import TiendaCafeteria.Cafeteria;
import TiendaCafeteria.Snack;
import TiendaCafeteria.Venta;
import club.Club;
import persistencias.PersistenciaCentral;
import Usuarios.Socio;
import Usuarios.Usuario;

public class PruebaVentaCafeteria {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("VENTA EN LA CAFETERIA");

            String ruta = "datos";
            Club datos = new Club();
            PersistenciaCentral persistencia = new PersistenciaCentral();
            persistencia.cargarTodo(datos, ruta);

            Cafeteria cafeteria = datos.getCafeteria();

            System.out.println("Datos cargados correctamente.");
            System.out.println(cafeteria.getBebidas().size() + " bebidas, "
                    + cafeteria.getSnacks().size() + " snacks.");

            System.out.println("\nINICIO DE SESION DEL SOCIO");
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Contrasena: ");
            String contrasena = scanner.nextLine();

            Usuario usuario = datos.buscarUsuario(login);

            if (usuario == null || !usuario.verificar(login, contrasena)) {
                System.out.println("Login o contrasena incorrectos.");
                return;
            }

            if (!(usuario instanceof Socio)) {
                System.out.println("El usuario no es un socio.");
                return;
            }

            Socio socio = (Socio) usuario;
            System.out.println("Bienvenido, " + socio.getLogin());

            boolean salir = false;

            while (!salir) {
                System.out.println("\nMENU");
                System.out.println("1. Ver la carta de la cafeteria");
                System.out.println("2. Comprar");
                System.out.println("3. Ver mi estado");
                System.out.println("0. Salir y guardar");

                int opcion = leerEntero(scanner, "Opcion", 0, 3);

                if (opcion == 1) {
                    mostrarCarta(cafeteria);
                } else if (opcion == 2) {
                    comprar(scanner, cafeteria, socio);
                } else if (opcion == 3) {
                    mostrarEstado(socio);
                } else {
                    salir = true;
                }
            }

            persistencia.guardarTodo(datos, ruta);
            System.out.println("\nCambios guardados correctamente.");

        } catch (Exception e) {
            System.out.println("Ocurrio un error: " + e.getMessage());
        }
    }

    //opciones del menu

    private static void mostrarCarta(Cafeteria cafeteria) {

        System.out.println("\nBEBIDAS");

        List<Bebida> bebidas = cafeteria.getBebidas();

        for (int i = 0; i < bebidas.size(); i++) {
            Bebida b = bebidas.get(i);

            System.out.println((i + 1) + ". " + b.getNombre()
                    + " | " + dinero(b.getPrecio())
                    + " | " + (b.isCaliente() ? "caliente" : "fria"));
        }

        System.out.println("\nSNACKS");

        List<Snack> snacks = cafeteria.getSnacks();

        for (int i = 0; i < snacks.size(); i++) {
            Snack s = snacks.get(i);

            System.out.println((i + 1) + ". " + s.getNombre()
                    + " | " + dinero(s.getPrecio())
                    + " | alergenos: "
                    + (s.tieneAlergenos() ? s.getAlergenos().toString() : "ninguno"));
        }
    }

    private static void comprar(Scanner scanner, Cafeteria cafeteria, Socio socio) {

        List<Bebida> catalogoBebidas = cafeteria.getBebidas();
        List<Snack> catalogoSnacks = cafeteria.getSnacks();

        List<Bebida> bebidas = new ArrayList<Bebida>();
        List<Snack> snacks = new ArrayList<Snack>();

        //Armar el pedido
        boolean agregando = true;

        while (agregando) {

            System.out.println("\nAGREGAR AL PEDIDO");
            System.out.println("1. Bebida");
            System.out.println("2. Snack");
            System.out.println("0. Terminar el pedido");

            int tipo = leerEntero(scanner, "Opcion", 0, 2);

            if (tipo == 1 && !catalogoBebidas.isEmpty()) {

                for (int i = 0; i < catalogoBebidas.size(); i++) {
                    Bebida b = catalogoBebidas.get(i);

                    System.out.println((i + 1) + ". " + b.getNombre()
                            + " | " + dinero(b.getPrecio())
                            + (b.isCaliente() ? " | caliente" : ""));
                }

                Bebida elegida = catalogoBebidas.get(
                        leerEntero(scanner, "Bebida", 1, catalogoBebidas.size()) - 1);

                int cantidad = leerEntero(scanner, "Cantidad", 1, 10);

                for (int i = 0; i < cantidad; i++) {
                    bebidas.add(elegida);
                }

            } else if (tipo == 2 && !catalogoSnacks.isEmpty()) {

                for (int i = 0; i < catalogoSnacks.size(); i++) {
                    Snack s = catalogoSnacks.get(i);

                    System.out.println((i + 1) + ". " + s.getNombre()
                            + " | " + dinero(s.getPrecio()));
                }

                Snack elegido = catalogoSnacks.get(
                        leerEntero(scanner, "Snack", 1, catalogoSnacks.size()) - 1);

                int cantidad = leerEntero(scanner, "Cantidad", 1, 10);

                for (int i = 0; i < cantidad; i++) {
                    snacks.add(elegido);
                }

            } else if (tipo == 0) {
                agregando = false;
            }
        }

        if (bebidas.isEmpty() && snacks.isEmpty()) {
            System.out.println("El pedido esta vacio.");
            return;
        }

        //Calcular subtotal

        double subtotal = 0.0;

        for (Bebida b : bebidas) {
            subtotal += b.getPrecio();
        }

        for (Snack s : snacks) {
            subtotal += s.getPrecio();
        }

        System.out.println("\nSubtotal del pedido: " + dinero(subtotal));

        // 3. Seleccionar propina

        double sugerida = cafeteria.calcularPropinaSugerida(subtotal);

        System.out.println("\nPROPINA");
        System.out.println("Subtotal de la compra: " + dinero(subtotal));
        System.out.println("Propina sugerida: " + dinero(sugerida));
        System.out.println("Total con propina sugerida: "
                + dinero(subtotal + sugerida));

        double propina = leerPropina(scanner, sugerida);

        System.out.println("\nRESUMEN DE LA PROPINA");
        System.out.println("Subtotal: " + dinero(subtotal));
        System.out.println("Propina seleccionada: " + dinero(propina));
        System.out.println("Total antes de impuestos: "
                + dinero(subtotal + propina));

        //Alergenos

        List<String> avisos = cafeteria.informarAlergenos(snacks);

        boolean confirmados = false;

        if (!avisos.isEmpty()) {

            System.out.println("\nALERGENOS");

            for (String aviso : avisos) {
                System.out.println("- " + aviso);
            }

            confirmados = leerSiNo(scanner,
                    "Confirmas que quieres comprar estos snacks?");
        }

        //Momento de la compra

        LocalDateTime momento = leerMomento(scanner);

        System.out.println("\nESTADO ANTES");
        mostrarEstado(socio);

        try {

            Venta venta = cafeteria.vender(
                    socio, bebidas, snacks, propina, confirmados, momento);

            System.out.println("\nVENTA ACEPTADA");
            System.out.println("Fecha:     " + venta.getFecha());
            System.out.println("Rubro:     " + venta.getRubro());
            System.out.println("Subtotal:  " + dinero(venta.getValor()));
            System.out.println("Impuesto:  " + dinero(venta.getImpuesto()) + " (8%)");
            System.out.println("Propina:   " + dinero(venta.getPropina()));
            System.out.println("Total:     " + dinero(venta.calcularTotal()));
            System.out.println("Puntos ganados: "
                    + venta.calcularPuntosFidelidad());

        } catch (RuntimeException e) {

            System.out.println("\nVENTA RECHAZADA: " + e.getMessage());
        }

        System.out.println("\nESTADO DESPUES");
        mostrarEstado(socio);
    }

    private static void mostrarEstado(Socio socio) {

        System.out.println("Puntos de fidelidad: "
                + socio.getPuntosFidelidad());

        System.out.println("Ultima bebida caliente: "
                + (socio.getUltimaBebidaCaliente() == null
                ? "ninguna" : socio.getUltimaBebidaCaliente()));

        if (socio.getReservas().isEmpty()) {
            System.out.println("Reservas: (ninguna)");
        }

        for (Reserva r : socio.getReservas()) {
            System.out.println("Reserva: "
                    + r.getInstalacion().getNombre() + " de "
                    + r.getInicio() + " a " + r.getFin());
        }
    }

    private static String dinero(double valor) {
        return "$" + String.format("%.2f", valor);
    }

    private static double leerPropina(Scanner scanner, double sugerida) {

        while (true) {

            System.out.println("\n¿COMO DESEAS MANEJAR LA PROPINA?");
            System.out.println("1. Aceptar propina sugerida: " + dinero(sugerida));
            System.out.println("2. Escribir otro valor");
            System.out.println("3. No dejar propina");

            int opcion = leerEntero(scanner, "Opcion", 1, 3);

            if (opcion == 1) {
                return sugerida;

            } else if (opcion == 2) {

                System.out.print("Escribe el valor de la propina: ");

                try {
                    double valor = Double.parseDouble(
                            scanner.nextLine().trim().replace(',', '.'));

                    return valor;

                } catch (NumberFormatException e) {
                    System.out.println("Escribe un numero valido.");
                }

            } else {
                return 0.0;
            }
        }
    }

    private static LocalDateTime leerMomento(Scanner scanner) {

        while (true) {

            System.out.print(
                    "Fecha y hora de la compra (AAAA-MM-DD HH:mm, Enter = ahora): ");

            String texto = scanner.nextLine().trim();

            if (texto.isEmpty()) {
                return LocalDateTime.now();
            }

            try {
                return LocalDateTime.parse(texto.replace(' ', 'T'));

            } catch (Exception e) {
                System.out.println("Formato incorrecto.");
            }
        }
    }

    private static int leerEntero(
            Scanner scanner, String mensaje, int min, int max) {

        while (true) {

            System.out.print(mensaje + " [" + min + "-" + max + "]: ");

            try {
                int n = Integer.parseInt(scanner.nextLine().trim());

                if (n >= min && n <= max) {
                    return n;
                }

            } catch (NumberFormatException e) {
               
            }

            System.out.println(
                    "Escribe un numero entre " + min + " y " + max + ".");
        }
    }

    private static boolean leerSiNo(Scanner scanner, String mensaje) {

        while (true) {

            System.out.print(mensaje + " (s/n): ");

            String s = scanner.nextLine().trim().toLowerCase();

            if (s.equals("s")) {
                return true;
            }

            if (s.equals("n")) {
                return false;
            }

            System.out.println("Responde s o n.");
        }
    }
}