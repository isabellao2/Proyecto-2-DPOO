package Consolas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Entrada {

    private Scanner scanner = new Scanner(System.in);

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("No puede estar vacío. Intenta de nuevo.");
        }
    }


    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= min && numero <= max) {
                    return numero;
                }
                System.out.println("Escribe un número entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Eso no es un número entero.");
            }
        }
    }


    public double leerDecimal(String mensaje, double min) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            try {
                double numero = Double.parseDouble(texto);
                if (numero >= min) {
                    return numero;
                }
                System.out.println("El valor debe ser al menos " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("Eso no es un número.");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String texto = scanner.nextLine().trim().toLowerCase();
            if (texto.equals("s")) {
                return true;
            }
            if (texto.equals("n")) {
                return false;
            }
            System.out.println("Responde s o n.");
        }
    }

    
    public LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (AAAA-MM-DD): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Fecha no válida. Ejemplo: 2026-11-20");
            }
        }
    }

  
    public LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (AAAA-MM-DDTHH:MM): ");
            try {
                return LocalDateTime.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Fecha y hora no válidas. Ejemplo: 2026-11-20T15:30");
            }
        }
    }
}