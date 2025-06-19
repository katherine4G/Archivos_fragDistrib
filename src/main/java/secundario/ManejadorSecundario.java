package secundario;

import common.FileUtils;
import common.Mensaje;
import common.Protocolo;

import java.io.*;
import java.net.Socket;

public class ManejadorSecundario extends Thread {

    private final Socket socket;

    public ManejadorSecundario(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectOutputStream salida = null;
        ObjectInputStream entrada = null;

        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();

            entrada = new ObjectInputStream(socket.getInputStream());

            System.out.println("Esperando mensaje...");

            Mensaje mensaje = (Mensaje) entrada.readObject();

            switch (mensaje.getCabecera()) {
                case Protocolo.STORE -> guardarFragmento(mensaje);
                case Protocolo.FRAG_REQUEST -> enviarFragmento(mensaje.getNombreArchivo(), salida);
                default -> System.out.println("Cabecera desconocida en secundario.");
            }

        } catch (Exception e) {
            System.err.println("Error en manejo de fragmento: " + e.getMessage());
        } finally {
            try {
                if (entrada != null) entrada.close();
                if (salida != null) salida.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando recursos en secundario: " + e.getMessage());
            }
        }
    }

    private void guardarFragmento(Mensaje msg) {
        try {
            FileUtils.guardarFragmento(msg.getNombreArchivo(), msg.getDatos());
        } catch (IOException e) {
            System.err.println("No se pudo guardar el fragmento: " + e.getMessage());
        }
    }

    private void enviarFragmento(String nombreArchivo, ObjectOutputStream salida) {
        try {
            byte[] datos = FileUtils.leerFragmento(nombreArchivo);

            if (datos.length == 0) {
                System.err.println("Fragmento vacío o no encontrado: " + nombreArchivo);
            }

            Mensaje respuesta = new Mensaje(Protocolo.FRAG_REQUEST, nombreArchivo, datos, datos.length);
            salida.writeObject(respuesta);
            salida.flush();

            System.out.println("Fragmento enviado: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al enviar fragmento: " + e.getMessage());
        }
    }
}
