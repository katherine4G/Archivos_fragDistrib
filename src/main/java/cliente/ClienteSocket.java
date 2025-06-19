package cliente;

import common.Constantes;
import common.Mensaje;
import common.Protocolo;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClienteSocket {

    public void enviarArchivo(String nombreArchivo, byte[] datos) throws IOException {
        try (
            Socket socket = new Socket(Constantes.IP_MAESTRO, Constantes.PUERTO_MAESTRO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Mensaje mensaje = new Mensaje(Protocolo.UPLOAD, nombreArchivo, datos, datos.length);
            out.writeObject(mensaje);
            out.flush();
        }
    }

    public byte[] descargarArchivo(String nombreArchivo) throws IOException, ClassNotFoundException {
        try (
            Socket socket = new Socket(Constantes.IP_MAESTRO, Constantes.PUERTO_MAESTRO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Mensaje solicitud = new Mensaje(Protocolo.DOWNLOAD, nombreArchivo, null, 0);
            out.writeObject(solicitud);
            out.flush();

            Mensaje respuesta = (Mensaje) in.readObject();
            return respuesta.getDatos();
        }
    }

    public void eliminarArchivo(String nombreArchivo) throws IOException {
        try (
            Socket socket = new Socket(Constantes.IP_MAESTRO, Constantes.PUERTO_MAESTRO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Mensaje msg = new Mensaje(Protocolo.DELETE, nombreArchivo, null, 0);
            out.writeObject(msg);
            out.flush();
        }
    }

    public void renombrarArchivo(String nombreActual, String nuevoNombre) throws IOException {
        try (
            Socket socket = new Socket(Constantes.IP_MAESTRO, Constantes.PUERTO_MAESTRO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Mensaje msg = new Mensaje(Protocolo.RENAME, nombreActual + "|" + nuevoNombre, null, 0);
            out.writeObject(msg);
            out.flush();
        }
    }

    public List<String> listarArchivos() throws IOException, ClassNotFoundException {
        try (
            Socket socket = new Socket(Constantes.IP_MAESTRO, Constantes.PUERTO_MAESTRO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Mensaje msg = new Mensaje(Protocolo.LIST, "", null, 0);
            out.writeObject(msg);
            out.flush();

            Mensaje respuesta = (Mensaje) in.readObject();
            String[] nombres = new String(respuesta.getDatos()).split(",");
            return Arrays.asList(nombres);
        }
    }

}