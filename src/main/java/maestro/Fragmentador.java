package maestro;

import common.Constantes;
import common.Mensaje;
import common.Protocolo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        System.out.println("➤ Enviando " + nombreParte + " a " + ip + ":" + puerto);
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
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Mensaje solicitud = new Mensaje(Protocolo.FRAG_REQUEST, nombreParte, null, 0);
        out.writeObject(solicitud);
        out.flush();

        Mensaje respuesta = (Mensaje) in.readObject();
        byte[] datos = respuesta.getDatos();

        in.close();
        out.close();
        socket.close();

        return datos;
    }

    public void eliminarFragmentos(String nombreArchivo) {
        try {
            eliminarEn(Constantes.IP_SEC1, Constantes.PUERTO_SEC1, "parte1_" + nombreArchivo);
            eliminarEn(Constantes.IP_SEC2, Constantes.PUERTO_SEC2, "parte2_" + nombreArchivo);
            eliminarEn(Constantes.IP_SEC3, Constantes.PUERTO_SEC3, "parte3_" + nombreArchivo);
        } catch (Exception e) {
            System.err.println("Error al eliminar fragmentos: " + e.getMessage());
        }
    }

    private void eliminarEn(String ip, int puerto, String nombreParte) throws Exception {
        Socket socket = new Socket(ip, puerto);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Mensaje mensaje = new Mensaje(Protocolo.DELETE, nombreParte, null, 0);
        out.writeObject(mensaje);
        out.flush();

        in.close();
        out.close();
        socket.close();
    }

    public void renombrarFragmentos(String actual, String nuevo) {
        try {
            renombrarEn(Constantes.IP_SEC1, Constantes.PUERTO_SEC1, "parte1_" + actual, "parte1_" + nuevo);
            renombrarEn(Constantes.IP_SEC2, Constantes.PUERTO_SEC2, "parte2_" + actual, "parte2_" + nuevo);
            renombrarEn(Constantes.IP_SEC3, Constantes.PUERTO_SEC3, "parte3_" + actual, "parte3_" + nuevo);
        } catch (Exception e) {
            System.err.println("Error renombrando: " + e.getMessage());
        }
    }

    private void renombrarEn(String ip, int puerto, String nombreViejo, String nombreNuevo) throws Exception {
        Socket socket = new Socket(ip, puerto);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Mensaje msg = new Mensaje(Protocolo.RENAME, nombreViejo + "|" + nombreNuevo, null, 0);
        out.writeObject(msg);
        out.flush();

        in.close();
        out.close();
        socket.close();
    }

    private List<String> obtenerDesdeNodo(String ip, int puerto) throws Exception {
        System.out.println("Conectando a nodo " + ip + ":" + puerto);
        Socket socket = new Socket(ip, puerto);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        Mensaje solicitud = new Mensaje(Protocolo.LIST, "", null, 0);
        out.writeObject(solicitud);
        out.flush();

        Mensaje respuesta = (Mensaje) in.readObject();
        String[] fragmentos = new String(respuesta.getDatos()).split(",");

        in.close();
        out.close();
        socket.close();

        System.out.println("Respuesta recibida del nodo " + puerto);
        return List.of(fragmentos);
    }

    public List<String> obtenerListaArchivos() {
        Map<String, Integer> contadorPartes = new HashMap<>();

        for (int i = 1; i <= 3; i++) {
            String ip = switch (i) {
                case 1 -> Constantes.IP_SEC1;
                case 2 -> Constantes.IP_SEC2;
                case 3 -> Constantes.IP_SEC3;
                default -> "";
            };
            int puerto = switch (i) {
                case 1 -> Constantes.PUERTO_SEC1;
                case 2 -> Constantes.PUERTO_SEC2;
                case 3 -> Constantes.PUERTO_SEC3;
                default -> 0;
            };

            try {
                List<String> fragmentos = obtenerDesdeNodo(ip, puerto);
                contarPartes(contadorPartes, fragmentos);
            } catch (Exception e) {
                System.err.println("Nodo " + puerto + " no respondió: " + e.getMessage());
            }
        }

        List<String> archivos = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : contadorPartes.entrySet()) {
            archivos.add(entry.getKey());
        }
        return archivos;
    }

    private void contarPartes(Map<String, Integer> mapa, List<String> fragmentos) {
        for (String frag : fragmentos) {
            if (frag == null || frag.isBlank()) continue;
            String base = removerPrefijo(frag);
            if (base != null) {
                mapa.put(base, mapa.getOrDefault(base, 0) + 1);
            }
        }
    }

    private String removerPrefijo(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;
        if (nombre.startsWith("parte1_"))
            return nombre.substring("parte1_".length());
        if (nombre.startsWith("parte2_"))
            return nombre.substring("parte2_".length());
        if (nombre.startsWith("parte3_"))
            return nombre.substring("parte3_".length());
        return null;
    }
}
