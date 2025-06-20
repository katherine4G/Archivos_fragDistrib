package secundario;

import common.FileUtils;
import common.Mensaje;
import common.Protocolo;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
                case Protocolo.DELETE -> eliminarFragmento(mensaje.getNombreArchivo());
                case Protocolo.RENAME -> renombrarFragmento(mensaje.getNombreArchivo());
                case Protocolo.LIST -> enviarListaArchivos(salida);

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
   //
    private void eliminarFragmento(String nombreArchivo) {
        File archivo = new File("fragmentos", nombreArchivo);
        if (archivo.exists()) {
            archivo.delete();
            System.out.println("Fragmento eliminado: " + nombreArchivo);
        } else {
            System.out.println("Fragmento no encontrado para eliminar: " + nombreArchivo);
        }
    }
    private void renombrarFragmento(String nombres) {
        String[] partes = nombres.split("\\|");
        File viejo = new File("fragmentos", partes[0]);
        File nuevo = new File("fragmentos", partes[1]);
        if (viejo.exists()) {
            viejo.renameTo(nuevo);
            System.out.println("Fragmento renombrado.");
        }
    }
    private void enviarListaArchivos(ObjectOutputStream salida) {
        File carpeta = new File("fragmentos");
        File[] archivos = carpeta.listFiles();

        List<String> nombres = new ArrayList<>();
        if (archivos != null) {
            for (File archivo : archivos) {
                nombres.add(archivo.getName());
            }
        }

        try {
            String joined = String.join(",", nombres);
            byte[] datos = joined.getBytes();
            Mensaje respuesta = new Mensaje(Protocolo.LIST, null, datos, datos.length);
            salida.writeObject(respuesta);
            salida.flush();
            System.out.println("Lista enviada al maestro: " + joined);
        } catch (IOException e) {
            System.err.println("Error enviando lista de archivos: " + e.getMessage());
        }
    }

}
