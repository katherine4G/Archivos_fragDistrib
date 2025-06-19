package maestro;

import common.Constantes;
import common.Mensaje;
import common.Protocolo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 *
 * @author kathe
 */

public class Fragmentador {

    public byte[][] dividirArchivo(byte[] archivo) {
        int total = archivo.length;
        int parteSize = total / 3;

        byte[][] partes = new byte[3][];

        partes[0] = new byte[parteSize];
        partes[1] = new byte[parteSize];
        partes[2] = new byte[total - parteSize * 2];
        System.arraycopy(archivo, 0, partes[0], 0, parteSize);
        System.arraycopy(archivo, parteSize, partes[1], 0, parteSize);
        System.arraycopy(archivo, parteSize * 2, partes[2], 0, partes[2].length);

        return partes;
    }

    public void enviarFragmentos(String nombreArchivo, byte[][] partes) {
        try {
            enviarA(Constantes.IP_SEC1, Constantes.PUERTO_SEC1, "parte1_" + nombreArchivo, partes[0]);
            enviarA(Constantes.IP_SEC2, Constantes.PUERTO_SEC2, "parte2_" + nombreArchivo, partes[1]);
            enviarA(Constantes.IP_SEC3, Constantes.PUERTO_SEC3, "parte3_" + nombreArchivo, partes[2]);
        } catch (Exception e) {
            System.err.println("Error al enviar fragmentos: " + e.getMessage());
        }
    }

    private void enviarA(String ip, int puerto, String nombreParte, byte[] datos) throws Exception {
        Socket socket = new Socket(ip, puerto);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); 

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Mensaje msg = new Mensaje(Protocolo.STORE, nombreParte, datos, datos.length);
        out.writeObject(msg);
        out.flush();

        out.close();
        in.close();
        socket.close();
    }


    public byte[] reensamblarArchivo(String nombreArchivo) {
        try {
            byte[] parte1 = pedirFragmento(Constantes.IP_SEC1, Constantes.PUERTO_SEC1, "parte1_" + nombreArchivo);
            byte[] parte2 = pedirFragmento(Constantes.IP_SEC2, Constantes.PUERTO_SEC2, "parte2_" + nombreArchivo);
            byte[] parte3 = pedirFragmento(Constantes.IP_SEC3, Constantes.PUERTO_SEC3, "parte3_" + nombreArchivo);

            // Unir todos los fragmentos
            byte[] completo = new byte[parte1.length + parte2.length + parte3.length];
            System.arraycopy(parte1, 0, completo, 0, parte1.length);
            System.arraycopy(parte2, 0, completo, parte1.length, parte2.length);
            System.arraycopy(parte3, 0, completo, parte1.length + parte2.length, parte3.length);

            return completo;

        } catch (Exception e) {
            System.err.println("Error al reensamblar archivo: " + e.getMessage());
            return null;
        }
    }

    private byte[] pedirFragmento(String ip, int puerto, String nombreParte) throws Exception {
        Socket socket = new Socket(ip, puerto);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); // handshake con el secundario

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Enviar solicitud
        Mensaje solicitud = new Mensaje(Protocolo.FRAG_REQUEST, nombreParte, null, 0);
        out.writeObject(solicitud);
        out.flush();

        // Leer respuesta
        Mensaje respuesta = (Mensaje) in.readObject();
        byte[] datos = respuesta.getDatos();

        in.close();
        out.close();
        socket.close();

        return datos;
    }

}
