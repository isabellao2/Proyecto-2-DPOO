
package persistencias;
import club.Club;
import java.io.BufferedReader;
import java.io.FileReader;
import TiendaCafeteria.*;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.FileWriter;

import java.io.IOException;

import Usuarios.*;
import Excepciones.*;

public class PersistenciaTiendaCafeteria {

    private static final String ARCHIVO_ARTICULOS = "articulos.txt";
    private static final String ARCHIVO_BEBIDAS = "bebidas.txt";
    private static final String ARCHIVO_SNACKS = "snacks.txt";
    private static final String ARCHIVO_VENTAS = "ventas.txt";
    private static final String SEPARADOR = ";";

    public PersistenciaTiendaCafeteria() {
    }

    public void cargarTiendaCafeteria(Club seneca, String carpeta) throws IOException, PersistenciaException{
        Cafeteria cafe = new Cafeteria();
        TiendaDeportiva tienda = new TiendaDeportiva();

        cargarArticulos(tienda, carpeta);
        cargarBebidas(cafe, carpeta);
        cargarSnacks(cafe, carpeta);
        seneca.setTienda(tienda);
        seneca.setCafeteria(cafe);
        cargarVentas(seneca, carpeta);

    }

    private void cargarBebidas(Cafeteria cafeteria, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta +"/" + ARCHIVO_BEBIDAS;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))){
            String linea;
            while ((linea = reader.readLine()) != null){
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")){
                    continue;}
                String[] partes = linea.split(SEPARADOR);
                String nombre = partes[0];
                boolean caliente = Boolean.parseBoolean(partes[1]);
                double precio = Double.parseDouble(partes[2]);
                Bebida nuevaBebida = new Bebida(nombre, precio, caliente);
                cafeteria.agregarBebida(nuevaBebida);
                }
            }
        
        catch(IOException e){
            throw new PersistenciaException("No se pudo cargar el archivo. Error: "+ e);
        }
    }
    private void cargarArticulos(TiendaDeportiva tienda, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta +"/" + ARCHIVO_ARTICULOS;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))){
            String linea;
            while ((linea = reader.readLine()) != null){
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")){
                    continue;}
                String[] partes = linea.split(SEPARADOR);  
                String nombre = partes[0];
                String categoria = partes[1];
                double precio = Double.parseDouble(partes[2]);
                int cantidad = Integer.parseInt(partes[3]);
                Articulo nuevoArticulo = new Articulo(nombre, categoria, precio, cantidad);
                if (partes.length > 4) {
                    nuevoArticulo.mover(partes[4]);
                }
                tienda.agregarArticulo(nuevoArticulo);
            }
        }
        catch(IOException e){
            throw new PersistenciaException("No se pudo cargar el archivo. Error: "+ e);
        }
    }
    private void cargarSnacks(Cafeteria cafeteria, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta +"/" + ARCHIVO_SNACKS;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))){
            String linea;
            while ((linea = reader.readLine()) != null){
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")){
                    continue;}
                String[] partes = linea.split(SEPARADOR); 
                String nombre = partes[0];
                double precio = Double.parseDouble(partes[1]);
                Snack nuevoSnack = new Snack(nombre, precio);
                String alergeno = partes[2];
                if (!alergeno.equals("NINGUNO")){
                    String[] alergenos = alergeno.split(",");
                    for (String a : alergenos){
                        a =  a.trim();
                        nuevoSnack.agregarAlergeno(a);
                    }
                }
                cafeteria.agregarSnack(nuevoSnack);
                }

            }
            catch(IOException e){
                throw new PersistenciaException("No se pudo cargar el archivo. Error: "+ e);
            }
        }
    private void cargarVentas(Club seneca, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta +"/" + ARCHIVO_VENTAS;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))){
            String linea;
            while ((linea = reader.readLine()) != null){
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")){
                    continue;}
                String[] partes = linea.split(SEPARADOR);
                String loginComprador = partes[0];
                String rubro = partes[1];
                LocalDate fecha = LocalDate.parse(partes[2].trim().substring(0, 10));
                double valor = Double.parseDouble(partes[3]);
                double impuesto = Double.parseDouble(partes[4]);
                double propina = Double.parseDouble(partes[5]);
                double descuento = Double.parseDouble(partes[6]);
                int puntosRedimidos = Integer.parseInt(partes[7]);
                Usuario comprador = seneca.buscarUsuario(loginComprador);
                Venta nuevaVenta = new Venta(fecha, rubro, valor, descuento, impuesto, propina, comprador);
                seneca.agregarVenta(nuevaVenta);

                }
            }
            catch(IOException e){
                throw new PersistenciaException("No se pudo cargar el archivo. Error: "+ e);}
        }
    public void guardarTiendaCafeteria(Club seneca, String carpeta) throws IOException, PersistenciaException {
        Cafeteria cafe = seneca.getCafeteria();
        TiendaDeportiva tienda = seneca.getTienda();

        guardarArticulos(tienda, carpeta);
        guardarBebidas(cafe, carpeta);
        guardarSnacks(cafe, carpeta);
        guardarVentas(seneca, carpeta);
    }
    
    private void guardarBebidas(Cafeteria cafe, String carpeta) throws IOException, PersistenciaException {
        String ruta = carpeta + "/" + ARCHIVO_BEBIDAS;
        try(PrintWriter escritor = new PrintWriter(new FileWriter(ruta))){ 
            for (Bebida b : cafe.getBebidas()){
                String linea = b.getNombre() + SEPARADOR + b.isCaliente()+ SEPARADOR + b.getPrecio();
                escritor.println(linea);
            }
        }
        catch(IOException e){
            throw new PersistenciaException("El archivo no se pudo cargar. Error: " + e);
        }

    }
    private void guardarArticulos(TiendaDeportiva tienda, String carpeta) throws IOException, PersistenciaException {
        String ruta = carpeta + "/" + ARCHIVO_ARTICULOS;
        try(PrintWriter escritor = new PrintWriter(new FileWriter(ruta))){ 
            for (Articulo a : tienda.getArticulosDisponibles()){
                String linea = a.getNombre() + SEPARADOR + a.getCategoria() + SEPARADOR + a.getPrecio() + SEPARADOR + a.getCantidadDisponible() + SEPARADOR + a.getUbicacion();
                escritor.println(linea);
            }
        }
        catch(IOException e){
            throw new PersistenciaException("El archivo no se pudo cargar. Error: " + e);
        }
    }
    private void guardarSnacks(Cafeteria cafe, String carpeta) throws IOException, PersistenciaException {
        String COMA = ",";
        String ruta = carpeta + "/" + ARCHIVO_SNACKS;
        try(PrintWriter escritor = new PrintWriter(new FileWriter(ruta))){
            for (Snack s : cafe.getSnacks()){
                String linea = s.getNombre() + SEPARADOR + s.getPrecio() + SEPARADOR;
                if (s.getAlergenos().isEmpty()){
                    linea += "NINGUNO";
                }
                else{
                    for (String alergeno : s.getAlergenos()){
                        linea += alergeno + COMA;
                    }
                }
                escritor.println(linea);
            }
        } 
        catch(IOException e){
            throw new PersistenciaException("El archivo no se pudo cargar. Error: " + e);}
    }
    private void guardarVentas(Club seneca, String carpeta) throws IOException, PersistenciaException{
        String ruta = carpeta + "/" + ARCHIVO_VENTAS;
        try(PrintWriter escritor = new PrintWriter(new FileWriter(ruta))){ 
            for (Venta v : seneca.getVentas()){
                String linea = v.getComprador().getLogin() + SEPARADOR + v.getRubro() + SEPARADOR + v.getFecha()
                        + SEPARADOR + v.getValor() + SEPARADOR + v.getImpuesto() + SEPARADOR + v.getPropina()
                        + SEPARADOR + v.getDescuento() + SEPARADOR + "0" + SEPARADOR + "0.0";
                escritor.println(linea);
            }
        }
        catch(IOException e){
            throw new PersistenciaException("El archivo no se pudo cargar. Error: " + e);}
    }

}