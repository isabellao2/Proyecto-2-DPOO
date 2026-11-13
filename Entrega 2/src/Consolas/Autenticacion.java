package Consolas;

import Usuarios.Usuario;
import club.Club;

public class Autenticacion {
    private Autenticacion(){
    }

public static <T extends Usuario> T iniciarSesion(Club club, Entrada entrada, Class<T> rol, int maxIntentos) {
        for (int i = 0; i < maxIntentos; i++) {
            String login = entrada.leerTexto("Login: ");
            String contrasena = entrada.leerTexto("Contraseña: ");
            Usuario usuario = club.autenticar(login, contrasena);
            if (usuario == null) {
                System.out.println("Login o contraseña incorrectos.");
            } else if (!rol.isInstance(usuario)) {
                System.out.println("Este usuario no puede entrar a esta consola.");
            } else {
                return rol.cast(usuario);
            }
        }
        System.out.println("Demasiados intentos. Intenta luego.");
        return null;
    }
}