package cliente;
import common.Mensaje;
import common.Protocolo;
import common.Constantes;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author kathe
 */
public class ClienteSocket {
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    public ClienteSocket() throws IOException {
        socket = new Socket(Constantes.IP_MAESTRO, Constantes.PUERTO_MAESTRO);
        salida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public void enviarArchivo(String nombreArchivo, byte[] datos) throws IOException {
        Mensaje mensaje = new Mensaje(Protocolo.UPLOAD, nombreArchivo, datos, datos.length);
        salida.writeObject(mensaje);
        salida.flush();
    }

    public byte[] descargarArchivo(String nombreArchivo) throws IOException, ClassNotFoundException {
        Mensaje solicitud = new Mensaje(Protocolo.DOWNLOAD, nombreArchivo, null, 0);
        salida.writeObject(solicitud);
        salida.flush();

        Mensaje respuesta = (Mensaje) entrada.readObject();
        return respuesta.getDatos();
    }

    public void cerrarConexion() {
        try {
            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar conexión: " + e.getMessage());
        }
    }
}
