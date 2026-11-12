package persistencias;
import club.Club;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.PrintWriter;
import java.io.FileWriter;

import java.io.IOException;

import Usuarios.*;
import Excepciones.*;

public class PersistenciaUsuarios {
    private static final String ARCHIVO = "usuarios.txt";
    private static final String SEPARADOR = ";";

    public PersistenciaUsuarios() {
    }

    public void cargarUsuarios(Club seneca, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta + "/" + ARCHIVO;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))){
            String linea = "";
            while ((linea = reader.readLine()) != null){
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")){
                    continue;
                }
                String[] partes = linea.split(SEPARADOR);
                String tipoUsuario = partes[0];
                String login = partes[1];
                String password = partes[2];
                if (tipoUsuario.equals("SOCIO")){
                    int edad = Integer.parseInt(partes[3]);
                    boolean aptoFisico = Boolean.parseBoolean(partes[4]);
                    int puntosFidelidad = Integer.parseInt(partes[5]);

                    Socio nuevoSocio = new Socio(login, password, edad);
                    // se agregaron los datos faltantes para guardar a socio
                    nuevoSocio.setFisicoApto (aptoFisico);
                    nuevoSocio.setPuntosFidelidad (puntosFidelidad);
    
                    seneca.agregarUsuario(nuevoSocio);
                }
                else if (tipoUsuario.equals("ENTRENADOR")){
                    String codigoDescuento = partes[3];
                    Entrenador nuevoEntrenador = new Entrenador(login, password, codigoDescuento);
                    // complemento de funcion para persistencia usuario entrenador 
                    if (partes.length > 4){
                        nuevoEntrenador.setPuntosFidelidad(Integer.parseInt(partes[4]));
                    }
                    seneca.agregarUsuario(nuevoEntrenador);
                }
                else if (tipoUsuario.equals("FISIOTERAPEUTA")){
                    String codigoDescuento = partes[3];
                    Fisioterapeuta nuevoFisio = new Fisioterapeuta(login, password, codigoDescuento);
                    // complemento de funcion para persistencia usuario entrenador 
                    if (partes.length > 4){
                        nuevoFisio.setPuntosFidelidad(Integer.parseInt(partes[4]));
                    }
                    seneca.agregarUsuario(nuevoFisio);
                }
                else if (tipoUsuario.equals("ADMIN")){
                    Administrador nuevoAdmin = new Administrador(login, password);
                    seneca.agregarUsuario(nuevoAdmin);
                }
                else{
                    throw new PersistenciaException("No se pudo leer el archivo");
                }
            }
        }
    }
    public void guardarUsuarios(Club seneca, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta + "/" + ARCHIVO;
        try(PrintWriter escritor = new PrintWriter(new FileWriter(ruta))){
            for (Usuario usuario : seneca.getUsuarios()){
                if (usuario instanceof Socio){
                    Socio s = (Socio) usuario;
                    String linea = "SOCIO" + SEPARADOR + s.getLogin() + SEPARADOR + s.getContraseña() + SEPARADOR + s.getEdad()  + SEPARADOR + s.isFisicoApto()  + SEPARADOR + s.getPuntosFidelidad();
                    escritor.println(linea);
                }
                else if (usuario instanceof Entrenador){
                    Entrenador entren = (Entrenador) usuario;
                    String linea = "ENTRENADOR"+ SEPARADOR + entren.getLogin() + SEPARADOR + entren.getContraseña() + SEPARADOR + entren.getCodigoDescuento() + SEPARADOR + entren.getPuntosFidelidad();
                    escritor.println(linea);
                }
                else if (usuario instanceof Fisioterapeuta){
                    Fisioterapeuta fisio = (Fisioterapeuta) usuario;
                    String linea = "FISIOTERAPEUTA" + SEPARADOR + fisio.getLogin() + SEPARADOR + fisio.getContraseña() + SEPARADOR + fisio.getCodigoDescuento() + SEPARADOR + fisio.getPuntosFidelidad();
                    escritor.println(linea);
                }
                else if (usuario instanceof Administrador){
                    Administrador admin = (Administrador) usuario;
                    String linea = "ADMIN" + SEPARADOR + admin.getLogin()+ SEPARADOR + admin.getContraseña();
                    escritor.println(linea);
                }
                else{
                    throw new PersistenciaException("Usuario no soportado: "+ usuario.getLogin());
                }
            }
        }
            catch (IOException e){
                throw new PersistenciaException("No se pudo escribir "+ ruta + ". Error: "+ e);
            }
        }
    }

    


