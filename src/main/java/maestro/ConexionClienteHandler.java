package maestro;

import common.Mensaje;
import common.Protocolo;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConexionClienteHandler extends Thread {

    private final Socket socketCliente;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;

    public ConexionClienteHandler(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            entrada = new ObjectInputStream(socketCliente.getInputStream());
            salida = new ObjectOutputStream(socketCliente.getOutputStream());

            // Esperar un mensaje del cliente
            Mensaje mensaje = (Mensaje) entrada.readObject();
            switch (mensaje.getCabecera()) {
                case Protocolo.UPLOAD -> manejarUpload(mensaje);
                case Protocolo.DOWNLOAD -> manejarDownload(mensaje);
                case Protocolo.DELETE -> manejarDelete(mensaje);
                case Protocolo.RENAME -> manejarRename(mensaje);
                case Protocolo.LIST -> manejarList();

                default -> System.out.println("Cabecera desconocida: " + mensaje.getCabecera());
            }

        } catch (Exception e) {
            System.err.println("Error manejando cliente: " + e.getMessage());
        } finally {
            try {
                if (entrada != null) entrada.close();
                if (salida != null) salida.close();
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }

    private void manejarUpload(Mensaje mensaje) {
        System.out.println("Recibiendo archivo: " + mensaje.getNombreArchivo());
        byte[] datos = mensaje.getDatos();

        // Fragmentar
        Fragmentador fragmentador = new Fragmentador();
        byte[][] partes = fragmentador.dividirArchivo(datos);

        fragmentador.enviarFragmentos(mensaje.getNombreArchivo(), partes);
    }

    private void manejarDownload(Mensaje mensaje) {
        System.out.println("Cliente solicita descarga: " + mensaje.getNombreArchivo());

        Fragmentador fragmentador = new Fragmentador();
        byte[] archivoReensamblado = fragmentador.reensamblarArchivo(mensaje.getNombreArchivo());

        try {
            Mensaje respuesta = new Mensaje(Protocolo.DOWNLOAD, mensaje.getNombreArchivo(), archivoReensamblado, archivoReensamblado.length);
            salida.writeObject(respuesta);
            salida.flush();
        } catch (IOException e) {
            System.err.println("Error enviando archivo al cliente: " + e.getMessage());
        }
    }
    private void manejarDelete(Mensaje mensaje) {
        Fragmentador fragmentador = new Fragmentador();
        fragmentador.eliminarFragmentos(mensaje.getNombreArchivo());
    }
    private void manejarRename(Mensaje mensaje) {
        String[] partes = mensaje.getNombreArchivo().split("\\|");
        String actual = partes[0];
        String nuevo = partes[1];

        Fragmentador fragmentador = new Fragmentador();
        fragmentador.renombrarFragmentos(actual, nuevo);
    }
    
    private void manejarList() {
        Fragmentador fragmentador = new Fragmentador();
        List<String> archivos = fragmentador.obtenerListaArchivos();

        try {
            String unidos = String.join(",", archivos);
            byte[] datos = unidos.getBytes();

            Mensaje respuesta = new Mensaje(Protocolo.LIST, null, datos, datos.length);
            salida.writeObject(respuesta);
            salida.flush();
        } catch (IOException e) {
            System.err.println("Error al enviar lista: " + e.getMessage());
        }
    }


}