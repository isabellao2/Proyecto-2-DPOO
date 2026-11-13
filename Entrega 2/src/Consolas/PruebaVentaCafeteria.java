package Consolas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DeporteIndividual.DisciplinaIndividual;
import DeporteIndividual.Instalacion;
import DeporteIndividual.Reserva;
import Excepciones.ClubException;
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
                System.out.println("3. Reservar una instalacion (para probar la bebida caliente)");
                System.out.println("4. Ver mi estado");
                System.out.println("0. Salir y guardar");

                int opcion = leerEntero(scanner, "Opcion", 0, 4);

                if (opcion == 1) {
                    mostrarCarta(cafeteria);
                } else if (opcion == 2) {
                    comprar(scanner, cafeteria, socio);
                } else if (opcion == 3) {
                    reservar(scanner, datos, socio);
                } else if (opcion == 4) {
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

    // ---------- Opciones del menu ----------

    private static void mostrarCarta(Cafeteria cafeteria) {
        System.out.println("\nBEBIDAS");
        List<Bebida> bebidas = cafeteria.getBebidas();
        for (int i = 0; i < bebidas.size(); i++) {
            Bebida b = bebidas.get(i);
            System.out.println((i + 1) + ". " + b.getNombre() + " | " + dinero(b.getPrecio())
                    + " | " + (b.isCaliente() ? "caliente" : "fria"));
        }

        System.out.println("\nSNACKS");
        List<Snack> snacks = cafeteria.getSnacks();
        for (int i = 0; i < snacks.size(); i++) {
            Snack s = snacks.get(i);
            System.out.println((i + 1) + ". " + s.getNombre() + " | " + dinero(s.getPrecio())
                    + " | alergenos: " + (s.tieneAlergenos() ? s.getAlergenos().toString() : "ninguno"));
        }
    }

    private static void comprar(Scanner scanner, Cafeteria cafeteria, Socio socio) {

        List<Bebida> catalogoBebidas = cafeteria.getBebidas();
        List<Snack> catalogoSnacks = cafeteria.getSnacks();

        List<Bebida> bebidas = new ArrayList<Bebida>();
        List<Snack> snacks = new ArrayList<Snack>();

        // 1. Armar el pedido
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
                    System.out.println((i + 1) + ". " + b.getNombre() + " | " + dinero(b.getPrecio())
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
                    System.out.println((i + 1) + ". " + s.getNombre() + " | " + dinero(s.getPrecio()));
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

        double subtotal = 0.0;
        for (Bebida b : bebidas) {
            subtotal += b.getPrecio();
        }
        for (Snack s : snacks) {
            subtotal += s.getPrecio();
        }
        System.out.println("\nSubtotal del pedido: " + dinero(subtotal));

        // Propina
        double sugerida = cafeteria.calcularPropinaSugerida(subtotal);
        double propina = leerPropina(scanner, sugerida);

        // Alergenos
        List<String> avisos = cafeteria.informarAlergenos(snacks);
        boolean confirmados = false;
        if (!avisos.isEmpty()) {
            System.out.println("\nALERGENOS");
            for (String aviso : avisos) {
                System.out.println("- " + aviso);
            }
            confirmados = leerSiNo(scanner, "Confirmas que quieres comprar estos snacks?");
        }

        // La compra
        LocalDateTime momento = leerMomento(scanner);

        System.out.println("\nESTADO ANTES");
        mostrarEstado(socio);

        try {
            Venta venta = cafeteria.vender(socio, bebidas, snacks, propina, confirmados, momento);

            System.out.println("\nVENTA ACEPTADA");
            System.out.println("Fecha:     " + venta.getFecha());
            System.out.println("Rubro:     " + venta.getRubro());
            System.out.println("Subtotal:  " + dinero(venta.getValor()));
            System.out.println("Impuesto:  " + dinero(venta.getImpuesto()) + " (8%)");
            System.out.println("Propina:   " + dinero(venta.getPropina()));
            System.out.println("Total:     " + dinero(venta.calcularTotal()));
            System.out.println("Puntos ganados: " + venta.calcularPuntosFidelidad());
        } catch (RuntimeException e) {
            System.out.println("\nVENTA RECHAZADA: " + e.getMessage());
        }

        System.out.println("\nESTADO DESPUES");
        mostrarEstado(socio);
    }

    private static void reservar(Scanner scanner, Club datos, Socio socio) {
        List<Instalacion> instalaciones = new ArrayList<Instalacion>(datos.getInstalaciones());

        System.out.println("\nINSTALACIONES");
        for (int i = 0; i < instalaciones.size(); i++) {
            Instalacion ins = instalaciones.get(i);
            System.out.println((i + 1) + ". " + ins.getNombre() + " | " + ins.getTipo()
                    + (ins.restringeBebidaCaliente() ? " | RESTRINGE bebida caliente" : ""));
        }
        Instalacion instalacion = instalaciones.get(
                leerEntero(scanner, "Instalacion", 1, instalaciones.size()) - 1);

        LocalDate fecha = leerFecha(scanner, "Fecha (AAAA-MM-DD)");
        LocalTime horaInicio = leerHora(scanner, "Hora de inicio (HH:mm)");
        LocalTime horaFin = leerHora(scanner, "Hora de fin (HH:mm)");

        try {
            // La reserva se neceista esta inscrta en la disciplina de la instalacion
            DisciplinaIndividual disciplina = disciplinaDe(datos, instalacion);
            if (disciplina != null && !disciplina.estaInscrito(socio)) {
                socio.inscribirseEnDisciplina(disciplina, "Principiante");
                System.out.println("(Te inscribi en " + disciplina.getNombre() + " nivel Principiante para poder reservar)");
            }

            Reserva r = socio.reservarInstalacion(instalacion, LocalDateTime.of(fecha, horaInicio),
                    LocalDateTime.of(fecha, horaFin), 2, "SENCILLOS");
            System.out.println("RESERVA APROBADA: " + r.getInstalacion().getNombre()
                    + " de " + r.getInicio() + " a " + r.getFin());
        } catch (ClubException e) {
            System.out.println("RESERVA RECHAZADA: " + e.getMessage());
        }
    }

    private static void mostrarEstado(Socio socio) {
        System.out.println("Puntos de fidelidad: " + socio.getPuntosFidelidad());
        System.out.println("Ultima bebida caliente: "
                + (socio.getUltimaBebidaCaliente() == null ? "ninguna" : socio.getUltimaBebidaCaliente()));
        if (socio.getReservas().isEmpty()) {
            System.out.println("Reservas: (ninguna)");
        }
        for (Reserva r : socio.getReservas()) {
            System.out.println("Reserva: " + r.getInstalacion().getNombre() + " de "
                    + r.getInicio() + " a " + r.getFin());
        }
    }

    
    private static DisciplinaIndividual disciplinaDe(Club datos, Instalacion ins) {
        for (DisciplinaIndividual d : datos.getDisciplinas()) {
            if (d.tieneInstalacion(ins)) {
                return d;
            }
        }
        return null;
    }

    private static String dinero(double valor) {
        return "$" + String.format("%.2f", valor);
    }

    /** propina sugerida. Se acepta cualquier numero. */
    private static double leerPropina(Scanner scanner, double sugerida) {
        while (true) {
            System.out.print("Propina (Enter = sugerida " + dinero(sugerida) + "): ");
            String texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                return sugerida;
            }
            try {
                return Double.parseDouble(texto.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Escribe un numero.");
            }
        }
    }

    
    private static LocalDateTime leerMomento(Scanner scanner) {
        while (true) {
            System.out.print("Fecha y hora de la compra (AAAA-MM-DD HH:mm, Enter = ahora): ");
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

    private static LocalDate leerFecha(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Fecha incorrecta.");
            }
        }
    }

    private static LocalTime leerHora(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            try {
                return LocalTime.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Hora incorrecta.");
            }
        }
    }
}